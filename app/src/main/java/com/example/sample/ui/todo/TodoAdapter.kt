package com.example.sample.ui.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
            binding.clickListener = todoActions.onClickDelete
            binding.checkboxTodo.isChecked = item.isCompleted
            binding.checkboxTodo.setOnClickListener {
                todoActions.toggleCheckTodo.clickListener(
                    item.todoId
                )
            }
//            binding.textTodo.setOnKeyListener(
//                makePressOnEnterListener(item.todoId, binding.textTodo.text.toString())
//            )
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
