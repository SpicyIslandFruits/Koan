package com.example.spicyisland.koan

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, HomeFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_curriculum -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, CurriculumFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment, HomeFragment()).commit()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        KoanService().getKoanCookiesObservableCallable().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Map<String, String>>{
                    override fun onComplete() {
                        /**
                         * 実際はプログレスバーの収納やk-Animeの起動時のような振る舞いをさせる
                         * 画面の操作を受け付けるようにする
                         */
                        Log.d("d", "onComplete()")
                    }

                    override fun onSubscribe(d: Disposable) {
                        /**
                         * 実際はプログレスバーの表示やk-Animeの起動時のような処理をさせる
                         * ログインが終わる前に画面に触ってほしくないので、k-Animeのように全画面で何か表示する
                         */
                        Log.d("d", "onSubscribe()")
                    }

                    override fun onNext(cookies: Map<String, String>) {
                        koanCookies = cookies

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
    }
}
