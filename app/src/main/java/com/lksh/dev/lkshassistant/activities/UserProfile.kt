package com.lksh.dev.lkshassistant.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lksh.dev.lkshassistant.R
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        Log.d(TAG, "Profile loaded")
        val fullname = intent.getStringArrayExtra("USER")

        profile_name.text = "${getString(R.string.preName)} ${fullname[1]}"
        profile_surname.text = "${getString(R.string.preSurname)} ${fullname[2]}"
        profile_city.text = "${getString(R.string.preCity)} ${fullname[3]}"
        profile_parallel.text = "${getString(R.string.preParallel)} ${fullname[4]}"
        profile_house.text = "${getString(R.string.preHouse)} ${fullname[5]}"
        profile_room.text = "${getString(R.string.preRoom)} ${fullname[6]}"

        //showOnMap.setOnClickListener{
        //    startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("FLAG_OPEN_MAP", fullname[5]))
          //  finish()
        //}
    }
}
