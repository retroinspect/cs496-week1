package com.example.sample.ui.todo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
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
//    val noteId : String = noteManager.insert(false)

    val noteId = "50b02432-89fd-4ea9-a90c-c98fb7f18120"
    val dataSource = TodoRealmManager(realm, noteId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        Timber.i("note test " + "$noteId")

        val application = requireNotNull(this.activity).application

        val viewModelFactory = TodoViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(TodoViewModel::class.java)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_todos, container, false)
        binding.todoViewModel = viewModel
        binding.lifecycleOwner = this

        val adapter =
            dataSource.getAllTodos()?.let {
                TodoAdapter(
                    TodoActions(viewModel, context),
                    it
                )
            }

        if (adapter == null)
            Timber.i("Invalid note id")

        binding.todoList.adapter = adapter

        binding.titleTodo.setOnClickListener {
            binding.editTitleTodo.visibility = VISIBLE
            binding.titleTodo.visibility = GONE
        }

        //임시 세부화면 버튼
        binding.subWindow.setOnClickListener {
            val note = noteManager.get(id) //id : setItemListener에서 가져와야 함!
            if (note?.isTodo == true) {
                //TodoActivity
                val oneTodoIntent = Intent(context, ClickTodoActivity::class.java)
                oneTodoIntent.putExtra("before_edit_title", note.title)
                oneTodoIntent.putExtra("before_edit_contexts", note.todos) //to-do type 전달 가능하도록 수정해야 함
                startActivityForResult(oneTodoIntent, 10001)
            }
            else if (note?.isTodo == false) {
                //MemoActivity
                val oneMemoIntent = Intent(context, ClickMemoActivity::class.java)
                oneMemoIntent.putExtra("before_edit_title", note.title)
                oneMemoIntent.putExtra("before_edit_contexts", note.memo?.desc)
                startActivityForResult(oneMemoIntent, 10002)
            }
        }

        val title = dataSource.getTitle()
        if (title != null && title.isNotEmpty()) {
            binding.titleTodo.text = title
            binding.editTitleTodo.setText(title)
        }
        else {
            binding.titleTodo.text = ""
            binding.editTitleTodo.visibility = VISIBLE
            binding.titleTodo.visibility = GONE
        }
        binding.editTitleTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
            val input = binding.editTitleTodo.text.toString()
            if (Util.isEnterPressedDown(keyCode, event)) {
                viewModel.updateTitle(input)
                binding.titleTodo.text = input
                binding.editTitleTodo.clearFocus()
                binding.editTitleTodo.visibility = GONE
                binding.titleTodo.visibility = VISIBLE
                Util.hideKeyboard(context, binding.root)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }


        val layoutManager = LinearLayoutManager(context)
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

    val hasFocus = { todo: Todo -> viewModel.hasFocus(todo) }
    val getFocus = { todo: Todo -> viewModel.getFocus(todo) }
    val setFocus = { createdAt: Date ->
        viewModel.setFocus(createdAt)
    }

//    val updateTitle = { input: String -> viewModel.updateTitle(input) }

}
