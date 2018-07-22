package com.lksh.dev.lkshassistant.ui.activities

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
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.data.UsersHolder.getUsers
import com.lksh.dev.lkshassistant.houseCoordinates
import com.lksh.dev.lkshassistant.ui.fragments.InfoFragment
import com.lksh.dev.lkshassistant.ui.fragments.MapBoxFragment
import com.lksh.dev.lkshassistant.ui.fragments.ProfileFragment
import com.lksh.dev.lkshassistant.ui.setVisibility
import com.lksh.dev.lkshassistant.ui.views.SearchResult
import com.lksh.dev.lkshassistant.ui.views.SearchResultAdapter
import com.lksh.dev.lkshassistant.ui.views.SectionsPagerAdapter
import com.lksh.dev.lkshassistant.web.JsoupHtml
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextFocusChange
import org.jetbrains.anko.doAsync

const val TAG = "_LKSH"

class MainActivity : AppCompatActivity(),
        JsoupHtml.JsoupInteraction {

    private lateinit var infoFragment: InfoFragment
    private lateinit var mapBoxFragment: MapBoxFragment
    private lateinit var searchAdapter: SearchResultAdapter
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

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
            JsoupHtml.getInstance(this@MainActivity).parseHtml()
        }

        /* Initialize navigation and pager */
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        infoFragment = InfoFragment()
        mapBoxFragment = MapBoxFragment()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                arrayOf(mapBoxFragment, infoFragment, ProfileFragment()))
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
        search.queryHint = "Введите имя ученика или номер домика"
        search.setOnClickListener {
            search.isIconified = false
            map.visibility = GONE
            search_rv.visibility = VISIBLE
        }
        search.onQueryTextFocusChange { _, hasFocus ->
            if (hasFocus) {
                search.onActionViewExpanded()
                search.isIconified = false
                search_rv.visibility = VISIBLE
                map.visibility = GONE
            } else {
                search.onActionViewCollapsed()
                search.isIconified = true
                search_rv.visibility = GONE
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
        val users = getUsers().toList()
        val dataset = arrayListOf<SearchResult>()
        houseCoordinates.mapTo(dataset)
        { SearchResult(SearchResult.Type.HOUSE, null, it) }
        users.mapTo(dataset) { SearchResult(SearchResult.Type.USER, it, null) }
        searchAdapter = SearchResultAdapter(this, dataset,
                object : SearchResultAdapter.OnHouseClickListener {
                    override fun onSearchResultClick(houseId: String) {
                        search.clearFocus()
                        mapBoxFragment.setPosByHouseName(houseId)
//                        supportFragmentManager.beginTransaction().add(R.id.activity_main,
//                                BuildingInfoFragment.newInstance(houseId)).commit()
                    }
                })
        searchAdapter.notifyDataSetChanged()
        search_rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = searchAdapter
        }
    }

    override fun timetableLoaded() {
        Log.d(TAG, "MAIN: timetable update")
        infoFragment.onTimetableUpdate()
    }
}

interface TimetableInteraction {
    fun onTimetableUpdate()
}
