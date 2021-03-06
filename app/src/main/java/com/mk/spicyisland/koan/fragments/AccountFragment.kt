package com.mk.spicyisland.koan.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_account.*
import androidx.appcompat.app.AlertDialog
import com.mk.spicyisland.koan.activities.FaqActivity
import com.mk.spicyisland.koan.activities.LoginActivity
import com.mk.spicyisland.koan.R
import com.mk.spicyisland.koan.models.User

/**
 * アカウント画面
 * TODO: フィードバックボタンとよくある質問画面の作成(MainActivityの子アクティビティ)
 */
class AccountFragment : Fragment() {

    /**
     * レイアウトのインフレート
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    /**
     * ボタンのonClickListenerの設定
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logOutButton.setOnClickListener {
            createLogoutDialog()
        }

        feedbackButton.setOnClickListener{
            sendFeedback()
        }

        FAQButton.setOnClickListener{
            openFaqActivity()
        }
    }

    /**
     * クッキーとrealmのユーザーオブジェクトを削除して
     * TODO: StartActivityを削除してLoginActivityにする
     * ログインアクティビティを起動し、メインアクティビティを終了
     */
    private fun logout(){
        removeAllCookies()
        removeAllRealmUserObject()
        startActivity(Intent(this.context, LoginActivity::class.java))
        activity!!.finish()
    }

    /**
     * realmからユーザークラスのデータをすべて削除するメソッド
     * TODO: 使いまわせるように、一つのクラスにまとめる
     * TODO: 非同期で使うかもしれないので変数はメソッドの中で宣言する
     */
    private fun removeAllRealmUserObject(){
        val realm = Realm.getDefaultInstance()
        val oldUser = realm.where(User::class.java).findAll()
        realm.beginTransaction()
        oldUser.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    /**
     * このアプリによるクッキーをすべて削除するメソッド
     * TODO: 使いまわせるように一つのクラスにすべてまとめる
     * TODO: 非同期で使うかもしれないので変数はメソッドの中で宣言する
     */
    private fun removeAllCookies() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
    }

    /**
     * ダイヤログを作成するメソッド
     * 連打防止のためにボタンを無効化してからダイヤログを作成し、ダイヤログがなくなるときにボタンを有効化
     */
    private fun createLogoutDialog(){

        logOutButton.isEnabled = false

        AlertDialog.Builder(this.context!!)
                .setTitle(R.string.logout_conformation_title)
                .setMessage(R.string.logout_confirmation_message)
                .setPositiveButton("OK"){ _, _ ->
                    logout()
                }
                .setNegativeButton("Cancel"){_, _ ->
                    logOutButton.isEnabled = true
                }
                .setOnCancelListener{
                    logOutButton.isEnabled = true
                }
                .show()
    }

    private fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", getString(R.string.email_address), null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
        startActivity(Intent.createChooser(emailIntent, "Send Feedback:"))
    }

    private fun openFaqActivity() {
        val intent = Intent(this.context, FaqActivity::class.java)
        startActivity(intent)
    }
}
