package com.example.sample.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R


class PhoneAdapter : RecyclerView.Adapter<PhoneAdapter.PhoneHolder>() {
    class PhoneHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phoneName: TextView = itemView.findViewById(R.id.phone_name)
        val phoneNumber: TextView = itemView.findViewById(R.id.phone_number)

        fun bind(item: PhoneModel) {
            phoneName.text = item.name
            phoneNumber.text = item.phone
            Log.i("PhoneAdapter","bind")
        }

        companion object {
            fun from(parent: ViewGroup): PhoneHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_phone, parent, false)
                Log.i("PhoneAdapter", "from")
                return PhoneHolder(view)
            }
        }
    }

    var data = ArrayList<PhoneModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.i("PhoneAdapter", "set")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneHolder {
        Log.i("PhoneAdapter","onCreateViewHolder")
        return PhoneHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PhoneHolder, position: Int) {
        Log.i("PhoneAdapter","onBindViewHolder")
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        Log.i("PhoneAdapter","getItemCount")
        return data.size
    }
}