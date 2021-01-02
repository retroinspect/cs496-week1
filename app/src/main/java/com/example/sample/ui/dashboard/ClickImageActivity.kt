package com.example.sample.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.sample.R

class ClickImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.click_image)
        val intent : Intent = getIntent()

        var imageOne = findViewById(R.id.image_item_one) as SubsamplingScaleImageView
        var editButton = findViewById(R.id.image_title_edit_button) as Button
        var shareButton = findViewById(R.id.image_share_button) as Button

        val imageUriString = intent.getStringExtra("image_uri")
        if (imageUriString != null) {
            val imageUri : Uri = imageUriString.toUri()
            val imageTitle : String? = intent.getStringExtra("image_title")
            imageOne.setImage(ImageSource.uri(imageUri))
        }

        /*
        editButton.setOnClickListener {
        }
        shareButton.setOnClickListener {
        }*/
    }
}