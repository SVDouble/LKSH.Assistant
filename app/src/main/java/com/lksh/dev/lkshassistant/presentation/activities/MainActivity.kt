package com.lksh.dev.lkshassistant.presentation.activities

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.sqlite_helper.DBWrapper
import com.lksh.dev.lkshassistant.domain.JsoupHtml
import com.lksh.dev.lkshassistant.domain.houseCoordinates
import com.lksh.dev.lkshassistant.presentation.fragments.*
import com.lksh.dev.lkshassistant.presentation.views.SearchResult
import com.lksh.dev.lkshassistant.presentation.views.SearchResultAdapter
import com.lksh.dev.lkshassistant.presentation.views.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextFocusChange
import org.jetbrains.anko.doAsync


const val TAG = "_LKSH"

class MainActivity : AppCompatActivity(),
        FragmentMapSvg.OnFragmentInteractionListener,
        JsoupHtml.JsoupInteraction {

    private lateinit var infoFragment: InfoFragment
    private lateinit var searchAdapter: SearchResultAdapter
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private val NAV_BAR_PAGES_COUNT = 3

    private fun setVisibility(element: View, isVisible: Boolean) {
        if (isVisible) {
            element.visibility = VISIBLE
        } else {
            element.visibility = GONE
        }
    }

    private fun prepareElements(isHeaderVisible: Boolean, isSearchVisible: Boolean, headerTitle: String = "", smoothElement: Int = 0) {

        setVisibility(header, isHeaderVisible)
        setVisibility(search, isSearchVisible)

        header.text = headerTitle

        map.setCurrentItem(smoothElement, false)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                prepareElements(false, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                prepareElements(true, false, getString(R.string.nav_title_info), 1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                prepareElements(true, false, getString(R.string.nav_title_profile), 2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "MainActivity: launched")

        /* Initialize tmetable */
        doAsync {
            JsoupHtml.getInstance(this@MainActivity).shouldParseHtml()
        }

        /* Initialize navigation and pager */
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        infoFragment = InfoFragment()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                arrayOf(MapBoxFragment(), infoFragment, ProfileFragment()))
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

        /* Search init */
        searchInit()
        searchResultsInit()
    }

    private fun searchInit() {
        search.isSubmitButtonEnabled = false
        search.queryHint = "Введите имя ученика"
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
    }

    private fun searchResultsInit() {
        val users = DBWrapper.getInstance(this).listUsers("%")
        val dataset = arrayListOf<SearchResult>()
        houseCoordinates.mapTo(dataset) { SearchResult(SearchResult.Type.HOUSE, null, it) }
        users.mapTo(dataset) { SearchResult(SearchResult.Type.USER, it, null) }
        searchAdapter = SearchResultAdapter(this, dataset,
                object : SearchResultAdapter.OnHouseClickListener {
                    override fun onCLick(houseId: String) {
                        search.clearFocus()
                        supportFragmentManager.beginTransaction().add(R.id.activity_main,
                                BuildingInfoFragment.newInstance(houseId)).commit()
                    }
                })
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
