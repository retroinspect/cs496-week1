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

    fun setFocus() {
        viewModelScope.launch {
            focusedTodo.value = getFocusedTodoFromDatabase()
        }
    }

    fun hasFocus(todo: Todo): Boolean {
        return todo.todoId == focusedTodo.value?.todoId ?: false
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

    fun insert(input: String = "") {
        val newTodo = Todo(text = input)
        viewModelScope.launch {
            insert(newTodo)
        }
    }

    fun update(input: String?, id: Long) {
        viewModelScope.launch {
            val todoToUpdate = database.get(id)
            if (input != null) {
                todoToUpdate?.text = input
            }
            if (todoToUpdate != null) {
                database.update(todoToUpdate)
            }
        }
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

    fun toggleCheck(id: Long) {
        viewModelScope.launch {
            val todoToUpdate = database.get(id)
            if (todoToUpdate != null) {
                todoToUpdate.isCompleted = !todoToUpdate.isCompleted
                database.update(todoToUpdate)
            }
        }
    }

}