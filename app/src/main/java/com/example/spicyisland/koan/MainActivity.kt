package com.example.spicyisland.koan

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * TODO: 時間割を初めて取得しに行く際に、連打されると固まるのでどうにかする
 * TODO: 時間割取得中はプログレスバーを回す
 * TODO: しばらくするとクッキーが無効になるので、クッキーが無効になった際の処理も書く、バックグラウンドで取得し直すのがいいかもしれない
 */
class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_curriculum -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, CurriculumFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, NotificationFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, AccountFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.elevation = 8f
        supportFragmentManager.beginTransaction().replace(R.id.fragment, CurriculumFragment()).commit()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
