package com.lksh.dev.lkshassistant.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    private val prefs by lazy {
        Prefs.getInstance(applicationContext)
    }

    private val listener: (View) -> Unit = {
        var isLogin = false
        val login = loginField.text.toString()
        val password = passwordField.text.toString()
        val usrDataList = DBWrapper.getInstance(this).listUsers("%")

        if (login.isNotEmpty() && password.isNotEmpty()) {
            if (usrDataList.size > 0) {
                for (temp in usrDataList) {
                    if (temp.login == login && temp.password == password) {
                        finish()
                        isLogin = true
                        prefs.login = login
                        prefs.password = password
                        prefs.loginState = true
                        startActivity(Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        break
                    }
                }
                if (!isLogin)
                    Toast.makeText(this,
                            "Invalid login or password",
                            Toast.LENGTH_LONG).show()
            }

        } else
            Toast.makeText(this,
                    "Empty fields",
                    Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        /* Request permissions */
        if (ContextCompat.checkSelfPermission(this@StartActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@StartActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@StartActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@StartActivity,
                        Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@StartActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET), 0)
        }

        if (prefs.loginState) {
            finish()
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        } else {
            login_button.setOnClickListener(listener)
        }
    }
}
