package com.lksh.dev.lkshassistant.ui

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver

fun setVisibility(element: View, isVisible: Boolean) {
    element.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun hideFragmentById(activity: AppCompatActivity, activityId: Int) {
    activity.supportFragmentManager.beginTransaction().remove(activity.supportFragmentManager.findFragmentById(activityId)).commit()
}

/* Keyboard events: open and close */
fun setKeyboardVisibilityListener(activity: Activity, keyboardVisibilityListener: KeyboardVisibilityListener) {
    val contentView = activity.findViewById<View>(android.R.id.content)
    var mAppHeight: Int = 0
    var currentOrientation = -1
    contentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        private var mPreviousHeight: Int = 0
        override fun onGlobalLayout() {
            val newHeight = contentView.height
            if (newHeight == mPreviousHeight)
                return
            mPreviousHeight = newHeight
            if (activity.resources.configuration.orientation != currentOrientation) {
                currentOrientation = activity.resources.configuration.orientation
                mAppHeight = 0
            }
            if (newHeight >= mAppHeight) {
                mAppHeight = newHeight
            }
            if (newHeight != 0) {
                if (mAppHeight > newHeight) {
                    keyboardVisibilityListener.onKeyboardVisibilityChanged(true)
                } else {
                    keyboardVisibilityListener.onKeyboardVisibilityChanged(false)
                }
            }
        }
    })
}

interface KeyboardVisibilityListener {
    fun onKeyboardVisibilityChanged(keyboardVisible: Boolean)
}