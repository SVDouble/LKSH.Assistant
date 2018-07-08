package com.lksh.dev.lkshassistant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DBHandler(context: Context) : SQLiteOpenHelper(context, DBName, null, DBVersion) {

    companion object {
        val DBName = "UsersDB"
        val DBVersion = 1
        val tableName = "userTable"
        val _id = "id"
        val login = "login"
        val password = "password"
        var house = "first"
        var parallel = "P"
        var name = "name"
        var surname = "surname"
        var admin = "admin"
    }

    var sqlObj: SQLiteDatabase = this.writableDatabase // Сущность SQLiteDatabase

    override fun onCreate(p0: SQLiteDatabase?) { // Вызывается при генерации БД
        var sql1: String = "CREATE TABLE IF NOT EXISTS $tableName ( $_id  INTEGER PRIMARY KEY, $login TEXT, $password TEXT, $house TEXT, $parallel TEXT, $name TEXT, $surname TEXT, $admin INTEGER);"
        p0!!.execSQL(sql1);
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { // Вызывается при обновлении версии БД
        p0!!.execSQL("Drop table IF EXISTS $tableName")
        onCreate(p0)
    }

    fun addUser(values: ContentValues) = sqlObj.insert(tableName, "", values)

    fun removeUser (id: Int) = sqlObj.delete(tableName, "id=?", arrayOf(id.toString()))

    fun updateUser(values: ContentValues, id: Int) = sqlObj.update(tableName, values, "id=?", arrayOf(id.toString()))

    fun listUsers(key : String) : ArrayList<UserData> {
        var arraylist = ArrayList<UserData>()
        var sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = tableName
        var cols = arrayOf(_id, login, password, house, parallel, name, surname, admin)
        var selectArgs = arrayOf(key)

        var cursor = sqlQB.query(sqlObj, cols,"$_id like ?", selectArgs,null,null, _id)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(_id))
                val login = cursor.getString(cursor.getColumnIndex(login))
                val password = cursor.getString(cursor.getColumnIndex(password))
                val house = cursor.getString(cursor.getColumnIndex(house))
                val parallel = cursor.getString(cursor.getColumnIndex(parallel))
                val name = cursor.getString(cursor.getColumnIndex(name))
                val surname = cursor.getString(cursor.getColumnIndex(surname))
                val admin = cursor.getInt(cursor.getColumnIndex(admin))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin))

            } while (cursor.moveToNext())
        }
        return arraylist
    }


}
