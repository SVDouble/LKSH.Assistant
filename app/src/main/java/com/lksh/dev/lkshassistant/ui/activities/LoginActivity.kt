package com.lksh.dev.lkshassistant.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.sqlite.DBHandler
import com.lksh.dev.lkshassistant.data.sqlite.DBWrapper
import com.lksh.dev.lkshassistant.ui.Fonts
import com.lksh.dev.lkshassistant.web.Auth
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.doAsync

class LoginActivity : AppCompatActivity(),
        DBWrapper.DbInteraction,
        KeyboardVisibilityListener {

    private lateinit var db: DBHandler

    private val listener: (View) -> Unit = {
        Log.d(TAG, "loginClicked: try to login")

        val login = loginField.text.toString()
        val psw = passwordField.text.toString()
        if (!::db.isInitialized) {
            Log.d(TAG, "loginClicked: failed, db is not initialized")
            Toast.makeText(this,
                    "Please wait: db is loading",
                    Toast.LENGTH_SHORT).show()
        } else if (!Auth.login(applicationContext, login, psw)) {
            Toast.makeText(this,
                    "Wrong login or password",
                    Toast.LENGTH_SHORT).show()
        } else {
            startMain()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)


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
        login_button.setOnClickListener(listener)

        /* Init DB */
        Log.d(TAG, "onCreate: loading db")
        doAsync {
            DBWrapper.registerCallback(this@LoginActivity, "LoginActivity")
            DBWrapper.initDb(applicationContext, resources)
            db = DBWrapper.getInstance(this@LoginActivity)
            Log.d(TAG, "Successfully loaded db: ${db.listUsers("%").size} recordings")
        }
    }

    override fun onDbLoaded() {

        Log.d(TAG, "onDbLoaded: try automatically login")
        if (Auth.login(applicationContext)) {
            startMain()
        } else {
            Log.d(TAG, "onDbLoaded: failed, enable login fields")
            loginField.visibility = View.VISIBLE
            passwordField.visibility = View.VISIBLE
            cardView.visibility = View.VISIBLE
        }
    }

    private fun startMain() {
        startActivity(Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }

    /* Keyboard events: open and close */

    fun setKeyboardVisibilityListener(activity: Activity, keyboardVisibilityListener: KeyboardVisibilityListener) {
        val contentView = activity.findViewById<View>(android.R.id.content)
        var mAppHeight: Int = 0
        var currentOrientation = -1
        contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private var mPreviousHeight: Int = 0
            override fun onGlobalLayout() {
                val newHeight = contentView.height
                if (newHeight == mPreviousHeight)
                    return
                mPreviousHeight = newHeight
                if (activity.resources.configuration.orientation != currentOrientation) {
                    currentOrientation = activity.resources.configuration.orientation
                    mAppHeight = 0
                }
                if (newHeight >= mAppHeight) {
                    mAppHeight = newHeight
                }
                if (newHeight != 0) {
                    if (mAppHeight > newHeight) {
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(true)
                    } else {
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(false)
                    }
                }
            }
        })
    }

    override fun onKeyboardVisibilityChanged(keyboardVisible: Boolean) {
        if (keyboardVisible) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
        }

    }
}

interface KeyboardVisibilityListener {
    fun onKeyboardVisibilityChanged(keyboardVisible: Boolean)
}
