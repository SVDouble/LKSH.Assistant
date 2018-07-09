package com.lksh.dev.lkshassistant

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import com.lksh.dev.lkshassistant.Fragments.*
import com.lksh.dev.lkshassistant.Views.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "_LKSH"

class MainActivity : AppCompatActivity(),
        ProfileFragment.OnFragmentInteractionListener,
        FragmentMap.OnFragmentInteractionListener,
        UserListFragment.OnFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener,
        BuildingInfoFragment.OnFragmentInteractionListener {

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

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,
                arrayOf(FragmentMap(), InfoFragment(), ProfileFragment()))
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
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    fun hideFragment() {
        supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.activity_main)).commit()
    }

}
