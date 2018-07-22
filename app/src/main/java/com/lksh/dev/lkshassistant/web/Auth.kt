package com.lksh.dev.lkshassistant.web

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.ui.activities.TAG
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread

class Auth private constructor() {
    companion object {
        private var listeners: MutableMap<String, onAuthInteractionListener> = mutableMapOf()

        @JvmStatic
        fun requestLogin(ctx: Context, login: String, password: String) {
            doAsync {
                val result = LoginResult.LOGIN_FAILED
                ctx.runOnUiThread {
                    listeners.values.forEach {
                        it.onLoginResultFetched(result)
                    }
                }
            }
        }

        @JvmStatic
        fun logout(ctx: Context) {
            Prefs.getInstance(ctx).apply {
                isLoggedIn = false
                userToken = ""
            }
        }

        @JvmStatic
        fun registerCallback(ctx: Context, key: String) {
            listeners[key] = (ctx as? onAuthInteractionListener) ?: throw IllegalArgumentException("Class must implement onAuthInteractionListener")
            Log.d(TAG, "registerCallback: registered $key")
        }

        @JvmStatic
        private fun checkCredentials(login: String, password: String): Boolean {
            return false
        }
    }

    interface onAuthInteractionListener {
        fun onLoginResultFetched(loginResult: LoginResult)
        fun onLoginTimeout()
    }

    enum class LoginResult {
        LOGIN_SUCCESS, LOGIN_FAILED
    }
}