package com.example.sample.ui.dashboard

import android.content.ContentUris
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DashboardFragment : Fragment() {
    var projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.TITLE
    )
    val selection = null
    val selectionArgs = null
    val sortOrder = null
    var refresh : Boolean = false

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val images: RecyclerView = root.findViewById(R.id.image_list)
        val adapter = ImageAdapter()
        val layoutManager = LinearLayoutManager(context)

        images.adapter = adapter
        images.setHasFixedSize(true)
        images.setLayoutManager(layoutManager)

        val allImages = getAllImages()
        adapter.data = allImages

        adapter.setItemClickListener(object : ImageAdapter.ItemClickListener {
            val itemClickIntent = Intent(context, ClickImageActivity::class.java)

            override fun onClick(view: View, position: Int) {
                itemClickIntent.putExtra("image_uri", allImages[position].uri.toString())
                itemClickIntent.putExtra("image_title", allImages[position].title)
                Log.i("Log test", "before startActivity")
                startActivity(itemClickIntent)
                Log.i("Log test", "finish activity")
                refreshFragment()
            }
        })
        return root
    }

    private fun getAllImages(): ArrayList<ImageModel> {
        var imageList: ArrayList<ImageModel> = ArrayList<ImageModel>()
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursorOrNull = context?.contentResolver?.query(
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
                imageList.add(imageModel)
            }
            cursor.close()
        }
        return imageList
    }

    fun refreshFragment() {
        var ft: FragmentTransaction? = getFragmentManager()?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
    }
}