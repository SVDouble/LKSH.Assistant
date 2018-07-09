package com.lksh.dev.lkshassistant

import android.app.Application
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.sqlite_helper.initDb

class App : Application() {

    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        initDb(DBWrapper.getInstance(applicationContext), resources)
        super.onCreate()
    }
}