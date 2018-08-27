package com.example.spicyisland.koan

import io.reactivex.Observable
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.concurrent.Callable

class KoanService {

    fun getStringsObservableCallable(url: String,
                                     cookies: Map<String, String>,
                                     tag: String,
                                     tagPositions: ArrayList<Int>): Observable<ArrayList<String>> {

        return Observable.fromCallable(object : Callable<ArrayList<String>>{

            @Throws(Exception::class)
            override fun call(): ArrayList<String> {
                val elementTexts = ArrayList<String>()
                for (i in tagPositions)
                    elementTexts.add(Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).execute().parse().body().getElementsByTag(tag)[i].toString())
                return elementTexts
            }

        })
    }

    fun getKoanCookiesObservableCallable(): Observable<Map<String, String>> {

        return Observable.fromCallable(object : Callable<Map<String, String>>{

            @Throws(Exception::class)
            override fun call(): Map<String, String> {
                val koanCookies = Jsoup.connect(KoanMainPage).followRedirects(false).method(Connection.Method.GET).execute().cookies()
                val idpCookies = Jsoup.connect(KoanSsoLoginPage).cookies(koanCookies).method(Connection.Method.GET).execute().cookies()
                Jsoup.connect(IdpAuthnPwd).data("USER_ID", "u324895f", "USER_PASSWORD", "YoYo1234YoYo1234").cookies(idpCookies).method(Connection.Method.POST).execute()
                val doc = Jsoup.connect(IdpRoleSelect).data("role", "self_0").cookies(idpCookies).method(Connection.Method.POST).execute().parse()
                koanCookies.putAll(Jsoup.connect(KoanSaml2Post).data("SAMLResponse", doc.select("input[name=SAMLResponse]").attr("value"), "RelayState", doc.select("input[name=RelayState]").attr("value")).cookies(koanCookies).followRedirects(false).method(Connection.Method.POST).execute().cookies())
                return koanCookies
            }

        })

    }

}
