package com.example.sample.ui.todo

import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.sample.database.Todo

@BindingAdapter("todoText")
fun TextView.setTodoText(item: Todo) {
    text = item.text
}

@BindingAdapter("todoChecked")
fun CheckBox.setTodoChecked(item: Todo) {
    isChecked = item.isCompleted
}