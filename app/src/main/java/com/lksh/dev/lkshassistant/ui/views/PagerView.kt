package com.lksh.dev.lkshassistant.ui.views

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = mutableListOf<Fragment>()

    constructor(fm: FragmentManager, fragments: Array<Fragment>) : this(fm) {
        this.fragments.addAll(fragments)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount() = fragments.size
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
