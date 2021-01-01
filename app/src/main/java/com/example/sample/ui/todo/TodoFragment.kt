package com.example.sample.ui.todo

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.database.Todo
import com.example.sample.database.TodoDatabase
import com.example.sample.databinding.FragmentTodosBinding
import timber.log.Timber

class TodoFragment : Fragment() {
    private lateinit var binding: FragmentTodosBinding
    private lateinit var viewModel: TodoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    Timber.i("Not a TextView")
                    return false
                } else if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    Timber.i("Should submit")
                    return true;
                }
                Timber.i("Not enter")
                return false
            }
        }

        binding.createTodo.setOnKeyListener(OnEnter())
        val todoList: RecyclerView = binding.todoList
        val adapter = TodoAdapter()

        var dummy = ArrayList<Todo>()
        dummy.add(Todo(text = "hello"))
        dummy.add(Todo(text = "world"))

        adapter.data = dummy
        todoList.adapter = adapter
//        TODO("Move to TodosViewModel")
        todoList.layoutManager = LinearLayoutManager(context)

        return binding.root
    }
}

