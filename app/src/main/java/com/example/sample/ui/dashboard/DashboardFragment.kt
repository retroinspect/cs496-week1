package com.example.sample.ui.dashboard

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import java.util.*
import kotlin.collections.ArrayList

class DashboardFragment : Fragment() {
    var imageRecyclerView: RecyclerView? = null
    var projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE)
    val selection = null
    val selectionArgs = null
    val sortOrder = null
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)

        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val images: RecyclerView = root.findViewById(R.id.image_list)
        val adapter = ImageAdapter()
        adapter.data = getAllImages()
        images.adapter = adapter
        images.layoutManager = LinearLayoutManager(context)

        return root
    }

    private fun getAllImages(): ArrayList<ImageModel> {
        val imageList: ArrayList<ImageModel> = ArrayList<ImageModel>()
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursorOrNull = context?.contentResolver?.query(imageUri, projection, selection, selectionArgs, sortOrder)
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
                imageList.add(imageModel)
            }
            cursor.close()
        }

        return imageList
    }
}