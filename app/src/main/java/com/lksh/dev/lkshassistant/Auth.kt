package com.lksh.dev.lkshassistant

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.activities.TAG
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper

class Auth private constructor() {
    companion object {
        @JvmStatic
        fun login(ctx: Context, login: String = "", password: String = ""): Boolean { // true -> successful login
            val user = DBWrapper.getInstance(ctx).listUsers("%")
                    .filter { it.login == login && it.password == password }

            if (user.size != 1){
                return Prefs.getInstance(ctx).isLoggedIn
            }

            Prefs.getInstance(ctx).apply {
                this.isLoggedIn = true
                this.login = user[0].login
            }

            return true
        }

        @JvmStatic
        fun logout(ctx: Context) {
                Prefs.getInstance(ctx).apply {
                    isLoggedIn = false
                    login = ""
                }
        }
    }
}