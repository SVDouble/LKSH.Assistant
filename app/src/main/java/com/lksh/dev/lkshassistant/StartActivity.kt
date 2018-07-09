package com.lksh.dev.lkshassistant

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        var isLogin = false

        val prefs by lazy {
            Prefs.getInstance(applicationContext)
        }

        if (prefs.loginState) {
            finish()
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        } else {

            login_button.setOnClickListener {
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
                        if (!isLogin) {
                            Toast.makeText(this, "Invalid login or password", Toast.LENGTH_LONG).show()
                            passwordField.setText("")

                        }
                    }

                } else {
                    Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG).show()
                    passwordField.setText("")

                }
            }
        }
    }
}
