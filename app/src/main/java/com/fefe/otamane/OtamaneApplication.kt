package com.fefe.otamane

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by fefe on 2018/01/25.
 */
class OtamaneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().build())
    }
}