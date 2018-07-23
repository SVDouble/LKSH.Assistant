package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.beust.klaxon.Json
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync

data class UserData(
        @Json(name = "name")
        var name: String = "name",

        @Json(name = "surname")
        var surname: String = "surname",

        @Json(name = "login")
        var login: String = "login",

        @Json(name = "parallel")
        var parallel: String = "parallel",

        @Json(name = "city")
        var city: String = "city",

        @Json(name = "room")
        var room: String = "room",

        @Json(name = "house_id")
        var house_id: Int = -1,

        @Json(ignored = true)
        var grade: String = "grade",

        @Json(ignored = true)
        var school: String = "school"
)

object UsersHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allUsers: MutableSet<UserData> = mutableSetOf()
    private val TAG = "_LKSH"

    data class UsersFromServer(
            val error: String,
            val result: Array<UserData>
    )

    fun initUsers(ctx: Context, useLocalVersion: Boolean = true) {
        doAsync {
            if (!useLocalVersion) {
                do {
                    if (!forceInitLock)
                        FileController.requestFile(ctx, this@UsersHolder, USERS_DB_FILENAME)
                    forceInitLock = true
                } while (allUsers.isEmpty())
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
        return allUsers.filter { it.house_id == house }
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
            Log.d(TAG, "Users aren't cached, try to download json from server...")
            initUsers(ctx, false)
        }
        forceInitLock = false
    }
}
