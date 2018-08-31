package com.example.spicyisland.koan

import android.app.Application
import com.beardedhen.androidbootstrap.TypefaceProvider
import io.realm.Realm
import io.realm.RealmConfiguration



class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
        TypefaceProvider.registerDefaultIconSets()
    }
}