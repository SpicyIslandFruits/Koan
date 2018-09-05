package com.example.spicyisland.koan

import io.reactivex.schedulers.Schedulers
import io.realm.Realm

/**
 * クッキーの再取得をして保存するだけ
 * 基本的にクッキーを取得し直したらonErrorにインターネット接続かログインのやり直しを求める処理
 * onNextにすべてのデータの再取得処理を書かなければいけないのでこれは使わない可能性が高い
 */
class RecoverCookies {
    init {
        val realm = Realm.getDefaultInstance()
        val encryptedUserData = realm.where(User::class.java).findFirst()
        val userData = DeCryptor().decryptData(encryptedUserData!!.userData, encryptedUserData.iv)
        KoanService.getKoanCookiesObservableCallable(userData.substring(0, 8),
                userData.substring(8),
                true).subscribeOn(Schedulers.newThread())
                .subscribe()
    }
}