package com.lksh.dev.lkshassistant.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.UserData
import com.lksh.dev.lkshassistant.ui.activities.LoginActivity
import com.lksh.dev.lkshassistant.ui.activities.MainActivity
import com.lksh.dev.lkshassistant.web.Auth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        val user = DBWrapper.getInstance(context!!).listUsers(Prefs.getInstance(context!!).login)[0]
        val user = UserData()

        profile_name.text = "Name: ${user.name}"
        profile_surname.text = "Surname: ${user.surname}"
        profile_parallel.text = "Parallel: ${user.parallel}"
        profile_house.text = "House: ${user.house_id}"
        info_logout.setOnClickListener {
            Auth.logout(context!!)
            (activity as? MainActivity)?.finish()
            startActivity(Intent(context, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
}
