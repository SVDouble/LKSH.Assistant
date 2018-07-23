package com.lksh.dev.lkshassistant.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.UsersHolder.getCurrentUser
import kotlinx.android.synthetic.main.activity_user_profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val user = getCurrentUser()
        profile_name.text = "${getString(R.string.preName)} ${user.name}"
        profile_surname.text = "${getString(R.string.preSurname)} ${user.surname}"
        profile_city.text = "${getString(R.string.preCity)} ${user.city}"
        profile_parallel.text = "${getString(R.string.preParallel)} ${user.parallel}"
        profile_house.text = "${getString(R.string.preHouse)} ${user.house_id}"
        profile_room.text = "${getString(R.string.preRoom)} ${user.room}"
    }
}
