package com.example.spicyisland.koan

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        //Cookieの数が2でも３でもないときはnullを返し、トーストで自動ログインできないことを伝える
        if(KoanService.getCookieMapFromCookieManager() == null
                && KoanService.getCookieMapFromCookieManager(KoanUrl, 2) == null){
            Toast.makeText(this, getText(R.string.still_getting_cookie), Toast.LENGTH_LONG).show()
        }
        val webView = webView
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.loadUrl(KoanMainPage)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
//気が向いたらブラウザのロードの表示をします。
//    inner class MyWebViewClient : WebViewClient() {
//        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//            super.onPageStarted(view, url, favicon)
//        }
//
//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//        }
//    }
}
