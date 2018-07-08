package com.lksh.dev.lkshassistant

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.e("START", "I'm started")
        startActivity(Intent(this, MapActivity::class.java))
        setContentView(R.layout.activity_main)
    }
}
