package com.lksh.dev.lkshassistant

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.activities.TAG
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper

class Auth private constructor() {
    companion object {
        @JvmStatic
        fun login(ctx: Context, login: String = "", psw: String = ""): Boolean {
            val user = DBWrapper.getInstance(ctx).listUsers("%")
                    .filter { it.login == login && it.password == psw }
            if (Prefs.getInstance(ctx).loginState) {
                Log.d(TAG, "login: login succeed")
            } else if (user.isEmpty()) {
                Log.d(TAG, "login: login failed")
                return false
            } else {
                if (user.size != 1)
                    Log.d(TAG, "login: warning, same user records detected")
                Prefs.getInstance(ctx).apply {
                    this.loginState = true
                    this.login = user[0].login
                }
                Log.d(TAG, "login: login succeed")
            }
            return true
        }

        @JvmStatic
        fun logout(ctx: Context) {
            Prefs.getInstance(ctx).apply {
                loginState = false
                login = ""
            }
        }
    }
}