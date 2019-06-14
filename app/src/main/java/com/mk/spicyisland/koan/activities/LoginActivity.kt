package com.mk.spicyisland.koan.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import com.mk.spicyisland.koan.R
import com.mk.spicyisland.koan.services.KoanService
import com.mk.spicyisland.koan.tools.EnCryptor
import com.mk.spicyisland.koan.models.User

/**
 * ログインアクティビティ
 * もしメインアクティビティでrealmからユーザーオブジェクトの取得に失敗した場合はこのアクティビティに飛ぶ
 */
class LoginActivity : AppCompatActivity() {

    var isSigningIn = false
    private val realm = Realm.getDefaultInstance()
    private val oldUser = realm.where(User::class.java).findAll()
    val enCryptor = EnCryptor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * 見た目の整形
         */
        supportActionBar!!.hide()
        changeStatusBarColorToWhite()
        setContentView(R.layout.activity_login)

        /**
         * サインインボタンが押されたときの処理を登録
         */
        sign_in_button.setOnClickListener{

            /**
             * キーボードを隠す
             */
            hideKeyboard()

            /**
             * パスワードの形式が適切で、ログイン処理実行中でない場合(isSignInがtrueの場合)は、ログイン処理をする(クッキーの取得を試みる)
             */
            if (koanID.text.length == 8 && password.text.length >= 8 && !isSigningIn){

                /**
                 * プログレスバーを表示
                 */
                login_progress.visibility = View.VISIBLE

                /**
                 * idとパスワードからクッキーの取得を試みる
                 * 保存もする
                 */
                KoanService.getKoanCookiesObservableCallable(koanID.text.toString(),
                        password.text.toString(), true)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<Map<String, String>> {

                            /**
                             * すべての処理が完了したらこのアクティビティを終了し、MainActivityを開く
                             */
                            override fun onComplete() {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }

                            /**
                             * 通信を始めたら通信がかぶらないようにisSignInをtrueにする
                             * パスワードとidの入力を受け付けないようにする
                             */
                            override fun onSubscribe(d: Disposable) {
                                isSigningIn = true
                                password.isEnabled = false
                                koanID.isEnabled = false
                            }

                            /**
                             * ここに来たらidとパスワードが正しかったと判断して、もし古いユーザーデータが残っていた場合はそれを削除し、新しくユーザーデータを作成して
                             * idとパスワードを暗号化して保存し、復号化に必要なivも保存する
                             */
                            override fun onNext(cookies: Map<String, String>) {
                                val userData = koanID.text.toString() + password.text.toString()
                                realm.beginTransaction()
                                oldUser.deleteAllFromRealm()
                                val user = realm.createObject(User::class.java)
                                user.userData = enCryptor.encryptText(userData)
                                user.iv = enCryptor.iv
                                realm.commitTransaction()

                            }

                            /**
                             * エラーが起こったらidとパスワードが違うと判断し、エラーを表示してパスワードだけ空欄にして
                             * isSignInをfalseに戻して次のログインを可能にして
                             * idとパスワードの入力を受け付けて
                             * プログレスバーを隠す
                             * TODO: インターネット接続によるエラーかもしれないのでそれを確認する
                             */
                            override fun onError(e: Throwable) {
                                isSigningIn = false
                                password.isEnabled = true
                                koanID.isEnabled = true
                                password.text.clear()
                                showErrorText()
                                login_progress.visibility = View.GONE
                            }

                        })
            }

            /**
             * idとパスワードの形式が違っている場合はエラーを表示
             */
            if (koanID.text.length != 8)
                koanID.error = getText(R.string.prompt_ID_error)
            if (password.text.length < 8)
                password.error = getText(R.string.prompt_password_number_of_characters_error)
        }
    }

    /**
     * 一応realmの破棄
     */
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    /**
     * 画面を触ったらキーボードを隠す
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        hideKeyboard()
        return super.onTouchEvent(event)
    }

    /**
     * エラーのトーストを出す
     */
    private fun showErrorText() {
        Toast.makeText(applicationContext, R.string.error_incorrect_password_or_ID, Toast.LENGTH_LONG).show()
    }

    /**
     * キーボードを隠す
     */
    private fun hideKeyboard() {
        (applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    /**
     * ステータスバーの色を白にして統一感を出す
     */
    private fun changeStatusBarColorToWhite(){
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary)
    }

}
