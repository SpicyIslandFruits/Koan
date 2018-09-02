package com.example.spicyisland.koan

import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class RecoverCookies{
    constructor(){
        val realm = Realm.getDefaultInstance()
        val encryptedUserData = realm.where(User::class.java).findFirst()
        val userData = DeCryptor().decryptData(encryptedUserData!!.userData, encryptedUserData.iv)
        KoanService.getKoanCookiesObservableCallable(userData.substring(0, 8),
                userData.substring(8),
                true).subscribeOn(Schedulers.newThread())
                .subscribe()
    }
}