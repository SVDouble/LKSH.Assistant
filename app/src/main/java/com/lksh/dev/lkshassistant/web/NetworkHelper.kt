package com.lksh.dev.lkshassistant.web

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.lksh.dev.lkshassistant.AppSettings

typealias httpResponse = (Request, Response, Result<String, FuelError>) -> Unit

class NetworkHelper private constructor() {
    companion object {
        @JvmStatic
        fun authUser(login: String, password: String, handler: httpResponse) {
            val authUrl = AppSettings.baseUrl + "/user_auth/"
            authUrl.httpPost(listOf(Pair("login", login),
                    Pair("password", password))).timeout(5000).responseString(Charsets.UTF_8, handler)
        }
    }
}