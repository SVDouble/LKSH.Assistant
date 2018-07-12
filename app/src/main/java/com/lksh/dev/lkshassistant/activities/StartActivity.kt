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
        Log.d(TAG, "loginClicked: try to login")

        val login = loginField.text.toString()
        val psw = passwordField.text.toString()
        if (!::db.isInitialized) {
            Log.d(TAG, "loginClicked: failed, db is not initialized")
            Toast.makeText(this,
                    "Please wait: db is loading",
                    Toast.LENGTH_SHORT).show()
        } else if (login.isEmpty() || psw.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show()
        } else {
            if (!login(login, psw)) {
                Toast.makeText(this,
                        "Wrong login or password",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

//        attachKeyboardListeners()

        /* Apply fonts */
        startLabel.typeface = Fonts.getInstance(this).montserrat
        loginField.typeface = Fonts.getInstance(this).montserrat
        passwordField.typeface = Fonts.getInstance(this).montserrat
        login_btn.typeface = Fonts.getInstance(this).montserrat

        /* Request permissions */
        Log.d(TAG, "onCreate: requesting permissions")
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

        /* Init DB */
        Log.d(TAG, "onCreate: loading db")
        doAsync {
            DBWrapper.registerCallback(this@StartActivity, "StartActivity")
            DBWrapper.initDb(applicationContext, resources)
            db = DBWrapper.getInstance(this@StartActivity)
            Log.d(TAG, "Successfully loaded db")
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

        Log.d(TAG, "onDbLoad: try automatically login")
        if (!login()) {
            Log.d(TAG, "onDbLoad: failed, enable login fields")
            loginField.visibility = View.VISIBLE
            passwordField.visibility = View.VISIBLE
            cardView.visibility = View.VISIBLE
        }
    }

    private fun login(login: String = "", psw: String = ""): Boolean {
        val user = DBWrapper.getInstance(applicationContext).listUsers("%")
                .filter { it.login == login && it.password == psw }
        if (user.isEmpty())
            return false
        else {
            if (user.size != 1)
                Log.d(TAG, "login: warning, same user records detected")
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
        return true
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
