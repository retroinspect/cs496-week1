package com.example.sample.database

/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDatabaseDao {
    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * from todo_table WHERE todoId = :key")
    suspend fun get(key: Long): Todo?

    @Query("DELETE FROM todo_table")
    suspend fun clear()

    @Query("SELECT * FROM todo_table ORDER BY todoId DESC LIMIT 1")
    suspend fun getTodo(): Todo?

    @Query("SELECT * FROM todo_table ORDER BY todoId DESC")
    fun getAlltodos(): LiveData<List<Todo>>
}

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract val TodoDatabaseDao: TodoDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null
        fun getInstance(context: Context): TodoDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            TodoDatabase::class.java,
                            "todo_database").fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}

