package com.example.spicyisland.koan

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

/**
 * TODO: このクラスは削除してMainActivityに統合
 */
class StartActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()

        val encryptedUserData = realm.where(User::class.java).findFirst()

        if(encryptedUserData != null) {

            val userData = DeCryptor().decryptData(encryptedUserData.userData, encryptedUserData.iv)

            KoanService.getKoanCookiesObservableCallable(userData.substring(0, 8), userData.substring(8), true)
                    .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Map<String, String>> {
                        override fun onComplete() {
                            return
                        }

                        override fun onSubscribe(d: Disposable) {
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        }

                        override fun onNext(cookies: Map<String, String>) {
                            return
                        }

                        override fun onError(e: Throwable) {
                            //TODO: トーストを出す処理
                            e.printStackTrace()
                        }

                    })

        }else {

            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()

        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
