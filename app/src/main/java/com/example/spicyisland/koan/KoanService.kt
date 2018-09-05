package com.example.spicyisland.koan

import android.webkit.CookieManager
import io.reactivex.Observable
import io.realm.Realm
import org.jsoup.Connection
import org.jsoup.Jsoup

/**
 * サービスはangularの真似をしてオブジェクトお保持するようにしてみた
 * TODO: クッキーを取り出して通信を始めたあとに、別でクッキーを取得する処理を行うと、クッキーと掲示板のリンクが噛み合わなくなるので禁止
 * TODO: クッキーの取得をやり直した場合は必ず他のデータも全部取得し直す、ReceivedStuffに入っているデータはすべて削除する
 * TODO: それでもエラーが出た場合は、インターネット接続か、ログインのやり直しを求めるトーストを表示
 */
object IsRecovering{
    var isRecoveringCookies = false
    var isRecoveringCurriculum = false
    var isRecoveringBulletinBoardLinks = false
}

object KoanService {

    /**
     * tagとtagの場所を指定して文字列を取ってくるメソッド
     * TODO: 指定されたとおりに取れなかったらエラーを吐くのでonErrorで処理を書く
     * TODO: onErrorには基本的にクッキーが間違っていると判断してクッキーの取得からやり直す処理を書く
     * TODO: クッキーの取得をやり直す場合、receivedStuffにはいっているデータをすべて削除し、必要なデータをすべて取得し直す
     * TODO: それでもエラーが出た場合は、インターネット接続の確認、ログインのやり直しを求めるトーストを表示
     */
    fun getStringsObservableCallableFromTagAndTagPosition(url: String,
                                     cookies: Map<String, String>?,
                                     tag: String,
                                     tagPositions: ArrayList<Int>): Observable<MutableList<String>> {

        return Observable.fromCallable {
            val elementTexts = mutableListOf<String>()
            val elements = Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).execute().parse().body().getElementsByTag(tag)
            for (i in tagPositions)
                elementTexts.add(elements[i].text())
            elementTexts
        }
    }

    /**
     * urlとidとパスワードからクッキーの取得をするメソッド、オプションでそのままクッキーストアに保存するか決めれる
     * TODO: 指定されたとおりに取得できなかったらエラーを吐くのでonErrorでそのエラーを処理するコードを書く
     * TODO: onErrorでは基本的にインターネット接続の確認、ログインのやり直しを求めるトーストを表示する
     */
    fun getKoanCookiesObservableCallable(koanID: String?,
                                         userPassword: String?,
                                         isSaveCookies: Boolean = false,
                                         url: String = KoanUrl): Observable<Map<String, String>> {

        return Observable.fromCallable {
            val koanCookies = Jsoup.connect(KoanMainPage).followRedirects(false).method(Connection.Method.GET).execute().cookies()
            val idpCookies = Jsoup.connect(KoanSsoLoginPage).cookies(koanCookies).method(Connection.Method.GET).execute().cookies()

            Jsoup.connect(IdpAuthnPwd)
                    .data("USER_ID", koanID, "USER_PASSWORD", userPassword)
                    .cookies(idpCookies).method(Connection.Method.POST).execute()

            val doc = Jsoup.connect(IdpRoleSelect).data("role", "self_0").cookies(idpCookies).method(Connection.Method.POST).execute().parse()

            koanCookies.putAll(Jsoup.connect(KoanSaml2Post).data("SAMLResponse",
                    doc.select("input[name=SAMLResponse]").attr("value"),
                    "RelayState", doc.select("input[name=RelayState]").attr("value"))
                    .cookies(koanCookies).followRedirects(false).method(Connection.Method.POST).execute().cookies())

            Jsoup.connect("https://koan.osaka-u.ac.jp/campusweb/ssologin.do?page=smart").cookies(koanCookies).method(Connection.Method.GET).execute()

            if (isSaveCookies) {
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                for (koanCookie in koanCookies)
                    cookieManager.setCookie(url, koanCookie.key + "=" + koanCookie.value)
            }

            koanCookies
        }

    }

    /**
     * idとパスワードをチェックするメソッドだが使いみちがない
     * もし間違っていた場合onErrorに行く
     */
    fun checkIDAndPass(id: String, password: String): Observable<Unit> {
        return Observable.fromCallable {
            val koanCookies = Jsoup.connect(KoanMainPage).followRedirects(false).method(Connection.Method.GET).execute().cookies()
            val idpCookies = Jsoup.connect(KoanSsoLoginPage).cookies(koanCookies).method(Connection.Method.GET).execute().cookies()

            Jsoup.connect(IdpAuthnPwd).data("USER_ID", id, "USER_PASSWORD", password)
                    .cookies(idpCookies).method(Connection.Method.POST).execute()

            val doc = Jsoup.connect(IdpRoleSelect).data("role", "self_0").cookies(idpCookies).method(Connection.Method.POST).execute().parse()

            koanCookies.putAll(Jsoup.connect(KoanSaml2Post).data("SAMLResponse",
                    doc.select("input[name=SAMLResponse]").attr("value"),
                    "RelayState", doc.select("input[name=RelayState]").attr("value"))
                    .cookies(koanCookies).followRedirects(false).method(Connection.Method.POST).execute().cookies())
        }
    }

    /**
     * クッキーストアからクッキーを取得するメソッド
     * クッキーの数はkoanでは3のはずなので、デフォルトではそれ以外の個数が帰ってきたらnullを返す
     * 予想されるクッキーの数はオプションで変更可能
     */
    fun getCookieMapFromCookieManager(url: String = KoanUrl, expectedCookieSize: Int = 3): MutableMap<String, String>? {
        val cookieManager = CookieManager.getInstance()
        val koanCookiesString = cookieManager.getCookie(url)
        var koanCookies: MutableMap<String, String>? = mutableMapOf()
        if (koanCookiesString != null) {
            val koanCookie = koanCookiesString.split("=", ";")
            for (i in 0 until koanCookie.size step 2)
                koanCookies!![koanCookie[i]] = koanCookie[i + 1]

            if (koanCookies!!.size != expectedCookieSize){
                koanCookies = null
            }
        }else{
            koanCookies = null
        }

        return koanCookies
    }

    /**
     * データベースからidとパスワードを取得して復号化してマップで返します
     * TODO: このメソッドを使った場合はアクティビティ側でnullチェックを行い、nullの場合はトーストで再ログインを求めてください
     */
    fun getAndDecryptIDAndPassFromRealm(): Map<String, String>? {
        val userData = mutableMapOf<String, String>()
        val realm = Realm.getDefaultInstance()
        val encryptedUserData = realm.where(User::class.java).findFirst()
        if (encryptedUserData != null) {
            val decrypted = DeCryptor().decryptData(encryptedUserData.userData, encryptedUserData.iv)
            userData["koanID"] = decrypted.substring(0, 8)
            userData["Password"] = decrypted.substring(8)
            return userData
        } else {
            return null
        }
    }

    /**
     * koanCookieを取得するメソッド
     * TODO: このメソッドを呼び出し、
     * TODO: onNextで時間割と掲示板の取得、onCompleteでisRecoveringCookiesをfalseにし、
     * TODO: onErrorでトーストの表示とisRecoveringCookiesをfalseにするメソッドをアクティビティに書く
     */
    fun recoverCookies(): Observable<Map<String, String>> {
        val userData: Map<String, String>?
        if (getAndDecryptIDAndPassFromRealm() != null) {
            userData = getAndDecryptIDAndPassFromRealm()
        } else {
            userData = mapOf()
        }
        return getKoanCookiesObservableCallable(userData!!["koanID"], userData["Password"], true)
    }

    /**
     * TODO: このメソッドを呼び出す場合...
     * TODO: onNextでデータの保存をtryしてエラーをcatchした場合再ログインを求めるトーストを表示する処理、receivedStuffsに入れる処理を書く
     * TODO: onErrorでisRecoveringCookiesをfalseに変えてrecoverCookiesを手順通りに行う処理を書いたメソッドを実行する処理をアクティビティに書く
     */
    fun getAndSaveCurriculum(): Observable<MutableList<String>> {
        val koanCookies = KoanService.getCookieMapFromCookieManager()
        return getStringsObservableCallableFromTagAndTagPosition(KoanCurriculum, koanCookies,
                "td", curriculumTagPositions)
    }

    /**
     * TODO: メソッドを完成させる
     * TODO: 現在このメソッドを実行すると必ずonErrorに行きます
     * TODO: 具体的には、getStringObservableCallableを実行して掲示板のリンクをObservableで取得してくる
     * TODO: このメソッドを呼び出す場合...
     * TODO: onNextでreceivedStuffsに文字列を保存する処理
     * TODO: onErrorでisRecoveringCookiesをfalseに変えてrecoverCookiesを手順通りに行う処理を書いたメソッドを実行する処理をアクティビティに書く
     */
    fun getBulletinBoardLinks(): Observable<MutableList<String>> {
        val koanCookies = KoanService.getCookieMapFromCookieManager()
        return Observable.fromCallable{
            var koanBulletinLinkList = mutableListOf<String>()

            val res = Jsoup.connect(BulletinBoardLink)
                    .cookies(koanCookies).followRedirects(false).method(Connection.Method.GET).execute()
            val flowExecutionKey = res.header("location")

            val urlBegin = KoanUrlWithoutSlash+flowExecutionKey

            try {
                koanBulletinLinkList.add(0, urlBegin + LessonBulletinBulletinLink)
                koanBulletinLinkList.add(1, urlBegin + LessonBulletinLink)
                koanBulletinLinkList.add(2, urlBegin + NotificationBulletinLink)
                koanBulletinLinkList.add(3, urlBegin + IndividualBulletinLink)
                koanBulletinLinkList.add(4, urlBegin + StudentAffairsOfficeBulletinLink)
                koanBulletinLinkList.add(5, urlBegin + MinorCourseBulletinLink)
                koanBulletinLinkList.add(6, urlBegin + TeachingProfessionBulletinLink)
                koanBulletinLinkList.add(7, urlBegin + ScholarshipBulletinLink)
                koanBulletinLinkList.add(8, urlBegin + CareerBulletinLink)
                koanBulletinLinkList.add(9, urlBegin + SchoolLifeBulletinLink)
                koanBulletinLinkList.add(10, urlBegin + StudyAbroadStudentBulletinLink)
                koanBulletinLinkList.add(11, urlBegin + StudyAbroadBulletinLink)
                koanBulletinLinkList.add(12, urlBegin + OtherBulletinLink)
            }catch (e: Exception){
                e.printStackTrace()
            }
            koanBulletinLinkList
        }
    }

}
