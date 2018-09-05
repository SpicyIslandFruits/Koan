package com.example.spicyisland.koan

import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

/**
 * TODO: receivedStuffs = ViewModelProviders.of(this).get(ReceivedStuffs::class.java)を書く
 * TODO: Realmにデータがなければログイン画面、あればそれをreceivedStuffs.receivedStrings.valueに追加する
 * TODO: onCreateViewにisRecoveringCookieがfalseのときにクッキーを取得してから時間割と掲示板のリンクを取得する一連の処理を書く
 * TODO: onStartに時間割と掲示板のリンクを取得する一連の処理を書く
 */
class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Curriculum"
        initContainer()

    }

    private fun initContainer(){
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> supportActionBar!!.title = "Curriculum"
                    1 -> supportActionBar!!.title = "BulletinBoard"
                    2 -> supportActionBar!!.title = "Account"
                }
            }

        })
        container.adapter = mSectionsPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_curriculum -> {container.currentItem = 0; return true}
            R.id.action_notifications -> {container.currentItem = 1; return true}
            R.id.action_account -> {container.currentItem = 2; return true}
            R.id.action_koan -> {
                startActivity(Intent(this, WebViewActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return CurriculumFragment()
                1 -> return NotificationFragment()
                2 -> return AccountFragment()
            }
            return CurriculumFragment()
        }

        override fun getCount(): Int {
            return 3
        }
    }

}
