package com.lksh.dev.lkshassistant

import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.BottomNavigationView
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.util.AttributeSet



class MainActivity : AppCompatActivity(), ProfileFragment.OnFragmentInteractionListener, FragmentMap.OnFragmentInteractionListener, UserListFragment.OnFragmentInteractionListener {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val mOnNavigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                map.setCurrentItem(0, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                map.setCurrentItem(1, false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
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

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
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
    }

    class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val fragments = mutableListOf<Fragment>()

        init {
            fragments.addAll(arrayOf(FragmentMap(), UserListFragment(), ProfileFragment()))
        }


        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount() = fragments.size
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}

class NoSwipePager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
    var swipingEnabled: Boolean = false

    init {
        this.swipingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipingEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }
}
