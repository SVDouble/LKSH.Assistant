package com.lksh.dev.lkshassistant

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_building_info.*

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
        val dataset = DBWrapper.getInstance(context!!).listHouse(houseId ?: "%")
        viewAdapter = UserCardAdapter(context!!, dataset)
        viewAdapter.notifyDataSetChanged()
        recycler.apply {
            layoutManager = GridLayoutManager(context!!, 1)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
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