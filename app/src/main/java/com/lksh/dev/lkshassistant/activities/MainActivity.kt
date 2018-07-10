package com.lksh.dev.lkshassistant.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.View.GONE
import android.view.View.VISIBLE
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.fragments.*
import com.lksh.dev.lkshassistant.views.SearchResult
import com.lksh.dev.lkshassistant.views.SearchResultAdapter
import com.lksh.dev.lkshassistant.views.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "_LKSH"

class MainActivity : AppCompatActivity(),
        ProfileFragment.OnFragmentInteractionListener,
        FragmentMapSvg.OnFragmentInteractionListener,
        UserListFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        BuildingInfoFragment.OnFragmentInteractionListener,
        TimetableFragment.OnFragmentInteractionListener,
        FragmentMapBox.OnFragmentInteractionListener {

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

        /* Request permissions */
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                arrayOf(FragmentMapBox(), InfoFragment(), ProfileFragment()))
        map.adapter = mSectionsPagerAdapter
        /* Handle bottom navigation clicks */
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

        /* Initializate search */
        search.setOnClickListener {
            (it as SearchView).isIconified = false
            map.visibility = GONE
            search_results.visibility = VISIBLE
        }

        search.setOnCloseListener {
            search_results.visibility = GONE
            map.visibility = VISIBLE
            true
        }
        //search.isActivated = true
        search.queryHint = "Enter user or building"
        //search.onActionViewExpanded()
        //search.isIconified = false
        //search.clearFocus()
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
        val dataset = arrayListOf(SearchResult(SearchResult.Type.USER, "Arkadiy"), SearchResult(SearchResult.Type.USER, "Gregoriy"))
        searchAdapter = SearchResultAdapter(this, dataset)
        searchAdapter.notifyDataSetChanged()
        search_results.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = searchAdapter
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    fun hideFragment() {
        supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.activity_main)).commit()
    }

}
