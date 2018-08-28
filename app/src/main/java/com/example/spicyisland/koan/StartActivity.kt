package com.example.spicyisland.koan

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_start)

        KoanService().getKoanCookiesObservableCallable().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Map<String, String>> {
                    override fun onComplete() {
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                    }

                    override fun onSubscribe(d: Disposable) { return }

                    override fun onNext(cookies: Map<String, String>) {
                        koanCookies = cookies
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
    }
}
