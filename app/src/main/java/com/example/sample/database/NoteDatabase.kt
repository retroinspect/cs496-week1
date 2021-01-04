package com.example.sample.database

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
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
    var memo: Memo? = null
    var todos: RealmList<Todo> = RealmList()
}

open class Memo : RealmObject() {
    var desc: String = ""
    var imgUri: String? = null
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
    fun insert(isTodo: Boolean): String {
        realm.beginTransaction()
        val primaryKey = UUID.randomUUID().toString()
        val note = realm.createObject<Note>(primaryKey)
        note.isTodo = isTodo
        realm.commitTransaction()
        return primaryKey
    }

    fun get(id: String): Note? {
        return realm.where<Note>().equalTo("id", id).findFirst()
    }

    fun getFocusedNote(): Note? {
        return realm.where<Note>().findFirst()
    }

    fun getAllNotes(): RealmResults<Note> {
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

    fun clear() {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }
}

class TodoRealmManager(val realm: Realm, noteId: String) {

    val curNote: Note? = realm.where<Note>().equalTo("id", noteId).findFirst()

    private fun getPrimaryKey(): String = UUID.randomUUID().toString()

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
        if (curNote == null) {
            curNoteIsNull()
            return null
        }
        realm.beginTransaction()
        var focusedTodo = curNote.todos.find { it.createdAt > createdAt }
        if (focusedTodo == null) {
            focusedTodo = realm.createObject<Todo>(getPrimaryKey())
            curNote.todos.add(focusedTodo)
        }
        realm.commitTransaction()
        return focusedTodo
    }

    fun update(todoId: String, text: String) {
        if (curNote == null) {
            curNoteIsNull()
            return
        }
        realm.beginTransaction()
        val selectedTodo = curNote.todos.find { it.todoId == todoId }
        if (selectedTodo != null) {
            selectedTodo.text = text
        }
        realm.commitTransaction()
    }

    fun getTitle(): String? {
        if (curNote == null) return null
        return curNote.title
    }

    fun updateTitle(input: String) {
        if (curNote == null) return
        realm.beginTransaction()
        curNote.title = input
        realm.commitTransaction()
        Timber.i("title changed to ${input}")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun delete(todoId: String) {
        if (curNote == null) {
            curNoteIsNull()
            return
        }
        realm.beginTransaction()
        curNote.todos.removeIf { it.todoId == todoId }
        if (curNote.todos.isEmpty()) {
            val data = realm.createObject<Todo>(getPrimaryKey())
            curNote.todos.add(data)
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

    private fun curNoteIsNull() = Timber.i("curNote is null: This should not happen")
}