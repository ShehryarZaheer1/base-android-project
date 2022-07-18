package com.xevensolutions.baseapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.xevensolutions.baseapp.R

class ActivityImage : AppCompatActivity() {

    lateinit var imageView: ImageView

    companion object {
        fun startActivity(activity: Activity, url: String) {
            var intent = Intent(activity, ActivityImage::class.java)
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        imageView = findViewById(R.id.image_view)
        val url = intent.getStringExtra("url")

        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)

    }
}