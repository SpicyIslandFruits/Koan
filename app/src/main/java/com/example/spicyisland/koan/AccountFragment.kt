package com.example.spicyisland.koan

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openKoanButton.setOnClickListener{
            startActivity(Intent(this.context, WebViewActivity::class.java))
        }

        logOutButton.setOnClickListener{
            removeAllCookies()
            removeAllRealmUserObject()
            startActivity(Intent(this.context, StartActivity::class.java))
        }
    }

    private fun removeAllRealmUserObject(){
        val realm = Realm.getDefaultInstance()
        val oldUser = realm.where(User::class.java).findAll()
        realm.beginTransaction()
        oldUser.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    private fun removeAllCookies() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
    }
}
