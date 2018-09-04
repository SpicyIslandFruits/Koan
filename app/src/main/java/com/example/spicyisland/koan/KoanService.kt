package com.example.spicyisland.koan

import android.webkit.CookieManager
import io.reactivex.Observable
import org.jsoup.Connection
import org.jsoup.Jsoup

/**
 * サービスはangularの真似をしてオブジェクトお保持するようにしてみた
 * TODO: クッキーを取り出してsubscribeまで行うメソッドを作る、時間割と掲示板のリンクの分を作る
 * TODO: クッキーを取り出して通信を始めたあとに、別でクッキーを取得する処理を行うと、クッキーと掲示板のリンクが噛み合わなくなるので禁止
 * TODO: クッキーの取得をやり直した場合は必ず他のデータも全部取得し直す、ReceivedStuffに入っているデータはすべて削除する
 * TODO: それでもエラーが出た場合は、インターネット接続か、ログインのやり直しを求めるトーストを表示
 */
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
    fun getKoanCookiesObservableCallable(userID: String,
                                         userPassword: String,
                                         isSaveCookies: Boolean = false,
                                         url: String = KoanUrl): Observable<Map<String, String>> {

        return Observable.fromCallable {
            val koanCookies = Jsoup.connect(KoanMainPage).followRedirects(false).method(Connection.Method.GET).execute().cookies()
            val idpCookies = Jsoup.connect(KoanSsoLoginPage).cookies(koanCookies).method(Connection.Method.GET).execute().cookies()

            Jsoup.connect(IdpAuthnPwd)
                    .data("USER_ID", userID, "USER_PASSWORD", userPassword)
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

}
