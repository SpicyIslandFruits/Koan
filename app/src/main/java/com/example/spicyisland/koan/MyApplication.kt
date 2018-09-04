package com.example.spicyisland.koan

import android.app.Application
import com.beardedhen.androidbootstrap.TypefaceProvider
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * realmの初期化、bootstrapの初期化
         */
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
        TypefaceProvider.registerDefaultIconSets()
    }

}