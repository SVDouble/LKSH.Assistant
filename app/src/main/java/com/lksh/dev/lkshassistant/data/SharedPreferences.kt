package com.lksh.dev.lkshassistant.data

import android.content.Context
import android.content.SharedPreferences

/* Shared preferences */
class Prefs private constructor(context: Context) {
    private val PREFS_FILENAME = "com.lksh.dev.lkshassistant.prefs"

    private val BOOL_LOGIN_STATE = "bool_login_state"
    private val STRING_TIMETABLE = "string_timetable"
    private val STRING_USER_TOKEN = "string_user_token"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(BOOL_LOGIN_STATE, false)
        set(value) = prefs.edit().putBoolean(BOOL_LOGIN_STATE, value).apply()

    var userToken: String
        get() = prefs.getString(STRING_USER_TOKEN, "")
        set(value) = prefs.edit().putString(STRING_USER_TOKEN, value).apply()

    var timetable: String
        get() = prefs.getString(STRING_TIMETABLE, "")
        set(value) = prefs.edit().putString(STRING_TIMETABLE, value).apply()

    companion object : SingletonHolder<Prefs, Context>(::Prefs)
}

/* Source: https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}