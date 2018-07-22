package com.lksh.dev.lkshassistant.ui.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.UserData
import com.lksh.dev.lkshassistant.ui.activities.MainActivity
import com.lksh.dev.lkshassistant.ui.hideFragmentById
import kotlinx.android.synthetic.main.fragment_building_info.*

class BuildingInfoHouseFragment : Fragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.text = houseId
        content_focusable.setOnClickListener {
            hideFragmentById(activity as MainActivity, R.id.activity_main)
        }
        //        val dataset = DBWrapper.getInstance(context!!)
        //                .listHouse(houseId ?: "%")
        //                .sortedBy { it.room.toIntOrNull() ?: 0 }
        val dataset = listOf<UserData>()
        table.isStretchAllColumns = false
        table.bringToFront()

        table.addView(createBuildingInfoPart("№", "name", "parallel", "room"), 0)

        dataset.forEachIndexed { i, data ->
            table.addView(createBuildingInfoPart(
                    (i + 1).toString(),
                    "${data.name} ${data.surname}",
                    data.parallel,
                    data.room
            ), i + 1)
        }
    }

    private fun createBuildingInfoPart(number: String, name: String, parallel: String, room: String) =
            layoutInflater.inflate(R.layout.part_rv_building, table, false)
                    .apply {
                        findViewById<TextView>(R.id.number).text = number
                        findViewById<TextView>(R.id.name).text = name
                        findViewById<TextView>(R.id.parallel).text = parallel
                        findViewById<TextView>(R.id.room).text = room
                    }

    override fun onStart() {
        super.onStart()

        view?.background = ColorDrawable(Color.argb(100, 0, 0, 0))
    }

    companion object {
        private const val ARG_HOUSE_ID = "house_id"

        fun newInstance(houseId: String) =
                BuildingInfoHouseFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_HOUSE_ID, houseId)
                    }
                }
    }
}
