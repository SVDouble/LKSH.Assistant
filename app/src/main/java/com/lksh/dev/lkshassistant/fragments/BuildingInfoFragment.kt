package com.lksh.dev.lkshassistant.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.activities.MainActivity
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.views.UserCardAdapter
import kotlinx.android.synthetic.main.fragment_building_info.*
import kotlinx.android.synthetic.main.part_rv_building.view.*

private const val ARG_HOUSE_ID = "house_id"

class BuildingInfoFragment : Fragment() {
    private var houseId: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var viewAdapter: UserCardAdapter

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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        header.text = if (houseId == null) "All users" else ("House " + houseId)
        content_focusable.setOnClickListener {
            (activity as? MainActivity)?.hideFragment()
        }
        val dataset = DBWrapper.getInstance(context!!)
                .listHouse(houseId ?: "%")
                .sortedBy { it.room.toIntOrNull() ?: 0 }
//        viewAdapter = UserCardAdapter(context!!, dataset)
//        viewAdapter.notifyDataSetChanged()
//        recycler.apply {
//            layoutManager = GridLayoutManager(context!!, 1)
//            itemAnimator = DefaultItemAnimator()
//            adapter = viewAdapter
//        }
        table.isStretchAllColumns = false
        table.bringToFront()


        table.addView(layoutInflater.inflate(R.layout.part_rv_building, null, false)
                .apply {
                    number.text = "№"
                    name.text = "name"
                    parallel.text = "parallel"
                    room.text = "room"
                }, 0)

        dataset.forEachIndexed { i, data ->
            table.addView(layoutInflater.inflate(R.layout.part_rv_building, null, false)
                    .apply {
                        number.text = (i + 1).toString()
                        name.text = "${data.name} ${data.surname}"
                        parallel.text = data.parallel
                        room.text = data.room
                    }, i + 1)

        }
    }

    override fun onStart() {
        super.onStart()

        view?.background = ColorDrawable(Color.argb(100, 0, 0, 0))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
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
