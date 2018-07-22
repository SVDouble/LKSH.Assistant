package com.lksh.dev.lkshassistant.data

import android.content.Context

const val USERS_DB_FILENAME = "users.json"

data class UserData(var login: String,
                    var name: String,
                    var surname: String,
                    var room: String,
                    var house: String,
                    var parallel: String,
                    var grade: String,
                    var school: String,
                    var city: String)

object UsersHolder {
    private lateinit var allUsers: MutableSet<UserData>

    fun initUsers(ctx: Context) {
        //val data = FileController.requestFile(ctx, USERS_DB_FILENAME)
        // parse data
    }

    fun getUsers(): List<UserData> {
        return allUsers.toList()
    }

    fun getUserByLogin(login: String): UserData? {
        return allUsers.find { it.login == login }
    }

}