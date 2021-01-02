package com.example.sample.ui.todo

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.database.Todo
import com.example.sample.database.TodoDatabase
import com.example.sample.databinding.FragmentTodosBinding


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

        class OnEnter : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (v !is TextView) {
                    return false
                } else if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    val input: String = binding.createTodo.text.toString()
                    viewModel.insert(input)
                    binding.createTodo.text.clear()
                    val imm =
                        context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        binding.root.windowToken, 0
                    )
                    return true
                }
                return false
            }
        }

        binding.createTodo.setOnKeyListener(OnEnter())

        val onClickDelete = TodoListener { todoId ->
            viewModel.onClickDelete(todoId)
        }

        val adapter = TodoAdapter(onClickDelete)

        viewModel.todos.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it as ArrayList<Todo>)
            }
        })

        binding.todoList.adapter = adapter
        binding.todoList.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
}

