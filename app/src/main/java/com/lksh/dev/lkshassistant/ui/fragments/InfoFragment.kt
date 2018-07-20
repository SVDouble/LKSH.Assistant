package com.lksh.dev.lkshassistant.ui.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.ui.activities.TimetableInteraction
import com.lksh.dev.lkshassistant.ui.views.SectionsPagerAdapter
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : Fragment(), TimetableInteraction {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var timetableFragment: TimetableFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timetableFragment = TimetableFragment()
        mSectionsPagerAdapter = SectionsPagerAdapter(activity!!.supportFragmentManager,
                arrayOf(timetableFragment, BookletInformation()))

        /* Set up the ViewPager with the sections adapter. */
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onTimetableUpdate() {
        timetableFragment.onTimetableUpdate()
    }
}