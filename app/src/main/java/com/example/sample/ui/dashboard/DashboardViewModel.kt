package com.example.sample.ui.dashboard

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private var allImages : MutableLiveData<ArrayList<ImageModel>> = MutableLiveData()
    private var images = ArrayList<ImageModel>()

    fun getAllImages() : MutableLiveData<ArrayList<ImageModel>> {
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
                images.add(imageModel)
            }
            cursor.close()
        }
        allImages.setValue(images)
        return allImages
    }
}