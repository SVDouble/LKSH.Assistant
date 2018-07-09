package com.lksh.dev.lkshassistant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        var isLogin = false

        login_button.setOnClickListener {
            val login = loginField.text.toString()
            val password = passwordField.text.toString()
            var usrDataList = DBWrapper.getInstance(this).listUsers("%")

            if (login.isNotEmpty() && password.isNotEmpty()) {
                if (usrDataList.size > 0) {
                    var lazyData = ArrayList<String>()
                    for (temp in usrDataList) {
                        if (temp.login == login && temp.password == password) {
                            finish()
                            isLogin = true
                            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            break
                        }
                    }
                    if (!isLogin){
                        Toast.makeText(this, "Invalid login or password", Toast.LENGTH_LONG).show()

                    }
                }

                }else {
                Toast.makeText(this, "Empty fields", Toast.LENGTH_LONG).show()

            }
        }
    }
}

