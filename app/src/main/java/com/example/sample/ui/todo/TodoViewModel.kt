package com.example.sample.ui.todo

import android.app.Application
import androidx.lifecycle.*
import com.example.sample.database.Todo
import com.example.sample.database.TodoDatabaseDao
import kotlinx.coroutines.launch
import timber.log.Timber

class TodoViewModel(
    val database: TodoDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var focusedTodo = MutableLiveData<Todo>()
    val todos = database.getAlltodos()

    init {
        initializeFocusedTodo()
    }

    fun onSubmit() {
    }

    private fun initializeFocusedTodo() {
        viewModelScope.launch {
            focusedTodo.value = getFocusedTodoFromDatabase()
        }
    }

    private suspend fun getFocusedTodoFromDatabase(): Todo? {
        return database.getTodo()
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

    fun insert(input: String) {
        val newTodo = Todo(text = input)
        viewModelScope.launch {
            insert(newTodo)
            focusedTodo.value = getFocusedTodoFromDatabase()
        }
    }


    private suspend fun update(todo: Todo) {
        database.update(todo)
    }

    fun onClickDelete(id: Long) {
        viewModelScope.launch {
            val todoToDelete = database.get(id)
            Timber.i("$todoToDelete")
            if (todoToDelete != null) {
                database.delete(todoToDelete)
            }
        }
    }

}