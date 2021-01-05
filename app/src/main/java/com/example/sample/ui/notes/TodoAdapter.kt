package com.example.sample.ui.notes

import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.databinding.ListItemTodoBinding
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import timber.log.Timber


class TodoAdapter(
    private val todoActions: TodoActions,
    realmResult: OrderedRealmCollection<Todo>,
) : RealmRecyclerViewAdapter<Todo, TodoAdapter.ViewHolder>(realmResult, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemTodoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Timber.i("ViewHolder of ${item?.todoId} created")
        if (item != null) {
            holder.bind(item, todoActions)
        }
    }

    class ViewHolder(private val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(
            item: Todo,
            todoActions: TodoActions
        ) {
            binding.textTodo.text = item.text
            binding.editTextTodo.setText(item.text)

            binding.deleteButton.setOnClickListener {
                todoActions.deleteTodo(item)
            }

            binding.textTodo.setOnClickListener {
                binding.editTextTodo.visibility = VISIBLE
                todoActions.getFocus(item)
            }

            binding.editTextTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
                val input = binding.editTextTodo.text.toString()
                if (Util.isEnterPressedDown(keyCode, event)) {
                    todoActions.update(item.todoId, input)
                    todoActions.insert()
                    todoActions.setFocus(item.createdAt)
                    binding.textTodo.text = input
                    binding.editTextTodo.clearFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            if (todoActions.hasFocus(item)) {
                binding.editTextTodo.requestFocus()
            }

            binding.editTextTodo.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Timber.i("${item.todoId} has focus")
                    binding.deleteButton.visibility = VISIBLE
                } else {
                    binding.deleteButton.visibility = INVISIBLE
                }
            }

            binding.checkboxTodo.isChecked = item.isCompleted
            binding.checkboxTodo.setOnClickListener {
                todoActions.toggleCheckTodo(item)
            }

            binding.executePendingBindings()
        }
    }
}
