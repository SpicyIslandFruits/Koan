package com.example.spicyisland.koan

import io.realm.RealmObject

open class User: RealmObject() {
    var userData = byteArrayOf()
    var iv = byteArrayOf()
}