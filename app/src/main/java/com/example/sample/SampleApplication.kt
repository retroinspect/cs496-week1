package com.example.sample

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("noteManager")
            .build()
        Realm.setDefaultConfiguration(config)
        Timber.plant(Timber.DebugTree())
    }

}

