package com.example.sample

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sample.database.TodoDatabase
import com.example.sample.database.TodoDatabaseDao
import com.example.sample.database.Todo
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.io.IOException


/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class TodoDatabaseTest {

    private lateinit var todoDao: TodoDatabaseDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.

        Timber.i("DB creating!")
        db = Room.inMemoryDatabaseBuilder(context, TodoDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        todoDao = db.TodoDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodo() {
        val todo = Todo()
        todoDao.insert(todo)
        val newTodo = todoDao.getTodo()
        assertEquals(newTodo?.isCompleted, false)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeleteTodo() {
        val todo = Todo()
        todoDao.insert(todo)
        val newTodo = todoDao.getTodo()
        assertEquals(newTodo?.isCompleted, false)

        if (newTodo != null) {
            todoDao.delete(newTodo)
        }
        assertEquals(todoDao.getTodo(), null)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndUpdateTodo() {
        val todo = Todo()
        todoDao.insert(todo)
        val newTodo = todoDao.getTodo()
        newTodo?.text = "hello world"
        if (newTodo != null) {
            todoDao.update(newTodo)
        }

        assertEquals(newTodo?.isCompleted, false)
        assertEquals(todoDao.getTodo()?.text, "hello world")
    }
}
