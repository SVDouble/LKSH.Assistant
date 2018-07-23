package com.lksh.dev.lkshassistant.web

import android.content.Context
import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.lksh.dev.lkshassistant.AppSettings
import com.lksh.dev.lkshassistant.data.FC_CONFIG_FILENAME
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.ui.activities.TAG
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

typealias httpResponse = (Request, Response, Result<String, FuelError>) -> Unit

class NetworkHelper private constructor() {
    companion object {
        @JvmStatic
        fun authUser(ctx: Context, login: String, password: String) {
            val authUrl = AppSettings.baseUrl + "/user_auth/"
            authUrl.httpPost(listOf(Pair("login", login), Pair("password", password)))
                    .timeout(5000).responseString { _, _, result ->
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
                        Auth.handleServerResponse(ctx, login, responseState, token)
                    }
        }

        private val serverFilePaths = mapOf(
                FC_CONFIG_FILENAME to "/versions/",
                "users" to "/get_users/"
        )

        @JvmStatic
        fun getTextFile(ctx: Context, fileName: String): String? {
            if (!serverFilePaths.containsKey(fileName))
                throw IllegalArgumentException("No path for requested file specified!")
            val fileUrl = AppSettings.baseUrl + serverFilePaths[fileName]
            val token = Prefs.getInstance(ctx).userToken
            Log.d(TAG, "Get '$fileName' from server")
            val url = URL(fileUrl)
            try {
                with(url.openConnection() as HttpURLConnection) {
                    //request
                    requestMethod = "POST"
                    connectTimeout = 50000
                    readTimeout = 50000
                    doOutput = true
                    doInput = true
                    val os = outputStream
                    val writer = BufferedWriter(
                            OutputStreamWriter(os, "UTF-8"))
                    writer.write(getPostDataString(hashMapOf("token" to token)))
                    writer.flush()
                    writer.close()
                    os.close()

                    //response
                    Log.d(TAG, "response code at $url is $responseCode")
                    if (responseCode != 200)
                        return null

                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        return response.toString()
                    }
                }
            } catch (e: UnknownHostException) {
                Log.d(TAG, "Host not found: ", e)
                return null
            }
        }

        private fun getPostDataString(params: HashMap<String, String>): String {
            val res = StringBuilder()
            params.forEach {
                res.append(it.key).append("=").append(it.value).append("&")
            }
            return res.dropLast(1).toString()
        }
    }
}