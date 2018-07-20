package com.lksh.dev.lkshassistant

import android.app.Application
import com.lksh.dev.lkshassistant.data.Prefs

class App : Application() {
    override fun onCreate() {
        /* Instantiate prefs */
        Prefs.getInstance(applicationContext)
        super.onCreate()
    }
}