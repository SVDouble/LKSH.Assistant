package com.lksh.dev.lkshassistant.sqlite_helper

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

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
        const val ROOM = "room"
    }

    private var sqlObj: SQLiteDatabase = this.writableDatabase // Сущность SQLiteDatabase

    override fun onCreate(p0: SQLiteDatabase?) { // Вызывается при генерации БД
        val sql1 = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ( $ID  INTEGER PRIMARY KEY, $LOGIN TEXT, $PASSWORD TEXT, $HOUSE TEXT, $PARALLEL TEXT, $NAME TEXT, $SURNAME TEXT, $ADMIN INTEGER, $ROOM TEXT);"
        p0!!.execSQL(sql1)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { // Вызывается при обновлении версии БД
        p0!!.execSQL("Drop table IF EXISTS $TABLE_NAME")
        onCreate(p0)
    }

    fun addUser(values: ContentValues) = sqlObj.insert(TABLE_NAME, "", values)

    fun removeUser(id: Int) = sqlObj.delete(TABLE_NAME, "id=?", arrayOf(id.toString()))

    fun updateUser(values: ContentValues, id: Int) = sqlObj.update(TABLE_NAME, values, "id=?", arrayOf(id.toString()))

    fun listUsers(key: String): ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$LOGIN like ?", selectArgs, null, null, LOGIN)

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
                val room = cursor.getString((cursor.getColumnIndex(ROOM)))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room))

            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listHouse(key: String): ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM)
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
                val room = cursor.getString((cursor.getColumnIndex(ROOM)))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room))


            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listParallel(key: String): ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM)
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
                val room = cursor.getString((cursor.getColumnIndex(ROOM)))
                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room))


            } while (cursor.moveToNext())
        }
        return arraylist
    }
}

class DBWrapper private constructor() {
    companion object {
        var db: DBHandler? = null

        @JvmStatic
        fun getInstance(ctx: Context): DBHandler {
            if (db == null)
                db = DBHandler(ctx)
            return db!!
        }

    }
}

fun initDb(ctx: Context, db: DBHandler, resources: Resources) {
    var usrDataList = db.listUsers("%")

    val inputStream = resources.openRawResource(R.raw.june2018_pass)                                 //file reading
    val lines = BufferedReader(InputStreamReader(inputStream)).readLines().map {
        it.split(",")
    }

    Prefs.getInstance(ctx).dbVersion = 0
    if (usrDataList.size == 0 || lines[0][0].toInt() > Prefs.getInstance(ctx).dbVersion) {
        //var a = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
        Prefs.getInstance(ctx).dbVersion = lines[0][0].toInt()
        for (temp in usrDataList) {
            db.removeUser(temp._id)
        }

        for (temp in usrDataList) {

        }
        var temppassword = ""
        var templogin = ""
        var temphouse = ""
        var tempparallel = ""
        var tempname = ""
        var tempsurname = ""
        var tempadmin = ""
        var temproom = ""
        val values = ContentValues()


        for (i in 1..(lines.size - 1)) {                   //put into db
            temppassword = lines[i][15]
            templogin = lines[i][0]
            temphouse = lines[i][12]
            tempparallel = lines[i][11]
            tempname = lines[i][2]
            tempsurname = lines[i][1]
            tempadmin = "0"
            temproom = lines[i][13]
            //Array(12) { Random().nextInt(a.length)}.forEach { temppassword += a[it] }

            values.put(DBHandler.LOGIN, templogin)
            values.put(DBHandler.PASSWORD, temppassword)
            values.put(DBHandler.HOUSE, temphouse)
            values.put(DBHandler.PARALLEL, tempparallel)
            values.put(DBHandler.NAME, tempname)
            values.put(DBHandler.SURNAME, tempsurname)
            values.put(DBHandler.ADMIN, tempadmin)
            values.put(DBHandler.ROOM, temproom)
            db.addUser(values)
        }
    }
}


