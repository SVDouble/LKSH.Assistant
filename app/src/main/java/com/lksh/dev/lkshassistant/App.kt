package com.lksh.dev.lkshassistant

import android.app.Application
import com.lksh.dev.lkshassistant.SQliteHelper.DBWrapper
import com.lksh.dev.lkshassistant.SQliteHelper.initDb

class App : Application() {

    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        initDb(DBWrapper.getInstance(applicationContext), resources)
        super.onCreate()
    }
}