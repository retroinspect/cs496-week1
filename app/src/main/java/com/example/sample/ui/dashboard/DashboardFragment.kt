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
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DashboardFragment : Fragment() {

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

        dashboardViewModel.getAllImages().observe(viewLifecycleOwner,
            {
                it?.let {
                    adapter.data = it
                    adapter.setItemClickListener(object : ImageAdapter.ItemClickListener {
                        val itemClickIntent = Intent(context, ClickImageActivity::class.java)

                        override fun onClick(view: View, position: Int) {
                            itemClickIntent.putExtra("image_uri", it[position].uri.toString())
                            itemClickIntent.putExtra("image_title", it[position].title)
                            Log.i("Log test", "before startActivity")
                            startActivity(itemClickIntent)
                            Log.i("Log test", "finish activity")
                            refreshFragment()
                        }
                    })
                }
            })

        images.adapter = adapter
        images.setHasFixedSize(true)
        images.setLayoutManager(layoutManager)

        return root
    }

    fun refreshFragment() {
        var ft: FragmentTransaction? = getFragmentManager()?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
    }
}