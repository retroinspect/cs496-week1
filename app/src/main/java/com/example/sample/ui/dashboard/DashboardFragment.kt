package com.example.sample.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sample.R
import jp.wasabeef.blurry.Blurry

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    lateinit var layoutManager : StaggeredGridLayoutManager
    lateinit var images: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        //actionBar


        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        images = root.findViewById(R.id.image_list)
        val titleImageView: ImageView = root.findViewById(R.id.album_title_image)
        val adapter = ImageAdapter()
        layoutManager = StaggeredGridLayoutManager(3, 1)

        val totImages = dashboardViewModel.setImages()
        if (totImages.size > 0) {
            val titleImage: Bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, totImages[0].uri)
            Blurry.with(context).radius(50).from(titleImage).into(titleImageView)
        }

        adapter.setItemClickListener(object : ImageAdapter.ItemClickListener {
            val itemClickIntent = Intent(context, ClickImageActivity::class.java)

            override fun onClick(view: View, position: Int) {
                itemClickIntent.putExtra("image_uri", adapter.data[position].uri.toString())
                itemClickIntent.putExtra("image_title", adapter.data[position].title)
                startActivityForResult(itemClickIntent, 10001)
            }
        })

        dashboardViewModel.allImages.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                adapter.data = it
            }
        }
        images.adapter = adapter
        images.setHasFixedSize(true)
        images.layoutManager = layoutManager
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            val adapter = ImageAdapter()
            adapter.data = dashboardViewModel.getAllImages()
            adapter.setItemClickListener(object : ImageAdapter.ItemClickListener {
                val itemClickIntent = Intent(context, ClickImageActivity::class.java)

                override fun onClick(view: View, position: Int) {
                    itemClickIntent.putExtra("image_uri", adapter.data[position].uri.toString())
                    itemClickIntent.putExtra("image_title", adapter.data[position].title)
                    startActivityForResult(itemClickIntent, 10001)
                }
            })
            images.adapter = adapter
            images.setHasFixedSize(true)
            images.layoutManager = layoutManager
        }
    }
}