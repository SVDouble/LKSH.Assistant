package com.lksh.dev.lkshassistant

import android.app.Application
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.sqlite_helper.initDb
import org.jetbrains.anko.doAsync

class App : Application() {

    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        doAsync {
            initDb(applicationContext, DBWrapper.getInstance(this@App), resources)
        }
        super.onCreate()
    }
}