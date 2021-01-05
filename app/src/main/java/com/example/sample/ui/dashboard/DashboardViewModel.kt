package com.example.sample.ui.dashboard

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.collections.ArrayList

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    var allImages : MutableLiveData<ArrayList<ImageModel>> = MutableLiveData()

    fun getAllImages() : ArrayList<ImageModel> {
        var images : ArrayList<ImageModel> = ArrayList<ImageModel>()
        var projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.TITLE
        )
        val selection = null
        val selectionArgs = null
        val sortOrder = null

        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursorOrNull = context.contentResolver?.query(
            imageUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        if (cursorOrNull != null) {
            val cursor = cursorOrNull
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val imageModel = ImageModel(contentUri, title)
                Log.i("refresh", title)
                images.add(imageModel)
            }
            cursor.close()
        }
        return images
    }

    fun setImages() : ArrayList<ImageModel> {
        val allImage : ArrayList<ImageModel> = getAllImages()
        allImages.value = allImage
        return allImage
    }
}