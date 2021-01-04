package com.example.sample.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    lateinit var images : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        images = root.findViewById(R.id.image_list)
        val adapter = ImageAdapter()
        val layoutManager = LinearLayoutManager(context)
        val allImage = dashboardViewModel.setImages()

        adapter.setItemClickListener(object : ImageAdapter.ItemClickListener {
            val itemClickIntent = Intent(context, ClickImageActivity::class.java)

            override fun onClick(view: View, position: Int) {
                itemClickIntent.putExtra("image_uri", allImage[position].uri.toString())
                itemClickIntent.putExtra("image_title", allImage[position].title)
                startActivityForResult(itemClickIntent, 10001)
            }
        })

        dashboardViewModel.allImages.observe(viewLifecycleOwner
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

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            refreshFragment()
        }
    }

    fun refreshFragment() {
        val adapter = ImageAdapter()
        adapter.data = dashboardViewModel.getAllImages()
        images.adapter = adapter
    }
}