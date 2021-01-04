package com.example.sample.ui.todo

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import com.example.sample.databinding.FragmentTodosBinding
import io.realm.Realm
import timber.log.Timber
import java.util.*

class TodoFragment : Fragment() {
    private lateinit var binding: FragmentTodosBinding

    private lateinit var viewModel: TodoViewModel
    val realm: Realm = Realm.getDefaultInstance()
    val noteManager = NoteRealmManager(realm)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this.activity).application
        val noteId = "default"

        Timber.i("creating a note...")
        noteManager.insert(false)
        val note = noteManager.getFocusedNote()
        if (note != null) {
            Timber.i("created a note of ${note.id}")
        } else {
            Timber.i("No note in realm")
        }

//        val root: View = inflater.inflate(R.layout.hello_world, container, false)
//        return root

        val dataSource = TodoRealmManager(realm, noteId)
        Timber.i(
            "curNote: ${
                dataSource.curNote?.id.toString()
            }"
        )

        val viewModelFactory = TodoViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TodoViewModel::class.java)
//
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todos, container, false)
        binding.todoViewModel = viewModel
        binding.lifecycleOwner = this

        binding.createTodo.setOnClickListener { viewModel.insert() }

        val adapter =
            dataSource.getAllTodos()?.let {
                TodoAdapter(TodoActions(viewModel, binding, context),
                    it
                )
            }

        if (adapter == null)
            Timber.i("Invalid note id")

        binding.todoList.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
//        layoutManager.reverseLayout = true
//        layoutManager.stackFromEnd = true
        binding.todoList.layoutManager = layoutManager

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

class TodoActions(
    val viewModel: TodoViewModel,
    val binding: FragmentTodosBinding,
    val context: Context?
) {
    @RequiresApi(Build.VERSION_CODES.N)
    val onClickDelete = TodoListener { todoId ->
        viewModel.onClickDelete(todoId)
    }

    val toggleCheckTodo = TodoListener { todoId ->
        viewModel.toggleCheck(todoId)
    }

    val updateTodo = { todoId: String, input: String -> viewModel.update(input, todoId) }
    val insertTodo = { viewModel.insert() }
    val hideKeyboard = { Util.hideKeyboard(context, binding.root) }

    val hasFocus = { todo: Todo -> viewModel.hasFocus(todo) }
    val getFocus = { todo: Todo -> viewModel.getFocus(todo) }
    val setFocus = { createdAt: Date ->
        viewModel.setFocus(createdAt)
    }

    val updateTitle = { input: String -> viewModel.updateTitle(input) }

}
