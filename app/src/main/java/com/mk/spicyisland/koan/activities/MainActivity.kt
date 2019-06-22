package com.mk.spicyisland.koan.activities

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.mk.spicyisland.koan.*
import com.mk.spicyisland.koan.fragments.AccountFragment
import com.mk.spicyisland.koan.fragments.BulletinBoardFragment
import com.mk.spicyisland.koan.fragments.CurriculumFragment
import com.mk.spicyisland.koan.models.ReceivedStuffs
import com.mk.spicyisland.koan.models.User
import com.mk.spicyisland.koan.models.receivedStuffs
import com.mk.spicyisland.koan.services.IsRecovering
import com.mk.spicyisland.koan.services.KoanService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var isOnCreateJustExecuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = getText(R.string.title_curriculum)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_curriculum)

        initContainer()

        /**
         * ReceivedStuffsの初期化
         */
        receivedStuffs = ViewModelProviders.of(this).get(ReceivedStuffs::class.java)

        /**
         * realmの初期化
         */
        val realm = Realm.getDefaultInstance()
        val userData = realm.where(User::class.java).findFirst()

        /**
         * realmにユーザーデータがあった場合はそこから時間割を取ってきて表示
         * データの形式が不正な場合は再ログインを求める
         * なかった場合はログイン画面に飛ばす
         */
        if (userData != null && userData.curriculum.size == 36){
            receivedStuffs.receivedCurriculum.value = userData.curriculum
            recoverAndSubscribeCookies()
            isOnCreateJustExecuted = true
        } else if(userData != null) {
            Toast.makeText(this@MainActivity, R.string.curriculum_not_found, Toast.LENGTH_LONG).show()
            recoverAndSubscribeCookies()
            isOnCreateJustExecuted = true
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_curriculum -> {
                container.setCurrentItem(0, true)
            }
            R.id.nav_bulletin_board -> {
                container.setCurrentItem(1, true)
            }
            R.id.nav_account -> {
                container.setCurrentItem(2, true)
            }
            R.id.nav_share -> {
                /**
                 * TODO: シェアするときの文字を完成させる
                 */
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("text/plain")
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.share_app_subject))
                shareIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_app_body))
                startActivity(Intent.createChooser(shareIntent, getText(R.string.share_this_app)))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initContainer(){
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        nav_view.setCheckedItem(R.id.nav_curriculum)
                        supportActionBar!!.title = getText(R.string.title_curriculum)
                    }

                    1 -> {
                        nav_view.setCheckedItem(R.id.nav_bulletin_board)
                        supportActionBar!!.title = getText(R.string.title_bulletin_board)
                    }

                    2 -> {
                        nav_view.setCheckedItem(R.id.nav_account)
                        supportActionBar!!.title = getText(R.string.title_account)
                    }
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
//            R.id.action_curriculum -> {container.currentItem = 0; return true}
//            R.id.action_notifications -> {container.currentItem = 1; return true}
//            R.id.action_account -> {container.currentItem = 2; return true}
            R.id.action_koan -> {
                startActivity(Intent(this, WebViewActivity::class.java))
            }
            R.id.action_refresh -> {
                if (!IsRecovering.isRecoveringCookies && !IsRecovering.isRecoveringCurriculum && !IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount) {
                    recoverAndSubscribeCookies(true)
                } else {
                    Toast.makeText(this@MainActivity, R.string.auto_refreshing, Toast.LENGTH_LONG).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return CurriculumFragment()
                1 -> return BulletinBoardFragment()
                2 -> return AccountFragment()
            }
            return CurriculumFragment()
        }

        override fun getCount(): Int {
            return 3
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isOnCreateJustExecuted && receivedStuffs.receivedBulletinBoardLinks.value == null) {
            /**
             * クッキーのチェックも兼ねて時間割の取得はonStartで呼ぶ
             * もしクッキーが使えなくなっていたらすべてのデータを再取得する
             * webView側でユーザーによって時間割が開かれたかを検知し、もしそうだった場合時間割のリンクを消すので
             * ここで取り直す
             */
            getAndSubscribeBulletinBoardLinks()
            getAndSaveAndSubscribeCurriculum()
        } else if (!isOnCreateJustExecuted) {
            getAndSaveAndSubscribeCurriculum()
        } else {
            isOnCreateJustExecuted = false
        }
    }

    /**
     * クッキーを再取得する処理、リンクや時間割も同時に取ってくる、基本的にすべてのデータを再取得する
     * 特にリンクはクッキーと噛み合っていないといけないため絶対に一緒に取ってくる
     */
    private fun recoverAndSubscribeCookies(showProgressBar: Boolean = false){
        /**
         * クッキーの再取得と同時にすべてtrueになり、全部完了しないと次のクッキーを取得させない
         * すべてのデータを空にする
         */
        if (!IsRecovering.isRecoveringCookies && !IsRecovering.isRecoveringCurriculum && !IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount) {
            KoanService.recoverCookies()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Map<String, String>> {
                        override fun onComplete() {
                            IsRecovering.isRecoveringCookies = false
                        }

                        override fun onSubscribe(d: Disposable) {
                            /**
                             * 更新中であることを明示
                             * これが全部falseになるまでこのメソッドは使用不能
                             */
                            IsRecovering.isRecoveringCookies = true
                            IsRecovering.isRecoveringCurriculum = true
                            IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount = true

                            /**
                             * 手動で更新した場合はプログレスバーを表示
                             */
                            if (showProgressBar) {
                                try {
                                    mainProgressBar.visibility = View.VISIBLE
                                }catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            /**
                             * 掲示板のデータはクッキーが変わると使用不能になるので
                             * データを空にする処理
                             * リンクと未読の数を消す
                             * 時間割を消すと文字まで消えてしまうので禁止
                             */
                            receivedStuffs.receivedBulletinBoardLinks.value = null
                            receivedStuffs.receivedBulletinBoardUnreadCount.value = null

                        }

                        override fun onNext(t: Map<String, String>) {
                            try {
                                getAndSaveAndSubscribeCurriculum()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                IsRecovering.isRecoveringCurriculum = false
                            }

                            try {
                                getAndSubscribeBulletinBoardLinks()
                            }catch (e: Exception) {
                                e.printStackTrace()
                                IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount = false
                            }

                        }

                        override fun onError(e: Throwable) {
                            /**
                             * ここでエラーになるということは、onNextは呼ばれないので次の通信を可能にするために
                             * すべてのフラッグをfalseに戻さなければならない
                             */
                            IsRecovering.isRecoveringCookies = false
                            IsRecovering.isRecoveringCurriculum = false
                            IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount = false
                            try {
                                /**
                                 * たまにこのトーストが出ないときがあるが理由がよくわからない
                                 */
                                Toast.makeText(this@MainActivity, R.string.recover_cookie_error, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    })
        }
    }

    /**
     * 時間割を取得し、データをrealmとグローバル変数に保存するメソッド
     * onErrorでisRecoveringCookiesをfalseに変えてrecoverCookiesを手順通りに行う処理を書いたメソッドを実行する処理
     * データの保存処理とtry..catchが多い為長い、、、
     */
    private fun getAndSaveAndSubscribeCurriculum() {
        KoanService.setCookieToGlobalAndGetCurriculum()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MutableMap<String, MutableList<String>>> {
                    override fun onComplete() {
                        IsRecovering.isRecoveringCurriculum = false
                        /**
                         * 手動更新した場合はプログレスバーが出ているのでこれで消す
                         * ビューが破棄されている場合は何もしない
                         */
                        if (!IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount) {
                            try {
                                mainProgressBar.visibility = View.INVISIBLE
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    override fun onSubscribe(d: Disposable) {}

                    /**
                     * 取得したデータをrealmListの形に直して保存し、グローバル変数にも入れる
                     */
                    override fun onNext(curriculumAndSyllabusLinks: MutableMap<String, MutableList<String>>) {
                        /**
                         * 非同期処理なのでここでrealm初期化
                         * データの形式をrealmListに変更
                         * 時間割とシラバスのリンクについてやる
                         * リンクをつける時間割を判別するためにavailableCurriculumPositionに有効な時間割の場所を書く
                         * リンクを保存するときは先頭にリンクポジションをつけて保存する
                         * onClickの中で押された時間割と合致するリンクポジションがある場合はそのリンクを開くようにする
                         * TODO: 今はcurriculumSyllabusLinksがないときにはぬるぽが出るが、わんちゃんtry_catch文でエラー処理したほうがいいかもしれない
                         */
                        val realmCurriculum = RealmList<String>()
                        val realmSyllabusLinks = RealmList<String>()
                        val availableCurriculumPositions = RealmList<Int>()
                        val tempRealm = Realm.getDefaultInstance()
                        val userData = tempRealm.where(User::class.java).findFirst()
                        for ((position, curriculumData) in curriculumAndSyllabusLinks["curriculum"]!!.withIndex()) {
                            realmCurriculum.add(curriculumData)
                            //有効だった時間割を記憶するメソッド
                            if (curriculumData != "")
                                availableCurriculumPositions.add(position)
                        }
                        for (syllabusLinks in curriculumAndSyllabusLinks["syllabusLinks"]!!)
                            realmSyllabusLinks.add(syllabusLinks)

                        /**
                         * realmに保存
                         * realmのデータが何らかの原因で消滅または改ざんされていた場合はエラーを表示
                         */
                        try {
                            tempRealm.beginTransaction()
                            userData!!.curriculum = realmCurriculum
                            userData.syllabusLinks = realmSyllabusLinks
                            userData.availableCurriculumPositions = availableCurriculumPositions
                            tempRealm.commitTransaction()
                        }catch (e: Exception){
                            tempRealm.cancelTransaction()
                            /**
                             * アクティビティが破棄されていた場合トーストを表示できないのでエラーのときは何もしない
                             */
                            try {
                                Toast.makeText(this@MainActivity, R.string.realm_error, Toast.LENGTH_LONG).show()
                            }catch (e: Exception){ e.printStackTrace() }
                        }

                        /**
                         * グローバル変数に入れる処理
                         */
                        receivedStuffs.receivedCurriculum.value = curriculumAndSyllabusLinks["curriculum"]
                        receivedStuffs.receivedSyllabusLinks.value = curriculumAndSyllabusLinks["syllabusLinks"]

                        /**
                         * realm閉じる
                         */
                        tempRealm.close()
                    }

                    override fun onError(e: Throwable) {
                        IsRecovering.isRecoveringCurriculum = false
                        /**
                         * 手動更新した場合はプログレスバーが出ているのでこれで消す
                         * ビューが破棄されている場合は何もしない
                         */
                        if (!IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount) {
                            try {
                                mainProgressBar.visibility = View.INVISIBLE
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        /**
                         * 通信に失敗した場合クッキーが無効になったとみなしクッキーの取得からやり直す
                         * クッキーの再取得をする場合はすべてのデータが再取得される
                         * アクティビティが破棄されていた場合メソッドを実行できないので何もしない
                         */
                        try {
                            recoverAndSubscribeCookies()
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }

                })
    }

    /**
     * TODO: onNextでreceivedStuffsにキーをbulletinBoardTextsで文字列を保存する処理
     * TODO: onErrorでisRecoveringCookiesをfalseに変えてrecoverCookiesを手順通りに行う処理を書いたメソッドを実行する処理
     * TODO: ログイン画面に行ってアクティビティが破棄される可能性があるので
     * TODO: アクティビティがアクティビティのインスタンスやメソッドにアクセスする場合はtry..catchで挟む
     */
    private fun getAndSubscribeBulletinBoardLinks(){
        KoanService.getBulletinBoardLinksAndUnreadCount()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MutableMap<String, MutableList<String>>> {
                    override fun onComplete() {
                        IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount = false
                        /**
                         * 手動更新した場合はプログレスバーが出ているのでこれで消す
                         * ビューが破棄されている場合は何もしない
                         */
                        if (!IsRecovering.isRecoveringCurriculum) {
                            try {
                                mainProgressBar.visibility = View.INVISIBLE
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(receivedKoanBulletinBoardLinksAndUnreadCount: MutableMap<String, MutableList<String>>) {
                        /**
                         * 掲示板のリンク(13個)と未読数を取ってくる
                         * 掲示板のリンクの方はKoanService側でやっています
                         */
                        receivedStuffs.receivedBulletinBoardLinks.value =
                                receivedKoanBulletinBoardLinksAndUnreadCount["koanBulletinLinks"]
                        receivedStuffs.receivedBulletinBoardUnreadCount.value =
                                receivedKoanBulletinBoardLinksAndUnreadCount["koanBulletinUnreadCount"]
                    }

                    override fun onError(e: Throwable) {
                        IsRecovering.isRecoveringBulletinBoardLinksAndUnreadCount = false
                        /**
                         * 手動更新した場合はプログレスバーが出ているのでこれで消す
                         * ビューが破棄されている場合は何もしない
                         */
                        if (!IsRecovering.isRecoveringCurriculum) {
                            try {
                                mainProgressBar.visibility = View.INVISIBLE
                            }catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        /**
                         * 通信に失敗した場合クッキーが無効になったとみなしクッキーの取得からやり直す
                         * クッキーの再取得をする場合はすべてのデータが再取得される
                         * アクティビティが破棄されていた場合メソッドを実行できないので何もしない
                         */
                        try {
                            recoverAndSubscribeCookies()
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }

                })
    }
}
