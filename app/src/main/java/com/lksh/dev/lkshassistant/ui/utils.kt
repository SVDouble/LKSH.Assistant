package com.lksh.dev.lkshassistant.ui

import android.support.v7.app.AppCompatActivity
import android.view.View

fun setVisibility(element: View, isVisible: Boolean) {
    element.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun hideFragmentById(activity: AppCompatActivity, activityId: Int) {
    activity.supportFragmentManager.beginTransaction().remove(activity.supportFragmentManager.findFragmentById(activityId)).commit()
}