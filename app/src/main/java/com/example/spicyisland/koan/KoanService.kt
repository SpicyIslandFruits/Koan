package com.example.spicyisland.koan

import io.reactivex.Observable
import org.jsoup.Connection
import org.jsoup.Jsoup

object KoanService {

    fun getStringsObservableCallableFromTagAndTagPosition(url: String,
                                     cookies: Map<String, String>,
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

    fun getKoanCookiesObservableCallable(userID: String, userPassword: String): Observable<Map<String, String>> {

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

            koanCookies
        }

    }

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

}
