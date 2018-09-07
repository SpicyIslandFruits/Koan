package com.example.spicyisland.koan

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    /**
     * webViewを起動して初めてのリクエストかどうかを確認
     */
    var isFirstRequest = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * webViewのセッティング
         */
        setContentView(R.layout.activity_web_view)
        val webView = webView
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = MyWebViewClient()
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        val url = intent.getStringExtra("LINK")
        /**
         * TODO: urlを指定してロードできるようにする、指定しない場合はKoanMainPageに行く
         */
        if (url.isEmpty()) {
            webView.loadUrl(KoanMainPage)
        } else {
            webView.loadUrl(url)
        }

    }

    /**
     * キーボードのバックボタン処理をブラウザバックにする
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    /**
     * webViewを起動して初めてのリクエストでリダイレクトされた場合はログインが完了していないと判断してメッセージを出す
     */
    inner class MyWebViewClient : WebViewClient(){
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (Build.VERSION.SDK_INT >= 24 && request!!.isRedirect && isFirstRequest)
                Toast.makeText(this@WebViewActivity, R.string.still_getting_cookie, Toast.LENGTH_LONG).show()
            isFirstRequest = false
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

}
