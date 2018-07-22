package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync

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

object UsersHolder : FileController.GetFileListener {
    private var forceInitLock = false
    private var allUsers: MutableSet<UserData> = mutableSetOf()
    private val TAG = "LKSH_USER_H"

    data class UsersFromServer(
            val error: String,
            val result: Array<User>
    ) {
        data class User(
                val name: String,
                val login: String,
                val parallel: String,
                val city: String,
                val lat: Double,
                val long: Double
        )
    }

    fun initUsers(ctx: Context) {
        doAsync {
            while (allUsers.isEmpty()) {
                if (!forceInitLock)
                    FileController.requestFile(ctx, this@UsersHolder, USERS_DB_FILENAME)
                forceInitLock = true
            }
        }
    }

    fun Context.getUsers(): List<UserData> {
        return allUsers.toList()
    }

    fun Context.getCurrentUser(): UserData {
        return allUsers.find { it.login == Prefs.getInstance(this).userLogin }!!
    }

    fun Context.getUserByLogin(login: String): UserData? {
        return allUsers.find { it.login == login }
    }

    override fun receiveFile(file: String?) {
        if (file != null) {
            val frServ = Gson().fromJson<UsersFromServer>(file, TypeToken.get(UsersFromServer::class.java).type)
            allUsers = mutableSetOf()
            frServ.result.forEach {
                allUsers.add(UserData(login = it.login, name = it.name, parallel = it.parallel, city = it.city,

                        //TODO: repair output from db this items:
                        surname = "", room = "", grade = "", house = "", school = ""
                        ))
            }
            Log.d(TAG, "users loaded")
        }
        forceInitLock = false
    }
}