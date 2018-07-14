package com.lksh.dev.lkshassistant.presentation.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.domain.Prefs
import com.lksh.dev.lkshassistant.presentation.activities.TimetableInteraction
import com.lksh.dev.lkshassistant.presentation.views.TimetableAdapter
import com.lksh.dev.lkshassistant.presentation.views.TimetableEvent
import kotlinx.android.synthetic.main.fragment_timetable.*

class TimetableFragment : Fragment(), TimetableInteraction {

    private lateinit var timetableAdapter: TimetableAdapter
    private var timetable = ""
    private var dataset: ArrayList<TimetableEvent> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onResume() {
        super.onResume()
        timetable = Prefs.getInstance(context!!).timetable
        updateRecycler()
    }

    override fun onTimetableUpdate() {
        timetable = Prefs.getInstance(context!!).timetable
        updateRecycler()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Search results init */
        timetableAdapter = TimetableAdapter(context!!, dataset)
        timetableAdapter.notifyDataSetChanged()
        timetable_recycler.apply {
            layoutManager = LinearLayoutManager(context!!)
            itemAnimator = DefaultItemAnimator()
            adapter = timetableAdapter
        }
    }

    private fun updateRecycler() {
        val tt = timetable.split("\n")
        val data = Array(tt.size) { i -> TimetableEvent(tt[i].substringBefore(" "), tt[i].substringAfter(" ")) }
        dataset.addAll(data.filter { !dataset.contains(it) })
        timetableAdapter.notifyDataSetChanged()
    }
}