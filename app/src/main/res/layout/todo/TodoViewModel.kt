package com.example.sample.ui.todo

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.sample.database.NoteRealmManager
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import io.realm.Realm
import java.util.*

class TodoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var focusedTodo = MutableLiveData<Todo>()
    val realm: Realm = Realm.getDefaultInstance()
    val noteManager = NoteRealmManager(realm)
    val noteId = noteManager.insert(false)
    val database = TodoRealmManager(realm, noteId)


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

    fun insert() {
        database.insert()
    }

    fun update(input: String?, id: String) {
        if (input != null) {
            database.update(id, input)
        }
    }

    fun updateTitle(input: String?) {
        if (input != null) {
            database.updateTitle(input)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun onClickDelete(todoId: String) {
        database.delete(todoId)
    }

    fun toggleCheck(todoId: String) {
        database.toggleCheck(todoId)
    }
}