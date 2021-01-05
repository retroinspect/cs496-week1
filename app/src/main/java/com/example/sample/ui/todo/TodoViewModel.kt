package com.example.sample.ui.todo

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
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

    fun setFocus(createdAt: Date) {
        focusedTodo.value = database.getFocusedTodo(createdAt)
    }

    fun hasFocus(todo: Todo): Boolean {
        return todo.todoId == focusedTodo.value?.todoId ?: false
    }

    fun getFocus(todo: Todo) {
        focusedTodo.value = todo
    }

    fun onClear() {
        database.clear()
        focusedTodo.value = null
    }

}

class TodoViewModelFactory(
    private val todoRealmManager: TodoRealmManager,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(todoRealmManager, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

