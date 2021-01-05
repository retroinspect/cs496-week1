package com.example.sample.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import java.util.*

class TodoViewModel(
    val database: TodoRealmManager,
    application: Application
) : AndroidViewModel(application) {

    private var focusedTodo = MutableLiveData<Todo>()

    init {
        focusedTodo.value = database.getFocusedTodo()
    }

    fun clearFocus() {
        focusedTodo.value = null
    }

    fun setFocusToNextTodo(createdAt: Date) {
        focusedTodo.value = database.getFocusedTodo(createdAt)
    }

    fun hasFocus(todo: Todo): Boolean {
        return todo.todoId == focusedTodo.value?.todoId ?: false
    }

    fun setFocusToCurrentTodo(todo: Todo) {
        focusedTodo.value = todo
    }
}

class TodoViewModelFactory(
    private val dataSource: TodoRealmManager,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

