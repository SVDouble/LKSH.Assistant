package com.lksh.dev.lkshassistant.web

import android.content.Context
import android.util.Log
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.ui.activities.TAG
import org.json.JSONObject
import java.net.UnknownHostException

class Auth private constructor() {
    companion object {

        /* Public API */
        @JvmStatic
        fun requestLogin(ctx: Context, login: String, password: String) {
            if (!checkCredentials(login, password))
                forwardLoginResult(LoginResult.LOGIN_FAILED)
            "http://assistant.p2.lksh.ru/user_auth/".httpPost(listOf(Pair("login", login),
                    Pair("password", password))).responseString { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        try {
                            val token = JSONObject(result.get())
                                    .getJSONArray("result")
                                    .getJSONObject(0)
                                    .getString("token")
                            Log.d("Network", token)
                            Prefs.getInstance(ctx).userLogin = login
                            Prefs.getInstance(ctx).userToken = token
                            forwardLoginResult(LoginResult.LOGIN_SUCCESS)

                        } catch (e: UnknownHostException) {
                            forwardResponseState(ResponseState.SERVER_NOT_FOUND)
                        }
                    }
                    is Result.Failure -> {
                        forwardResponseState(ResponseState.TIMEOUT_REACHED)
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
            listeners[key] = (ctx as? OnAuthInteractionListener) ?: throw IllegalArgumentException("Class must implement onAuthInteractionListener")
            Log.d(TAG, "registerCallback: registered $key")
        }

        /* Inner logic */

        private var listeners: MutableMap<String, OnAuthInteractionListener> = mutableMapOf()

        @JvmStatic
        private fun checkCredentials(login: String, password: String): Boolean {
            if (login == "" || password == "")
                return false
            if (!login.matches(Regex("([a-zA-Z0-9@*#.@]+)")))
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
        LOGIN_SUCCESS,
        LOGIN_FAILED
    }

    enum class ResponseState {
        SERVER_NOT_FOUND,
        TIMEOUT_REACHED
    }
}