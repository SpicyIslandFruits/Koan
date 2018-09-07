package com.example.spicyisland.koan

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    var isFirstConnection = true

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
        if (url == null) {
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
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            isFirstConnection = false
            /**
             * urlがログイン画面のものになっていた場合は自動ログインが完了していないと判断してトーストを出す
             */
            if (url!!.contains("https://ou-idp.auth.osaka-u.ac.jp/idp/sso_redirect?SAMLRequest=")) {
                Toast.makeText(this@WebViewActivity, R.string.cookie_error, Toast.LENGTH_LONG).show()
            }
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (!isFirstConnection && request!!.url.toString().contains(BulletinBoardLink)) {
                receivedStuffs.receivedBulletinBoardLinks.value = null
                receivedStuffs.receivedBulletinBoardUnreadCount.value = null
                Toast.makeText(applicationContext, R.string.recover_bulletin_board_link, Toast.LENGTH_LONG).show()
            }
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

}
