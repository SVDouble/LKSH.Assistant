package com.lksh.dev.lkshassistant.web

import android.content.Context
import android.util.Log
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.ui.activities.TAG

class Auth private constructor() {
    companion object {

        /* Public API */
        @JvmStatic
        fun continueIfAlreadyLoggedIn(ctx: Context) {
            if (Prefs.getInstance(ctx).isLoggedIn) {
                Log.d(TAG, "Auth: already logged in, fast start!")
                forwardLoginResult(LoginResult.SUCCESS)
            }
        }

        @JvmStatic
        fun requestLogin(ctx: Context, login: String, password: String) {
            if (!checkCredentials(login, password))
                forwardLoginResult(LoginResult.FAIL_INCORRECT_CRED)
            else
                NetworkHelper.authUser(ctx, login, password)
        }

        @JvmStatic
        fun handleServerResponse(ctx: Context, login: String, responseState: ResponseState?, token: String?) {
            if (responseState != null)
                forwardResponseState(responseState)
            else {
                if (token == null)
                    forwardLoginResult(LoginResult.FAIL_CRED_DO_NOT_MATCH)
                else {
                    Prefs.getInstance(ctx).userLogin = login
                    Prefs.getInstance(ctx).userToken = token
                    Prefs.getInstance(ctx).isLoggedIn = true
                    forwardLoginResult(LoginResult.SUCCESS)
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
            listeners[key] = (ctx as? OnAuthInteractionListener) ?: throw IllegalArgumentException("Class must implement onAuthInteractionListener")
            Log.d(TAG, "registerCallback: registered $key")
        }

        /* Inner logic */
        private var listeners: MutableMap<String, OnAuthInteractionListener> = mutableMapOf()

        @JvmStatic
        private fun checkCredentials(login: String, password: String): Boolean {
            if (login == "" || password == "")
                return false
            if (!login.matches(Regex("([а-яА-Яa-zA-Z0-9@_.]+)")))
                return false
            return true
        }

        @JvmStatic
        private fun forwardLoginResult(loginResult: LoginResult) {
            listeners.values.forEach {
                it.onLoginResultFetched(loginResult)
            }
        }

        @JvmStatic
        private fun forwardResponseState(responseState: ResponseState) {
            listeners.values.forEach {
                it.onServerFault(responseState)
            }
        }
    }

    interface OnAuthInteractionListener {
        fun onLoginResultFetched(loginResult: LoginResult)
        fun onServerFault(responseState: ResponseState)
    }

    enum class LoginResult {
        SUCCESS,
        FAIL_CRED_DO_NOT_MATCH,
        FAIL_INCORRECT_CRED
    }

    enum class ResponseState {
        SERVER_NOT_FOUND,
        TIMEOUT_REACHED
    }
}