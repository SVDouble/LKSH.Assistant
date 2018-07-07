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

        photoView.setOnPhotoTapListener { view, x, y ->
            Log.i("TAG", "$x, $y")
        }
    }


}


