package com.lksh.dev.lkshassistant.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.Prefs
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.activities.TimetableInteraction
import com.lksh.dev.lkshassistant.views.TimetableAdapter
import com.lksh.dev.lkshassistant.views.TimetableEvent
import kotlinx.android.synthetic.main.fragment_timetable.*

class TimetableFragment : Fragment(), TimetableInteraction {

    private lateinit var timetableAdapter: TimetableAdapter
    private var dataset: Array<TimetableEvent> = arrayOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onTimetableUpdate() {
        val tt = Prefs.getInstance(context!!).timetable.split("\n")
        timetable_info.text = "Info"
        dataset = Array(tt.size) { i -> TimetableEvent(tt[i].substringBefore(" "), tt[i].substringAfter(" ")) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Search results init */
        val dataset = arrayOf<TimetableEvent>()
        timetableAdapter = TimetableAdapter(context!!, dataset)
        timetableAdapter.notifyDataSetChanged()
        timetable_recycler.apply {
            layoutManager = LinearLayoutManager(context!!)
            itemAnimator = DefaultItemAnimator()
            adapter = timetableAdapter
        }
    }
}