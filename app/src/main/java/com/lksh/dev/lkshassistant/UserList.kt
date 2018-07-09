package com.lksh.dev.lkshassistant

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_user_list.*

class UserList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
    }

    override fun onResume() {
        super.onResume()
        var usrDataList = DBWrapper.getInstance(this).listUsers("%")
        if(usrDataList.size > 0) {
            var lazyData = ArrayList<String>()
            for (temp in usrDataList){
                lazyData.add(/*temp.ID.toString() + */
                        "Login : "+ temp.login+"\n" +
                        "Name : "+ temp.name +"\n" +
                        "Surname : "+ temp.surname +"\n" +
                        "House : "+ temp.house + "\n" +
                        "Parallel : " +temp.parallel +"\n" +
                        "Password : " + temp.password+"\n" +
                        "Admin : " + temp.admin)
            }
            var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lazyData)
            userlist.adapter = adapter
        }

        add_new.setOnClickListener {
            val intent = Intent(this, AddUser::class.java)
            startActivity(intent)
        }
    }
}
