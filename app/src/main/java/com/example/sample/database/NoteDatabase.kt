package com.example.sample.database

import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import timber.log.Timber
import java.util.*

open class Note : RealmObject() {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var title: String = ""
    var isTodo = false
    var createdAt: Date = Date()

    var todos: RealmList<Todo> = RealmList()
    var desc: String = ""
}

open class Todo : RealmObject() {
    @PrimaryKey
    var todoId: String = UUID.randomUUID().toString()
    var text: String = ""
    var isCompleted: Boolean = false
    var createdAt: Date = Date()

// inverse relation
//    @LinkingObjects("note")
//    val note: RealmResults<Note>? = null
}


class NoteRealmManager(val realm: Realm) {
    /// insert an empty note
    fun insert(isTodo: Boolean, idForDebug: String? = null) {
        realm.beginTransaction()
        val primaryKey = UUID.randomUUID().toString()
        val note = realm.createObject<Note>(primaryKey)
        note.isTodo = isTodo
        realm.commitTransaction()
    }

    fun get(id: String): Note? {
        return realm.where<Note>().equalTo("id", id).findFirst()
    }

    fun getFocusedNote(): Note? {
        return realm.where<Note>().findFirst()
    }

    fun getAllNotes(): List<Note> {
        return realm.where<Note>().findAll()
    }

    fun update(oldNote: Note, newNote: Note) {
        realm.beginTransaction()
        oldNote.title = newNote.title

        realm.commitTransaction()
    }

    fun delete(note: Note) {
        realm.beginTransaction()
        note.deleteFromRealm()
        realm.commitTransaction()
    }

}

class TodoRealmManager(val realm: Realm, private val noteId: String) {

    val curNote: Note? = realm.where<Note>().equalTo("id", noteId).findFirst()

    fun getPrimaryKey(): String = UUID.randomUUID().toString()

    /// clear todo list
    fun clear() {
        realm.beginTransaction()
        curNote?.todos?.clear()
        val data = realm.createObject<Todo>(getPrimaryKey())
        curNote?.todos?.add(data)
        realm.commitTransaction()
    }

    /// insert an empty todo
    fun insert(): Todo? {
        if (curNote == null) {
            Timber.i("Invalid noteId")
            return null
        }

        Timber.i("add a todo")
        realm.beginTransaction()
        val data = realm.createObject<Todo>(getPrimaryKey())
        curNote.todos.add(data)
        realm.commitTransaction()
        return data
    }

    fun get(todoId: String): Todo? {
        if (curNote != null) {
            return curNote.todos.find { it.todoId == todoId }
        }
        return null
    }

    fun getAllTodos(): RealmList<Todo>? {
        if (curNote != null) {
            return curNote.todos
        }

        return null
    }

    fun getFocusedTodo(createdAt: Date = Date(Long.MIN_VALUE)): Todo? {
        realm.beginTransaction()
        var focusedTodo = curNote?.todos?.find { it.createdAt > createdAt }
        if (focusedTodo == null) focusedTodo = insert()
        realm.commitTransaction()
        return focusedTodo
    }

    fun update(todoId: String, text: String) {
        realm.beginTransaction()
        val selectedTodo = curNote?.todos?.find { it.todoId == todoId }
        if (selectedTodo != null) {
            selectedTodo.text = text
        }
        realm.commitTransaction()
    }

    fun updateTitle(input: String) {
        realm.beginTransaction()
        if (curNote != null) {
            curNote.title = input
        }
        realm.commitTransaction()
        Timber.i("should change title to $input")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun delete(todoId: String) {
        realm.beginTransaction()
        curNote?.todos?.removeIf { it.todoId == todoId }
        if (curNote?.todos?.isEmpty() == true) {
            val data = realm.createObject<Todo>(getPrimaryKey())
            curNote?.todos?.add(data)
        }
        realm.commitTransaction()
    }

    fun toggleCheck(todoId: String) {
        realm.beginTransaction()
        val todoToUpdate = get(todoId)
        if (todoToUpdate != null) {
            todoToUpdate.isCompleted = !todoToUpdate.isCompleted
        }
        realm.commitTransaction()
    }
}