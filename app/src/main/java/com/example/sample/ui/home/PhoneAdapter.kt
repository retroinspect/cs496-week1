package com.example.sample.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R


class PhoneAdapter(val list:ArrayList<Phone>) : RecyclerView.Adapter<PhoneAdapter.PhoneHolder>() {
    class PhoneHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textPhone: TextView = itemView.findViewById(R.id.textPhone)

        fun bind(item: Phone) {
            textName.text = item.name
            textPhone.text = item.phone
            Log.i("Adapter",item.name)
            item.name?.let { Log.i("Adapter", it) }
        }

        companion object {
            fun from(parent: ViewGroup): PhoneHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_phone, parent, false)
                return PhoneHolder(view)
            }
        }
    }

    var data = ArrayList<Phone>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneHolder {
        return PhoneHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PhoneHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}


/*
@SuppressLint("MissingPermission")
class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mPhone:Phone? = null

init {
}

    fun setPhone(phone:Phone) {
        this.mPhone = phone
        itemView.textName.text = phone.name
        itemView.textPhone.text = phone.phone
    }
}*/