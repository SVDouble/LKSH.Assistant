@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package com.lksh.dev.lkshassistant.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.Prefs
import com.lksh.dev.lkshassistant.ui.activities.TimetableInteraction
import com.lksh.dev.lkshassistant.ui.views.TimetableAdapter
import com.lksh.dev.lkshassistant.ui.views.TimetableEvent
import com.lksh.dev.lkshassistant.web.JsoupHtml
import kotlinx.android.synthetic.main.fragment_timetable.*
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import kotlinx.coroutines.experimental.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast

class TimetableFragment : Fragment(), TimetableInteraction {

    private lateinit var timetableAdapter: TimetableAdapter
    private var timetable = ""
    private var dataset: ArrayList<TimetableEvent> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val v = inflater.inflate(R.layout.fragment_timetable, container, false)
        try {
            v.refresher.onRefresh {
                launch {
                    context?.apply {
                        JsoupHtml(context!!).parseHtml()
                        context!!.runOnUiThread {
                            v.refresher.isRefreshing = false
                        }
                    }
                    Thread.sleep(5000)
                    context!!.runOnUiThread {
                        v.refresher.isRefreshing = false
                    }
                }
            }
        } catch (e : Exception) {}
        return v
    }

    override fun onResume() {
        super.onResume()
        onTimetableUpdate()
    }

    override fun onTimetableUpdate() {
        val ctx = context
        if (ctx != null) {
            timetable = Prefs.getInstance(ctx).timetable
            updateRecycler()
        }
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
        dataset.clear()
        val data = Array(tt.size) { i -> TimetableEvent(tt[i].substringBefore(" "), tt[i].substringAfter(" ")) }
        dataset.addAll(data.filter { !dataset.contains(it) })
        timetableAdapter.notifyDataSetChanged()
    }
}