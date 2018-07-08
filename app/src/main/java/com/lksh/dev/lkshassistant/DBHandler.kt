package com.lksh.dev.lkshassistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "UsersDB"
        const val DB_VERSION = 1
        const val TABLE_NAME = "userTable"
        const val ID = "id"
        const val LOGIN = "login"
        const val PASSWORD = "password"
        const val HOUSE = "first"
        const val PARALLEL = "P"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val ADMIN = "admin"
    }

    private var sqlObj: SQLiteDatabase = this.writableDatabase // Сущность SQLiteDatabase

    override fun onCreate(p0: SQLiteDatabase?) { // Вызывается при генерации БД
        var sql1: String = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ( $ID  INTEGER PRIMARY KEY, $LOGIN TEXT, $PASSWORD TEXT, $HOUSE TEXT, $PARALLEL TEXT, $NAME TEXT, $SURNAME TEXT, $ADMIN INTEGER);"
        p0!!.execSQL(sql1);
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { // Вызывается при обновлении версии БД
        p0!!.execSQL("Drop table IF EXISTS $TABLE_NAME")
        onCreate(p0)
    }

    fun addUser(values: ContentValues) = sqlObj.insert(TABLE_NAME, "", values)

    fun removeUser(id: Int) = sqlObj.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))

    fun updateUser(values: ContentValues, id: Int) = sqlObj.update(TABLE_NAME, values, "id=?", arrayOf(id.toString()))

    fun listUsers(key : String) : ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$ID like ?", selectArgs, null, null, ID)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val login = cursor.getString(cursor.getColumnIndex(LOGIN))
                val password = cursor.getString(cursor.getColumnIndex(PASSWORD))
                val house = cursor.getString(cursor.getColumnIndex(HOUSE))
                val parallel = cursor.getString(cursor.getColumnIndex(PARALLEL))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                val admin = cursor.getInt(cursor.getColumnIndex(ADMIN))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin))

            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listHouse(key : String) : ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$HOUSE like ?", selectArgs, null, null, ID)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val login = cursor.getString(cursor.getColumnIndex(LOGIN))
                val password = cursor.getString(cursor.getColumnIndex(PASSWORD))
                val house = cursor.getString(cursor.getColumnIndex(HOUSE))
                val parallel = cursor.getString(cursor.getColumnIndex(PARALLEL))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                val admin = cursor.getInt(cursor.getColumnIndex(ADMIN))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin))

            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listParallel(key : String) : ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$PARALLEL like ?", selectArgs, null, null, ID)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val login = cursor.getString(cursor.getColumnIndex(LOGIN))
                val password = cursor.getString(cursor.getColumnIndex(PASSWORD))
                val house = cursor.getString(cursor.getColumnIndex(HOUSE))
                val parallel = cursor.getString(cursor.getColumnIndex(PARALLEL))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                val admin = cursor.getInt(cursor.getColumnIndex(ADMIN))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin))

            } while (cursor.moveToNext())
        }
        return arraylist
    }
}
