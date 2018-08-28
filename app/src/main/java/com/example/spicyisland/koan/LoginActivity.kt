package com.example.spicyisland.koan

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

/**
 * TODO: ログイン画面の見た目を良くする、ロゴを貼る
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener{
            
            hideKeyboard()
            if (koanID.text.length == 8 && password.text.length >= 8){

                KoanService().checkIDAndPass(koanID.text.toString(), password.text.toString())
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Unit>{
                            override fun onComplete() {
                                startActivity(Intent(applicationContext, StartActivity::class.java))
                                finish()
                            }

                            override fun onSubscribe(d: Disposable) {
                                //TODO: プログレスバーを回す
                            }

                            override fun onNext(t: Unit) {
                                getSharedPreferences("UserDataStore", MODE_PRIVATE).edit()
                                        .putString("koanID", koanID.text.toString()).putString("password", password.text.toString()).apply()
                            }

                            override fun onError(e: Throwable) {
                                koanID.text.clear()
                                password.text.clear()
                                showToast()
                                //TODO: プログレスバーを消す
                            }

                        })
            }
            if (koanID.text.length != 8){
                koanID.error = getText(R.string.prompt_ID_error)
            }
            if (password.text.length < 8){
                password.error = getText(R.string.prompt_password_error)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        hideKeyboard()
        return super.onTouchEvent(event)

    }

    private fun showToast() {
        Toast.makeText(applicationContext, R.string.error_incorrect_password_or_ID, Toast.LENGTH_LONG).show()
    }

    private fun hideKeyboard() {
        (applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}
