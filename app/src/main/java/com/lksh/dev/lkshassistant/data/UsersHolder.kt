package com.lksh.dev.lkshassistant.data

import android.content.Context
import com.beust.klaxon.Klaxon
import com.lksh.dev.lkshassistant.ui.activities.OnResourseLoad
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
    private lateinit var allUsers: MutableSet<UserData>

    fun Context.forceInitUsers(listener: OnResourseLoad) {
        doAsync {
            while (!::allUsers.isInitialized) {
                if (!forceInitLock)
                    FileController.requestFile(this@forceInitUsers, this@UsersHolder, USERS_DB_FILENAME)
                forceInitLock = true
            }
            listener.resolveDependencies("UsersHolder")
        }
    }

    fun getUsers(): List<UserData> {
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