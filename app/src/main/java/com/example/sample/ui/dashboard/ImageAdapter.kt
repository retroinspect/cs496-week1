package com.example.sample.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import java.util.ArrayList

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageItem: ImageView = itemView.findViewById(R.id.image_item)
        val imageTitleView: TextView = itemView.findViewById(R.id.image_title)

        fun bind(item: ImageModel) {
            imageItem.setImageURI(item.uri)
            imageTitleView.text = item.title
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_image, parent, false)
                return ViewHolder(view)
            }
        }
    }

    var data = ArrayList<ImageModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener:ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}