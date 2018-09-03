package com.example.spicyisland.koan

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

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
