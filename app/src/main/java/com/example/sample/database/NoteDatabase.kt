package com.example.sample.database

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
    var memo: Memo? = Memo()
    var todos: RealmList<Todo> = RealmList()
}

open class Memo(desc: String = "") : RealmObject() {
    var desc: String = desc
    var imgUri: String? = null
}

open class Todo : RealmObject() {
    @PrimaryKey
    var todoId: String = UUID.randomUUID().toString()
    var text: String = ""
    var isCompleted: Boolean = false
    var createdAt: Date = Date()
}

class MemoRealmManager(val realm: Realm, val noteId: String) {
    private val curNote: Note? = realm.where<Note>().equalTo("id", noteId).findFirst()

    fun getTitle(): String {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        return curNote.title
    }

    fun getDesc(): String {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")
        val memo = curNote.memo
        return if (memo == null) {
            realm.beginTransaction()
            curNote.memo = realm.createObject()
            realm.commitTransaction()
            ""
        } else
            memo.desc
    }

    fun updateTitle(input: String) {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")
        realm.beginTransaction()
        curNote.title = input
        realm.commitTransaction()
    }

    fun updateDesc(input: String) {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        val memo = curNote.memo
        realm.beginTransaction()
        if (memo == null) {
            curNote.memo = realm.createObject()
            curNote.memo!!.desc = input
        } else
            memo.desc = input

        realm.commitTransaction()
    }
}


class NoteRealmManager(val realm: Realm) {
    /// insert an empty note
    fun insert(isTodo: Boolean): String {
        realm.beginTransaction()
        val primaryKey = getPrimaryKey()
        val note = realm.createObject<Note>(primaryKey)
        note.isTodo = isTodo
        if (isTodo) {
            val data = realm.createObject<Todo>(getPrimaryKey())
            note.todos.add(data)
            Timber.i("Add todo")
        }
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

    private fun getPrimaryKey(): String = UUID.randomUUID().toString()

}


class TodoRealmManager(val realm: Realm, val noteId: String) {

    val curNote: Note? = realm.where<Note>().equalTo("id", noteId).findFirst()

    private fun getPrimaryKey(): String = UUID.randomUUID().toString()

    /// clear todo list
    fun clear() {
        realm.beginTransaction()
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        curNote.todos.clear()
        val data = realm.createObject<Todo>(getPrimaryKey())
        curNote.todos.add(data)
        realm.commitTransaction()
    }

    /// insert an empty todo
    fun insert(input: String = "", isCompleted: Boolean = false): Todo {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        Timber.i("add a todo")
        realm.beginTransaction()
        val data = realm.createObject<Todo>(getPrimaryKey())
        data.text = input
        data.isCompleted = isCompleted
        curNote.todos.add(data)
        realm.commitTransaction()
        return data
    }

    fun get(todoId: String): Todo? {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        return curNote.todos.find { it.todoId == todoId }
    }

    fun getAllTodos(): RealmList<Todo> {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        return curNote.todos
    }

    fun getFocusedTodo(createdAt: Date = Date(Long.MIN_VALUE)): Todo {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        realm.beginTransaction()
        var focusedTodo = curNote.todos.find { it.createdAt > createdAt }
        if (focusedTodo == null) {
            focusedTodo = realm.createObject(getPrimaryKey())
            curNote.todos.add(focusedTodo)
        }
        realm.commitTransaction()
        return focusedTodo
    }

    fun update(todoId: String, text: String) {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        realm.beginTransaction()
        val selectedTodo = curNote.todos.find { it.todoId == todoId }
            ?: throw Exception("No corresponding todo of id $todoId")
        Timber.i("should update todo id: $todoId to input $text")
        selectedTodo.text = text
        realm.commitTransaction()
    }

    fun getTitle(): String? {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")
        return curNote.title
    }

    fun updateTitle(input: String) {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

        realm.beginTransaction()
        curNote.title = input
        realm.commitTransaction()
        Timber.i("title changed to ${input}")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun delete(todoId: String) {
        if (curNote == null)
            throw Exception("No corresponding note of id $noteId")

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
        val todoToUpdate = get(todoId) ?: throw Exception("No corresponding todo of id $todoId")
        todoToUpdate.isCompleted = !todoToUpdate.isCompleted
        realm.commitTransaction()
    }
}