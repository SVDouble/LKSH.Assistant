package com.lksh.dev.lkshassistant.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.lksh.dev.lkshassistant.JsoupHtml
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.fragments.*
import com.lksh.dev.lkshassistant.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.views.SearchResult
import com.lksh.dev.lkshassistant.views.SearchResultAdapter
import com.lksh.dev.lkshassistant.views.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextFocusChange
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.doAsync

const val TAG = "_LKSH"

class MainActivity : AppCompatActivity(),
        ProfileFragment.OnFragmentInteractionListener,
        FragmentMapSvg.OnFragmentInteractionListener,
        UserListFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        BuildingInfoFragment.OnFragmentInteractionListener,
        TimetableFragment.OnFragmentInteractionListener,
        FragmentMapBox.OnFragmentInteractionListener,
        JsoupHtml.JsoupInteraction {

    private lateinit var infoFragment: InfoFragment

    private lateinit var searchAdapter: SearchResultAdapter

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                header.visibility = GONE
                search.visibility = VISIBLE
                map.setCurrentItem(0, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                header.visibility = VISIBLE
                search.visibility = GONE
                header.text = getString(R.string.nav_title_info)
                map.setCurrentItem(1, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                header.visibility = VISIBLE
                search.visibility = GONE
                header.text = getString(R.string.nav_title_profile)
                map.setCurrentItem(2, false)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {
            JsoupHtml.getInstance(this@MainActivity).shouldParseHtml()
        }

        /* Initialize navigation and pager */
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        infoFragment = InfoFragment()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                arrayOf(FragmentMapBox(), infoFragment, ProfileFragment()))
        map.adapter = mSectionsPagerAdapter
        map.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = when (position) {
                    0 -> R.id.navigation_home
                    1 -> R.id.navigation_dashboard
                    2 -> R.id.navigation_notifications
                    else -> throw IndexOutOfBoundsException("Wrong!!!")
                }
            }
        })
        map.swipingEnabled = false
        header.visibility = GONE

        /* Initialize search */
        search.isSubmitButtonEnabled = false
        search.queryHint = "Enter user or building"
        search.setOnClickListener {
            search.isIconified = false
            map.visibility = GONE
            search_results.visibility = VISIBLE
        }
        search.onQueryTextFocusChange { v, hasFocus ->
            if (hasFocus) {
                search.onActionViewExpanded()
                search.isIconified = false
                search_results.visibility = VISIBLE
                map.visibility = GONE
            } else {
                search.onActionViewCollapsed()
                search.isIconified = true
                search_results.visibility = GONE
                map.visibility = VISIBLE
            }
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

        })

        /* Search results init */
        val users = DBWrapper.getInstance(this).listUsers("%")
        val dataset = Array(users.size) { i -> SearchResult(SearchResult.Type.USER, "${users[i].name} ${users[i].surname}") }
        searchAdapter = SearchResultAdapter(this, dataset)
        searchAdapter.notifyDataSetChanged()
        search_results.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = searchAdapter
        }
    }

    override fun onFragmentInteraction(uri: Uri) {}

    override fun timetableLoaded() {
        Log.d(TAG, "MAIN: timetable update")
        infoFragment.onTimetableUpdate()
    }

    fun hideFragment() {
        supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.activity_main)).commit()
    }
}

interface TimetableInteraction {
    fun onTimetableUpdate()
}
