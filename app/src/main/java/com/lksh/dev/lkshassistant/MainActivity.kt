package com.lksh.dev.lkshassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.github.chrisbanes.photoview.PhotoView



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val photoView = findViewById<View>(R.id.photo_view) as PhotoView
        photoView.setImageResource(R.drawable.ic_map)
        //photoView.setOnTouchListener(handleTouch)
        
    }


    private val handleTouch = View.OnTouchListener { v, event ->
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> Log.i("TAG", "touched down")

            MotionEvent.ACTION_MOVE -> Log.i("TAG", "moving: ($x, $y)")
            MotionEvent.ACTION_UP -> Log.i("TAG", "touched up")
        }

        true
    }
}


