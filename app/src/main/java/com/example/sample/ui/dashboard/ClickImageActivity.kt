package com.example.sample.ui.dashboard

import android.content.ContentProviderOperation
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.sample.R

class ClickImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.click_image)
        val intent : Intent = getIntent()
        lateinit var newTitle: String;

        var imageOne = findViewById(R.id.image_item_one) as SubsamplingScaleImageView
        var editButton = findViewById(R.id.image_title_edit_button) as Button
        var shareButton = findViewById(R.id.image_share_button) as Button

        val imageUriString = intent.getStringExtra("image_uri")
        val imageTitle : String? = intent.getStringExtra("image_title")
        val imageUri : Uri? = imageUriString?.toUri()
        if (imageUri != null) {
            imageOne.setImage(ImageSource.uri(imageUri))
        }

        editButton.setOnClickListener {
            setContentView(R.layout.image_title_edit)
            var editTitle = findViewById(R.id.edit_image_title) as EditText
            var editSaveButton = findViewById(R.id.edit_image_save_button) as Button
            editTitle.setText(imageTitle)

            editSaveButton.setOnClickListener {
                var edited : String = editTitle.text.toString()
                intent.putExtra("edited_text", edited)
                newTitle = edited

                var ops : ArrayList<ContentProviderOperation> = ArrayList<ContentProviderOperation>()
                if (imageUri != null) {
                    var op : ContentProviderOperation.Builder = ContentProviderOperation.newUpdate(imageUri)
                        .withValue(MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.CONTENT_TYPE)
                        .withValue(MediaStore.Images.Media.TITLE, edited)
                    ops.add(op.build())
                }
                this.getContentResolver().applyBatch(MediaStore.AUTHORITY, ops)
                Toast.makeText(this, "수정되었습니다", Toast.LENGTH_LONG).show()
                Log.i("ClickActivity","edit finish")
                finish()
            }
        }
        /*
        shareButton.setOnClickListener {
        }*/
    }
}