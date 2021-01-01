package com.example.sample.ui.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.database.Todo

class TodoAdapter: RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val todoTextView: TextView = itemView.findViewById(R.id.text_todo)
        private val todoCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_todo)

        fun bind(item: Todo) {
            todoTextView.text = item.text
            todoCheckbox.isChecked = item.isCompleted
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_todo, parent, false)
                return ViewHolder(view)
            }
        }
    }

    var data = ArrayList<Todo>()
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
    }

    override fun getItemCount(): Int {
        return data.size
    }
}