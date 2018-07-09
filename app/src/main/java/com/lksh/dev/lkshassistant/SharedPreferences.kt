package com.lksh.dev.lkshassistant

import android.content.Context
import android.content.SharedPreferences

/* Shared preferences */
class Prefs private constructor(context: Context) {
    private val PREFS_FILENAME = "com.lksh.dev.lkshassistant.prefs"
    private val BOOL_LOGIN_STATE = "login_state"
    private val STRING_LOGIN = "login"
    private val STRING_PASSWORD = "psw"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var login: String
        get() = prefs.getString(STRING_LOGIN, "")
        set(value) = prefs.edit().putString(STRING_LOGIN, value).apply()

    var password: String
        get() = prefs.getString(STRING_PASSWORD, "")
        set(value) = prefs.edit().putString(STRING_PASSWORD, value).apply()

    var loginState: Boolean
        get() = prefs.getBoolean(BOOL_LOGIN_STATE, false)
        set(value) = prefs.edit().putBoolean(BOOL_LOGIN_STATE, value).apply()


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