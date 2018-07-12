package com.lksh.dev.lkshassistant.sqlite_helper

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.R
import org.jetbrains.anko.runOnUiThread
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
        const val CITY = "city"
        const val GRADE = "grade"
        const val SCHOOL = "school"
    }

    private var sqlObj: SQLiteDatabase = this.writableDatabase // Сущность SQLiteDatabase

    override fun onCreate(p0: SQLiteDatabase?) { // Вызывается при генерации БД
        val sql1 = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ( $ID  INTEGER PRIMARY KEY, $LOGIN TEXT, $PASSWORD TEXT, $HOUSE TEXT, $PARALLEL TEXT, $NAME TEXT, $SURNAME TEXT, $ADMIN INTEGER, $ROOM TEXT, $CITY TEXT, $GRADE TEXT, $SCHOOL TEXT );"
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
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM, SCHOOL, GRADE, CITY)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$LOGIN like ?", selectArgs, null, null, SURNAME)

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
                val school = cursor.getString(cursor.getColumnIndex(SCHOOL))
                val grade = cursor.getString(cursor.getColumnIndex(GRADE))
                val city = cursor.getString((cursor.getColumnIndex(CITY)))

                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room, school, city, grade))

            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listHouse(key: String): ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM, SCHOOL, GRADE, CITY)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$HOUSE like ?", selectArgs, null, null, SURNAME)

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
                val school = cursor.getString(cursor.getColumnIndex(SCHOOL))
                val grade = cursor.getString(cursor.getColumnIndex(GRADE))
                val city = cursor.getString((cursor.getColumnIndex(CITY)))

                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room, school, city, grade))



            } while (cursor.moveToNext())
        }
        return arraylist
    }

    fun listParallel(key: String): ArrayList<UserData> {
        val arraylist = ArrayList<UserData>()
        val sqlQB = SQLiteQueryBuilder()
        sqlQB.tables = TABLE_NAME
        val cols = arrayOf(ID, LOGIN, PASSWORD, HOUSE, PARALLEL, NAME, SURNAME, ADMIN, ROOM, SCHOOL, GRADE, CITY)
        val selectArgs = arrayOf(key)

        val cursor = sqlQB.query(sqlObj, cols, "$PARALLEL like ?", selectArgs, null, null, SURNAME)

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
                val school = cursor.getString(cursor.getColumnIndex(SCHOOL))
                val grade = cursor.getString(cursor.getColumnIndex(GRADE))
                val city = cursor.getString((cursor.getColumnIndex(CITY)))

                arraylist.add(UserData(id, login, password, house, parallel, name, surname, admin, room, school, city, grade))



            } while (cursor.moveToNext())
        }
        return arraylist
    }
}

class DBWrapper private constructor() {
    companion object {
        private var listeners: MutableList<DbInteraction> = mutableListOf()
        private var db: DBHandler? = null
        var lockCallbacks = false

        @JvmStatic
        fun getInstance(ctx: Context): DBHandler {
            if (db == null)
                db = DBHandler(ctx)
            return db!!
        }

        @JvmStatic
        fun registerCallback(ctx: Context, lock: Boolean = false) {
            val listener = ctx as? DbInteraction
            if (!lockCallbacks && listener != null) {
                listeners.add(listener)
                lockCallbacks = lock
            }
        }

        @JvmStatic
        fun initDb(ctx: Context, resources: Resources) {
            getInstance(ctx)

            val usrDataList = db!!.listUsers("%")
            val inputStream = resources.openRawResource(R.raw.june2018_pass)
            val lines = BufferedReader(InputStreamReader(inputStream)).readLines().map {
                it.split(",")
            }

            Prefs.getInstance(ctx).dbVersion = 0
            if (usrDataList.size == 0 || lines[0][0].toInt() > Prefs.getInstance(ctx).dbVersion) {
                Prefs.getInstance(ctx).dbVersion = lines[0][0].toInt()
                for (temp in usrDataList) {
                    db!!.removeUser(temp._id)
                }

                val values = ContentValues()
                for (i in 1..(lines.size - 1)) {

                    values.put(DBHandler.LOGIN, lines[i][0])
                    values.put(DBHandler.SURNAME, lines[i][1])
                    values.put(DBHandler.NAME, lines[i][2])
                    values.put(DBHandler.CITY, lines[i][5])
                    values.put(DBHandler.GRADE, lines[i][6])
                    values.put(DBHandler.SCHOOL, lines[i][7])
                    values.put(DBHandler.PARALLEL, lines[i][11])

                    if (lines[i][12] == "ГК2" || lines[i][12] == "ГК1") {
                        values.put(DBHandler.HOUSE, "ГК")
                    } else {
                        values.put(DBHandler.HOUSE, lines[i][12] )
                    }

                    values.put(DBHandler.ROOM, lines[i][13])
                    values.put(DBHandler.PASSWORD, lines[i][15])
                    values.put(DBHandler.ADMIN, "0")
                    db!!.addUser(values)
                }
            }
            ctx.runOnUiThread {
                listeners.forEach { it.onDbLoad() }
            }
        }
    }

    interface DbInteraction {
        fun onDbLoad()
    }
}
