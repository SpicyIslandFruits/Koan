package com.example.spicyisland.koan

import android.webkit.CookieManager

class DataChanger {
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