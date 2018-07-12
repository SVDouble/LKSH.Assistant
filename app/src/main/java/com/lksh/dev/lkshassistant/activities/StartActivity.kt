package com.lksh.dev.lkshassistant.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.lksh.dev.lkshassistant.Fonts
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.sqlite_helper.DBHandler
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import kotlinx.android.synthetic.main.activity_start.*
import org.jetbrains.anko.doAsync


class StartActivity : AppCompatActivity(), DBWrapper.DbInteraction {

    private val prefs by lazy {
        Prefs.getInstance(applicationContext)
    }

    private lateinit var db: DBHandler

    private val listener: (View) -> Unit = {
        if (!::db.isInitialized) {
            Toast.makeText(this,
                    "Please wait: db is loading",
                    Toast.LENGTH_SHORT).show()
        } else {
            var isLogin = false
            val login = loginField.text.toString()
            val password = passwordField.text.toString()
            val usrDataList = DBWrapper.getInstance(this).listUsers("%")

            if (login.isNotEmpty() && password.isNotEmpty()) {
                if (usrDataList.size > 0) {
                    for (temp in usrDataList) {
                        if (temp.login == login && temp.password == password) {
                            isLogin = true
                            prefs.login = login
                            prefs.password = password
                            prefs.loginState = true
                            startActivity(Intent(this, MainActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                            finish()
                            break
                        }
                    }
                    if (!isLogin)
                        Toast.makeText(this,
                                "Invalid login or password",
                                Toast.LENGTH_SHORT).show()
                }

            } else
                Toast.makeText(this,
                        "Empty fields",
                        Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Log.d(TAG, "START: OnCreate loaded!")
//        attachKeyboardListeners()

        startLabel.typeface = Fonts.getInstance(this).montserrat
        loginField.typeface = Fonts.getInstance(this).montserrat
        passwordField.typeface = Fonts.getInstance(this).montserrat
        login_btn.typeface = Fonts.getInstance(this).montserrat

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
        login_button.setOnClickListener(listener)
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "START: OnStart loaded!")
        /* Init DB */
        Log.d(TAG, "Loading database!")
        doAsync {
            DBWrapper.registerCallback(this@StartActivity, true)
            DBWrapper.initDb(applicationContext, resources)
            db = DBWrapper.getInstance(this@StartActivity)
            Log.d(TAG, "Successfully loaded")
            runOnUiThread {
                onDbLoad()
            }
        }
    }

    override fun onDestroy() {
//        if (keyboardListenersAttached) {
//            rootLayout!!.viewTreeObserver
//                    .removeOnGlobalLayoutListener(keyboardLayoutListener)
//        }
        super.onDestroy()
    }

    override fun onDbLoad() {

        Log.d(TAG, "START: OnDbLoad!")
        if (prefs.loginState) {
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        } else {
            loginField.visibility = View.VISIBLE
            passwordField.visibility = View.VISIBLE
            cardView.visibility = View.VISIBLE
        }
    }
    /*
    /* Keyboard listener */
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = rootLayout!!.rootView.height - rootLayout!!.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top

        val broadcastManager = LocalBroadcastManager.getInstance(this@StartActivity)

        if (heightDiff <= contentViewTop) {
            onHideKeyboard()
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)
        }
    }

    private var keyboardListenersAttached = false
    private var rootLayout: ViewGroup? = null

    private fun onShowKeyboard(keyboardHeight: Int) {
        Log.d(TAG, "Keyboard show!")
        imageView.visibility = View.GONE
    }
    private fun onHideKeyboard() {
        Log.d(TAG, "Keyboard hide!")
        imageView.visibility = View.VISIBLE
    }

    private fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        rootLayout = findViewById<View>(R.id.activity_start) as ViewGroup
        rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        keyboardListenersAttached = true
    }
    */
}
