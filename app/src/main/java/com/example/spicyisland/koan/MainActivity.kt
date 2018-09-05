package com.example.spicyisland.koan

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*

/**
 * TODO: receivedStuffs = ViewModelProviders.of(this).get(ReceivedStuffs::class.java)を書く
 * TODO: Realmにデータがなければログイン画面、あればそれをreceivedStuffs.receivedStrings.valueに追加する
 * TODO: onCreateViewにisRecoveringCookieがfalseのときにクッキーを取得してから時間割と掲示板のリンクを取得する一連の処理を書く
 * TODO: onStartに時間割と掲示板のリンクを取得する処理を書く
 */
class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * 見た目の整形
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "Curriculum"
        initContainer()

        /**
         * ReceivedStuffsの初期化
         */
        receivedStuffs = ViewModelProviders.of(this).get(ReceivedStuffs::class.java)
        receivedStuffs.receivedBulletinBoardLinks.value = mutableListOf()
        receivedStuffs.receivedCurriculum.value = mutableListOf()

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
        } else if(userData != null) {
            Toast.makeText(this@MainActivity, R.string.curriculum_not_found, Toast.LENGTH_LONG).show()
            recoverAndSubscribeCookies()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        getAndSaveAndSubscribeCurriculum()
        getAndSubscribeBulletinBoardLinks()
    }

    /**
     * クッキーを再取得する処理、リンクや時間割も同時に取ってくる、基本的にすべてのデータを再取得する
     * 特にリンクはクッキーと噛み合っていないといけないため絶対に一緒に取ってくる
     */
    private fun recoverAndSubscribeCookies(){
        /**
         * クッキーの再取得と同時にすべてtrueになり、全部完了しないと次のクッキーを取得させない
         * すべてのデータを空にする
         */
        if (!IsRecovering.isRecoveringCookies && !IsRecovering.isRecoveringCurriculum && !IsRecovering.isRecoveringBulletinBoardLinks) {
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
                            IsRecovering.isRecoveringBulletinBoardLinks = true

                            /**
                             * 掲示板のデータはクッキーが変わると使用不能になるので
                             * データを空にする処理
                             */
                            receivedStuffs.receivedBulletinBoardLinks.value = mutableListOf()
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
                                IsRecovering.isRecoveringBulletinBoardLinks = false
                            }

                        }

                        override fun onError(e: Throwable) {
                            /**
                             * ここでエラーになるということは、onNextは呼ばれないので次の通信を可能にするために
                             * すべてのフラッグをfalseに戻さなければならない
                             */
                            IsRecovering.isRecoveringCookies = false
                            IsRecovering.isRecoveringCurriculum = false
                            IsRecovering.isRecoveringBulletinBoardLinks = false
                            try {
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
        KoanService.getAndSaveCurriculum()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MutableList<String>> {
                    override fun onComplete() {
                        IsRecovering.isRecoveringCurriculum = false
                    }
                    override fun onSubscribe(d: Disposable) {}

                    /**
                     * 取得したデータをrealmListの形に直して保存し、グローバル変数にも入れる
                     */
                    override fun onNext(curriculum: MutableList<String>) {
                        /**
                         * 非同期処理なのでここでrealm初期化
                         * データの形式をrealmListに変更
                         */
                        val realmCurriculum = RealmList<String>()
                        val tempRealm = Realm.getDefaultInstance()
                        val userData = tempRealm.where(User::class.java).findFirst()
                        for (curriculumData in curriculum)
                            realmCurriculum.add(curriculumData)

                        /**
                         * realmに保存
                         * realmのデータが何らかの原因で消滅または改ざんされていた場合はエラーを表示
                         */
                        try {
                            tempRealm.beginTransaction()
                            userData!!.curriculum = realmCurriculum
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
                        receivedStuffs.receivedCurriculum.value = curriculum

                        /**
                         * realm閉じる
                         */
                        tempRealm.close()
                    }

                    override fun onError(e: Throwable) {
                        IsRecovering.isRecoveringCurriculum = false
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
        KoanService.getBulletinBoardLinks()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MutableList<String>> {
                    override fun onComplete() {
                        IsRecovering.isRecoveringBulletinBoardLinks = false
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(receivedBulletinBoardLinks: MutableList<String>) {
                        /**
                         * 掲示板のリンク(13個)を取ってくる
                         */
                        receivedStuffs.receivedBulletinBoardLinks.value = receivedBulletinBoardLinks
                    }

                    override fun onError(e: Throwable) {
                        IsRecovering.isRecoveringBulletinBoardLinks = false
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
                1 -> return BulletinBoardFragment()
                2 -> return AccountFragment()
            }
            return CurriculumFragment()
        }

        override fun getCount(): Int {
            return 3
        }
    }

}
