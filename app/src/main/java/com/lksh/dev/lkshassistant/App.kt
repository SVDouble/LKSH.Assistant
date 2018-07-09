package com.lksh.dev.lkshassistant

import android.app.Application

class App : Application() {

    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        initDb(DBWrapper.getInstance(this), resources)
        super.onCreate()
    }
}