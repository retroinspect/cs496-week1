package com.example.sample.ui.todo

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.databinding.ListItemTodoBinding
import timber.log.Timber

class TodoAdapter(
    val todoActions: TodoActions
) : ListAdapter<Todo, TodoAdapter.ViewHolder>(TodoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTodoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Timber.i("ViewHolder of ${item.todoId} created")
        holder.bind(item, todoActions)
    }

    class ViewHolder(val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: Todo,
            todoActions: TodoActions
        ) {
            binding.todo = item
            binding.textTodo.text = item.text
            binding.editTextTodo.setText(item.text)

            binding.textTodo.setOnClickListener {
                binding.editTextTodo.visibility = VISIBLE
            }

            binding.editTextTodo.setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
                val input = binding.editTextTodo.text.toString()
                if (Util.isEnterPressedDown(keyCode, event)) {
                    todoActions.updateTodo(item.todoId, input)
                    todoActions.insertTodo()
                    todoActions.setFocus()
                    binding.textTodo.text = input
                    binding.editTextTodo.clearFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            if (todoActions.getFocus(item)) {
                binding.editTextTodo.requestFocus()
            }

            binding.editTextTodo.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    Timber.i("${item.todoId} has focus")
                    binding.deleteButton.visibility = VISIBLE
                }
                else {
                    binding.deleteButton.visibility = INVISIBLE
                }
            }

            binding.clickListener = todoActions.onClickDelete
            binding.checkboxTodo.isChecked = item.isCompleted
            binding.checkboxTodo.setOnClickListener {
                todoActions.toggleCheckTodo.onClick(
                    item
                )
            }

            binding.executePendingBindings()
        }
    }
}

class TodoDiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.todoId == newItem.todoId
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }

}

class TodoListener(val clickListener: (todoId: Long) -> Unit) {
    fun onClick(todo: Todo) = clickListener(todo.todoId)
}
