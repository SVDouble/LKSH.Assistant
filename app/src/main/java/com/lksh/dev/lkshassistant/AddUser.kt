package com.lksh.dev.lkshassistant

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_user.*
import java.util.*

class AddUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        var localDB = DBHandler(this)

        submit_btn.setOnClickListener{
            var a = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            var temppassword = ""
            Array(12) { Random().nextInt(a.length)}.forEach { temppassword += a[it] }
            var templogin = login.text.toString()
            var temphouse = house.text.toString()
            var tempparallel = parallel.text.toString()
            var tempname = name.text.toString()
            var tempsurname = surname.text.toString()
            var tempadmin = 0
            if (templogin == "adminadmin"){
                tempadmin = 1
            }

            var values = ContentValues()
            values.put(DBHandler.login, templogin)
            values.put(DBHandler.password, temppassword)
            values.put(DBHandler.house, temphouse)
            values.put(DBHandler.parallel, tempparallel)
            values.put(DBHandler.name, tempname)
            values.put(DBHandler.surname, tempsurname)
            values.put(DBHandler.admin, tempadmin)
            localDB.addUser(values)

            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }
    }
}
