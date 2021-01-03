package com.example.sample.ui.dashboard

import android.content.ContentProviderOperation
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.sample.R

class ClickImageActivity : AppCompatActivity() {
    lateinit var mainIntent : Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.click_image)

        mainIntent = getIntent()

        var imageOne = findViewById(R.id.image_item_one) as SubsamplingScaleImageView

        val imageUriString = mainIntent.getStringExtra("image_uri")
        val imageTitle : String? = mainIntent.getStringExtra("image_title")
        val imageUri : Uri? = imageUriString?.toUri()
        if (imageUri != null) {
            imageOne.setImage(ImageSource.uri(imageUri))
        }
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.image_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        val imageUriString = mainIntent.getStringExtra("image_uri")
        val imageUri : Uri? = imageUriString?.toUri()
        val imageTitle : String? = mainIntent.getStringExtra("image_title")

        val selected = item.itemId
        if (selected == R.id.navigation_image_title_edit) {
            setContentView(R.layout.image_title_edit)
            var editTitle = findViewById(R.id.edit_image_title) as EditText
            var editSaveButton = findViewById(R.id.edit_image_save_button) as Button
            editTitle.setText(imageTitle)

            editSaveButton.setOnClickListener {
                var edited : String = editTitle.text.toString()
                mainIntent.putExtra("edited_text", edited)

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
            return true
        }
        if (selected == R.id.navigation_image_share) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                intent.setPackage("com.kakao.talk")
                this.startActivity(intent)
                return true
            } catch(e : Exception) {
                //kakaotalk 미설치 에러
                var kakaoAlertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
                kakaoAlertBuilder.setTitle("공유할 수 없음")
                kakaoAlertBuilder.setMessage("이 디바이스에 Kakao Talk이 설치되어있지 않습니다.\n설치하시겠습니까?")
                kakaoAlertBuilder.setPositiveButton("예", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        installIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        installIntent.setData(Uri.parse("market://details?id=com.kakao.talk"))
                        startActivity(installIntent)
                    }
                })
                kakaoAlertBuilder.setNegativeButton("아니오", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(getApplicationContext(),"Pressed Cancle", Toast.LENGTH_SHORT).show()
                    }
                })
                kakaoAlertBuilder.create().show()
                return true
            }
        }
        return false
    }
}