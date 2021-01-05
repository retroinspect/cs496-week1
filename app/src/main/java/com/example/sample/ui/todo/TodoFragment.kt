package com.example.sample.ui.todo

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import com.example.sample.databinding.FragmentTodosBinding
import io.realm.Realm
import io.realm.RealmList
import timber.log.Timber
import java.lang.Exception
import java.util.*

class TodoFragment : Fragment() {
    private lateinit var binding: FragmentTodosBinding
    private lateinit var viewModel: TodoViewModel
    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this.activity).application
        arguments ?: Timber.i("No arguments passed!")
        val noteId = requireArguments().getString("noteId")
            ?: throw Exception("Arguments should not be null")

        Timber.i(noteId)

        val realm: Realm = Realm.getDefaultInstance()
        val todoManager = TodoRealmManager(realm, noteId)

        val viewModelFactory = TodoViewModelFactory(todoManager, application)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TodoViewModel::class.java)

        val todoActions = TodoActions(viewModel, context, todoManager)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todos, container, false)
        binding.todoViewModel = viewModel
        binding.lifecycleOwner = this

        val data = todoActions.getAllTodos()
        val adapter =
            TodoAdapter(todoActions, data)

        binding.todoList.adapter = adapter

        binding.titleTodo.setOnClickListener {
            Timber.i("titleTodo click listener")
            binding.editTitleTodo.visibility = VISIBLE
            binding.titleTodo.visibility = GONE
            binding.editTitleTodo.requestFocus()
        }

        val title = todoManager.getTitle()
        Timber.i("title: $title")
        if (title != null && title.isNotEmpty()) {
            Timber.i("non-empty title: $title)")
            binding.titleTodo.text = title
            binding.editTitleTodo.setText(title)
            binding.editTitleTodo.visibility = GONE
            binding.titleTodo.visibility = VISIBLE
        } else {
            Timber.i("empty title")
            binding.editTitleTodo.visibility = VISIBLE
            binding.titleTodo.visibility = GONE
            binding.editTitleTodo.requestFocus()
        }

        binding.editTitleTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
            val input = binding.editTitleTodo.text.toString()
            Timber.i("editTitleTodo text: $input")
            if (Util.isEnterPressedDown(keyCode, event)) {
                Timber.i("editTodo Enter")
                todoActions.updateTitle(input)
                binding.titleTodo.text = input
                binding.editTitleTodo.visibility = GONE
                binding.titleTodo.visibility = VISIBLE
                todoManager.updateTitle(input)
                binding.editTitleTodo.clearFocus()
                Util.hideKeyboard(context, binding.root)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        val layoutManager = LinearLayoutManager(context)
        binding.todoList.layoutManager = layoutManager

        binding.clearTodoAll.setOnClickListener {
            todoManager.clear()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

class TodoActions(
    private val viewModel: TodoViewModel,
    val context: Context?,
    private val todoManager: TodoRealmManager
) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteTodo(todo: Todo) = todoManager.delete(todo.todoId)

    fun toggleCheckTodo(todo: Todo) {
        todoManager.toggleCheck(todo.todoId)
    }

    fun update(id: String, input: String) {
        todoManager.update(id, input)
    }

    fun insert() {
        todoManager.insert()
    }

    val hasFocus = { todo: Todo -> viewModel.hasFocus(todo) }
    val getFocus = { todo: Todo -> viewModel.getFocus(todo) }
    val setFocus = { createdAt: Date ->
        viewModel.setFocus(createdAt)
    }

    fun updateTitle(input: String) = todoManager.updateTitle(input)

    fun getAllTodos(): RealmList<Todo> {
        return todoManager.getAllTodos()
    }

}
