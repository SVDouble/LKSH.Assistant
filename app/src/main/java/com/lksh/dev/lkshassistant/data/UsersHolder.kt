package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.beust.klaxon.Json
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync

data class UserData(
        @Json(name = "login")
        var login: String,
        @Json(name = "name")
        var name: String,
        @Json(name = "surname")
        var surname: String,
        @Json(name = "room")
        var room: String,
        @Json(name = "house_id")
        var house: Int,
        @Json(name = "parallel")
        var parallel: String,
        @Json(ignored = true)
        var grade: String = "",
        @Json(ignored = true)
        var school: String = "",
        @Json(name = "city")
        var city: String)

object UsersHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allUsers: MutableSet<UserData> = mutableSetOf()
    private val TAG = "LKSH_USER_H"

    data class UsersFromServer(
            val error: String,
            val result: Array<UserData>
    )

    fun initUsers(ctx: Context, useLocalVersion: Boolean = true) {
        doAsync {
            if (!useLocalVersion) {
                while (allUsers.isEmpty()) {
                    if (!forceInitLock)
                        FileController.requestFile(ctx, this@UsersHolder, USERS_DB_FILENAME)
                    forceInitLock = true
                }
            } else {
                FileController.requestFile(ctx, this@UsersHolder, USERS_DB_FILENAME, true)
            }
        }
    }

    fun Context.getUsers(): List<UserData> {
        return allUsers.toList()
    }

    fun Context.getCurrentUser(): UserData {
        return allUsers.find { it.login == Prefs.getInstance(this).userLogin }!!
    }

    fun Context.getUsersByHouse(house: Int): List<UserData> {
        return allUsers.filter { it.house == house }
    }

    fun Context.getUserByLogin(login: String): UserData? {
        return allUsers.find { it.login == login }
    }

    override fun receiveFile(ctx: Context, file: String?) {
        if (file != null) {
            val frServ = Gson().fromJson<UsersFromServer>(file, TypeToken.get(UsersFromServer::class.java).type)
            allUsers = mutableSetOf()
            allUsers.addAll(frServ.result)
            Log.d(TAG, "users loaded")
        } else {
            initUsers(ctx, false)
        }
        forceInitLock = false
    }
}
