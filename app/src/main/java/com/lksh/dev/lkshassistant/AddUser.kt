package com.lksh.dev.lkshassistant

import android.content.ContentValues
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_user.*

class AddUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        var localDB = DBHandler(this)

        submit_btn.setOnClickListener{
            var templogin = login.text.toString()
            var temppassword = password.text.toString()
            var temphouse = house.text.toString()
            var tempparallel = house.text.toString()

            var values = ContentValues()
            values.put(DBHandler.login, templogin)
            values.put(DBHandler.password, temppassword)
            values.put(DBHandler.house, temphouse)
            values.put(DBHandler.parallel, tempparallel)
            localDB.addUser(values)

            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }
    }
}
