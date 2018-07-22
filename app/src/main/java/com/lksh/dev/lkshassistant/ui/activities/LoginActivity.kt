package com.lksh.dev.lkshassistant.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.ui.Fonts
import com.lksh.dev.lkshassistant.ui.KeyboardVisibilityListener
import com.lksh.dev.lkshassistant.ui.setKeyboardVisibilityListener
import com.lksh.dev.lkshassistant.web.Auth
import kotlinx.android.synthetic.main.activity_start.*

class LoginActivity : AppCompatActivity(),
        KeyboardVisibilityListener,
        Auth.onAuthInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        /* Register login activity */
        Auth.registerCallback(this, "LoginActivity")

        Log.d(TAG, "LoginActivity: launched")
        setKeyboardVisibilityListener(this, this)

        /* Apply fonts */
        startLabel.typeface = Fonts.getInstance(this).montserrat
        loginField.typeface = Fonts.getInstance(this).montserrat
        passwordField.typeface = Fonts.getInstance(this).montserrat
        login_btn.typeface = Fonts.getInstance(this).montserrat

        /* Request permissions */
        Log.d(TAG, "onCreate: requesting permissions")
        if (ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@LoginActivity,
                        Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@LoginActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET), 0)
        }
        login_button.setOnClickListener {
            val login = loginField.text.toString()
            val psw = passwordField.text.toString()
            Auth.requestLogin(applicationContext, login, psw)
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }

    override fun onKeyboardVisibilityChanged(keyboardVisible: Boolean) {
        if (keyboardVisible) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
        }

    }

    /* Auth */
    override fun onLoginResultFetched(loginResult: Auth.LoginResult) {
        if (loginResult == Auth.LoginResult.LOGIN_SUCCESS)
            startMain()
        else
            Log.d(TAG, "Login failed!")
    }

    override fun onServerFault() {
        Log.d(TAG, "Login timeout!")
    }
}
