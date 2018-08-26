package com.example.spicyisland.koan

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException

class KoanService : AsyncTask<Void, Void, Void> {
    lateinit var res: Connection.Response
    lateinit var koanCookies: MutableMap<String, String>
    lateinit var idpCookies: Map<String, String>
    lateinit var key: String
    lateinit var url: String
    lateinit var cookies: Map<String, String>
    lateinit var tag: String
    lateinit var tagPositions: ArrayList<Int>
    lateinit var elements: Elements
    var elementTexts = ArrayList<String>()
    lateinit var context: Context

    var isLogin = false

    constructor(key: String,
                url: String,
                cookies: Map<String, String>,
                tag: String,
                tagPositions: ArrayList<Int>, context: Context) : super() {

        this.key = key
        this.url = url
        this.cookies = cookies
        this.tag = tag
        this.tagPositions = tagPositions
        this.context = context

    }

    constructor(isLogin: Boolean) : super() {
        if (isLogin) {
            this.isLogin = isLogin
        }else{
            /**
             * 手動でfalseを選ぶと必ずエラーになるので
             * 処理をさせない工夫をしたい。
             */
            return
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: Void?): Void? {

        try {

            if(isLogin){
                res = Jsoup.connect(KoanMainPage).followRedirects(false).method(Connection.Method.GET).execute()
                koanCookies = res.cookies()
                res = Jsoup.connect(KoanSsoLoginPage).cookies(koanCookies).method(Connection.Method.GET).execute()
                idpCookies = res.cookies()
                res = Jsoup.connect(IdpAuthnPwd).data("USER_ID", "u324895f", "USER_PASSWORD", "YoYo1234YoYo1234").cookies(idpCookies).method(Connection.Method.POST).execute()
                res = Jsoup.connect(IdpRoleSelect).data("role", "self_0").cookies(idpCookies).method(Connection.Method.POST).execute()
                val doc = res.parse()
                val samlResponse = doc.select("input[name=SAMLResponse]").attr("value")
                val relayState = doc.select("input[name=RelayState]").attr("value")
                res = Jsoup.connect(KoanSaml2Post).data("SAMLResponse", samlResponse, "RelayState", relayState).cookies(koanCookies).followRedirects(false).method(Connection.Method.POST).execute()
                koanCookies.putAll(res.cookies())
            }else{
                res = Jsoup.connect(this.url).cookies(cookies).method(Connection.Method.GET).execute()
                elements = res.parse().body().getElementsByTag(tag)
            }

        }catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        if(isLogin){
            ReceivedKoanCookies = koanCookies
        }else{
            try {
                for (i in tagPositions)
                    elementTexts.add(elements[i].toString())
            }catch (e: Exception){
                e.printStackTrace()
            }
            ReceivedStrings.put(key, elementTexts)
        }
    }

}
