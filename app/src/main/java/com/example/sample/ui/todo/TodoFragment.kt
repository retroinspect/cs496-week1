package com.example.sample.ui.todo

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.database.TodoDatabase
import com.example.sample.databinding.FragmentTodosBinding
import timber.log.Timber
import java.util.function.ToDoubleBiFunction


class TodoFragment : Fragment() {
    private lateinit var binding: FragmentTodosBinding
    private lateinit var viewModel: TodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val dataSource = TodoDatabase.getInstance(application).TodoDatabaseDao

        val viewModelFactory = TodoViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TodoViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todos, container, false)
        binding.todoViewModel = viewModel
        binding.lifecycleOwner = this

        binding.createTodo.setOnClickListener { viewModel.insert() }

        val adapter = TodoAdapter(TodoActions(viewModel, binding, context))

        viewModel.todos.observe(viewLifecycleOwner,
            {
                it?.let {
                    adapter.submitList(it as ArrayList<Todo>)
                }
            })

        binding.todoList.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.todoList.layoutManager = layoutManager

        return binding.root
    }
}

class TodoActions(
    val viewModel: TodoViewModel,
    val binding: FragmentTodosBinding,
    val context: Context?
) {
    val onClickDelete = TodoListener { todoId ->
        viewModel.onClickDelete(todoId)
    }

    val toggleCheckTodo = TodoListener { todoId ->
        viewModel.toggleCheck(todoId)
    }

    val updateTodo = { todoId: Long, input: String -> viewModel.update(input, todoId) }
    val insertTodo = { viewModel.insert() }
    val hideKeyboard = { Util.hideKeyboard(context, binding.root) }

    val getFocus = { todo: Todo -> viewModel.hasFocus(todo) }
    val setFocus = {
        viewModel.setFocus()
    }

}
