package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon
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
        if (file != null)
            allUsers = Klaxon().parse<MutableSet<UserData>>(file)!!
        forceInitLock = false
    }
}