package com.example.sample.ui.notes

import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
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
        return ViewHolder(binding, todoActions)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Timber.i("ViewHolder of ${item?.todoId} created")
        if (item != null) {
            holder.bind(item)
        }
    }

    class ViewHolder(private val binding: ListItemTodoBinding, private val todoActions: TodoActions) :
        RecyclerView.ViewHolder(binding.root) {
        private fun handleFocus(item: Todo) {
            Timber.i("${item.todoId} has focus")
//            todoActions.showKeyboard(binding.editTextTodo)
            binding.deleteButton.visibility = VISIBLE
            binding.editTextTodo.visibility = VISIBLE
            binding.textTodo.visibility = GONE
        }

        private fun handleClearedFocus() {
            binding.deleteButton.visibility = INVISIBLE
            binding.editTextTodo.visibility = GONE
            binding.textTodo.visibility = VISIBLE
        }

        private fun setCheckbox(item: Todo) {
            binding.checkboxTodo.isChecked = item.isCompleted
            binding.checkboxTodo.setOnClickListener {
                todoActions.toggleCheckTodo(item)
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun setDeleteButton(item: Todo) {
            binding.deleteButton.setOnClickListener {
                todoActions.deleteTodo(item)
            }
        }

        private fun initializeText(item: Todo) {
            binding.textTodo.text = item.text
            binding.editTextTodo.setText(item.text)
        }

        private fun handleSubmit(item: Todo) {
            val input = binding.editTextTodo.text.toString()
            todoActions.update(item.todoId, input)
            Timber.i("Pressed enter: $input")
            binding.textTodo.text = input
            binding.editTextTodo.clearFocus()
//            todoActions.hideKeyboard(binding.textTodoWrapper)
            todoActions.setFocusToNextTodo(item)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(
            item: Todo
        ) {
            initializeText(item)

            binding.editTextTodo.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) handleFocus(item)
                else handleClearedFocus()
            }

            binding.textTodoWrapper.setOnClickListener {
                binding.editTextTodo.showSoftInputOnFocus = true
                todoActions.setFocusToCurrentTodo(item, binding.editTextTodo)
                binding.editTextTodo.requestFocus()
//                todoActions.showKeyboard(binding.editTextTodo)
                Timber.i("should focus on ${item.todoId}")
            }

            binding.editTextTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
                if (Util.isEnterPressedDown(keyCode, event)) {
                    handleSubmit(item)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            if (todoActions.hasFocus(item)) {
                todoActions.setFocusToCurrentTodo(item, binding.editTextTodo)
                binding.editTextTodo.requestFocus()
            }

            setCheckbox(item)
            setDeleteButton(item)
            binding.executePendingBindings()
        }
    }
}
