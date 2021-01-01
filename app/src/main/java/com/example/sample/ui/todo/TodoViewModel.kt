package com.example.sample.ui.todo

import android.app.Application
import androidx.lifecycle.*
import com.example.sample.database.Todo
import com.example.sample.database.TodoDatabaseDao
import kotlinx.coroutines.launch

class TodoViewModel(
    val database: TodoDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var focusedTodo = MutableLiveData<Todo>()
    
    init {
        initializeFocusedTodo()
    }

    private fun initializeFocusedTodo() {
        viewModelScope.launch {
            getFocusedTodoFromDatabase()
        }
    }

    private suspend fun getFocusedTodoFromDatabase() {
        focusedTodo.value = database.getTodo()
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
        }
        focusedTodo.value = null
    }

    private suspend fun clear() {
        database.clear()
    }

    private suspend fun insert(todo: Todo) {
        database.insert(todo)
    }

    private suspend fun update(todo: Todo) {
        database.update(todo)
    }

}