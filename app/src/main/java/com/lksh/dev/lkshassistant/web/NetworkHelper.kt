package com.lksh.dev.lkshassistant.web

import android.content.Context
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.lksh.dev.lkshassistant.AppSettings
import org.json.JSONException
import org.json.JSONObject
import java.net.UnknownHostException

typealias httpResponse = (Request, Response, Result<String, FuelError>) -> Unit

class NetworkHelper private constructor() {
    companion object {
        @JvmStatic
        fun authUser(ctx: Context, login: String, password: String) {
            val authUrl = AppSettings.baseUrl + "/user_auth/"
            authUrl.httpPost(listOf(Pair("login", login), Pair("password", password)))
                    .timeout(5000).responseString { request, response, result ->
                        var token: String? = null
                        var responseState: Auth.ResponseState? = null

                        when (result) {
                            is Result.Success -> {
                                try {
                                    token = JSONObject(result.get())
                                            .getJSONArray("result")
                                            .getJSONObject(0)
                                            .getString("token")
                                } catch (e: UnknownHostException) {
                                    responseState = Auth.ResponseState.SERVER_NOT_FOUND
                                } catch (e: JSONException) {
                                    /* Token is null, everything is ok */
                                }
                            }
                            is Result.Failure -> {
                                responseState = Auth.ResponseState.TIMEOUT_REACHED
                            }
                        }
                        Auth.handleLoginResponse(ctx, login, responseState, token)
                    }
        }

        @JvmStatic
        fun getUsers(token: String, handler: httpResponse) {
            val authUrl = AppSettings.baseUrl + "/get_users/"
            authUrl.httpPost(listOf(Pair("token", authUrl)))
                    .timeout(5000).responseString { request, response, result ->
                    }
        }

        fun sendPosition(id: String, token: String, lat: Double, long: Double) {
            val authUrl = AppSettings.baseUrl + "/get_users/$id/set/"
            authUrl.httpPost(listOf(Pair("token", token), Pair("lat", lat),
                    Pair("long", long)))
                    .timeout(5000).responseString { request, response, result -> }
        }
    }
}