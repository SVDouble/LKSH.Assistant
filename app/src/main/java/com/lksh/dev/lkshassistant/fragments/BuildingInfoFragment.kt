package com.lksh.dev.lkshassistant.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.activities.MainActivity
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import kotlinx.android.synthetic.main.fragment_building_info.*
import kotlinx.android.synthetic.main.part_rv_building.view.*
import org.w3c.dom.Text

private const val ARG_HOUSE_ID = "house_id"

class BuildingInfoFragment : Fragment() {
    private var houseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            houseId = it.getString(ARG_HOUSE_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val block = listOf(contacts)

        contacts.findViewById<TextView>(R.id.header).text = "Контакты"
    }

    override fun onStart() {
        super.onStart()

        view?.background = ColorDrawable(Color.argb(0, 0, 0, 0))
    }

    companion object {
        @JvmStatic
        fun newInstance(houseId: String) =
                BuildingInfoFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOUSE_ID, houseId)
                    }
                }
    }
}
