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
            .schemaVersion(2)
            .name("todo")
            .build()
        Realm.setDefaultConfiguration(config)
        Timber.plant(Timber.DebugTree())
    }
}

