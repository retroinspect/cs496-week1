package com.example.sample.ui.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

class ClickTodoActivity : AppCompatActivity() {
    lateinit var mainIntent: Intent
    lateinit var todoManager: TodoRealmManager
    val realm = Realm.getDefaultInstance()
    lateinit var todoActions: TodoActions

    lateinit var root: ViewGroup
    lateinit var clearTodoAll: ImageButton
    lateinit var todoList: RecyclerView
    lateinit var titleTodo: TextView
    lateinit var editTitleTodo: EditText
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainIntent = intent
        setContentView(R.layout.detail_todo_list)
        clearTodoAll = findViewById(R.id.clear_todo_all)
        todoList = findViewById(R.id.todo_list)
        titleTodo = findViewById(R.id.title_todo)
        editTitleTodo = findViewById(R.id.edit_title_todo)

        val todoId: String? = mainIntent.getStringExtra("todo_id")
        if (todoId != null) {
            todoManager = TodoRealmManager(realm, todoId)
//            setView()
            val viewModelFactory = TodoViewModelFactory(todoManager, application)
            viewModel = ViewModelProvider(
                this, viewModelFactory
            ).get(TodoViewModel::class.java)
            todoActions = TodoActions(baseContext, todoManager, viewModel)

            clearTodoAll.setOnClickListener {
                todoActions.clearAll()
                Timber.i("Clear button clicked")
            }

            val todos = todoActions.getAllTodos()

            if (todos.isEmpty())
                throw Exception("Todos cannot empty")

            val adapter = TodoAdapter(todoActions, todos)
            todoList.adapter = adapter
            val layoutManager = LinearLayoutManager(baseContext)
            todoList.layoutManager = layoutManager

            titleTodo.setOnClickListener {
                Timber.i("titleTodo click listener")
                titleTodo.visibility = GONE
                editTitleTodo.visibility = VISIBLE
                editTitleTodo.requestFocus()
            }

            editTitleTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
                val input = editTitleTodo.text.toString()
                Timber.i("editTitleTodo text: $input")
                if (Util.isEnterPressedDown(keyCode, event)) {
                    Timber.i("editTodo Enter")
                    editTitleTodo.visibility = GONE
                    titleTodo.visibility = VISIBLE
                    todoActions.updateTitle(input)
                    titleTodo.text = input
                    editTitleTodo.clearFocus()
                    Util.hideKeyboard(baseContext, editTitleTodo as View)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            val title = todoActions.getTitle()
            if (title != null && title.isNotEmpty()) {
                Timber.i("non-empty title: $title)")
                titleTodo.visibility = VISIBLE
                editTitleTodo.setText(title)
            } else {
                Timber.i("empty title")
                titleTodo.visibility = GONE
                editTitleTodo.requestFocus()
            }

        } else {
            Timber.i("Todo id is null")
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notes_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selected = item.itemId
        if (selected == R.id.share_kakaotalk) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                val message = getMessage()
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, message)
                intent.setPackage("com.kakao.talk")
                this.startActivity(intent)
                return true
            } catch (e: Exception) {
                // kakaotalk 미설치 에러
                val kakaoAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                kakaoAlertBuilder.setTitle("공유할 수 없음")
                kakaoAlertBuilder.setMessage("이 디바이스에 Kakao Talk 이 설치되어있지 않습니다.\n설치하시겠습니까?")
                kakaoAlertBuilder.setPositiveButton(
                    "예"
                ) { _, _ ->
                    val installIntent = Intent(Intent.ACTION_VIEW)
                    installIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    installIntent.data = Uri.parse("market://details?id=com.kakao.talk")
                    startActivity(installIntent)
                }
                kakaoAlertBuilder.setNegativeButton(
                    "아니오"
                ) { _, _ ->
                    Toast.makeText(applicationContext, "Pressed Cancel", Toast.LENGTH_SHORT)
                        .show()
                }
                kakaoAlertBuilder.create().show()
                return true
            }
        }
        if (selected == R.id.share_sms) {
            val intent = Intent(Intent.ACTION_VIEW)
            val message = getMessage()
            intent.type = "vnd.android-dir/mms-sms"
            intent.putExtra("sms_body", message)
            this.startActivity(intent)
            return true
        }
        Timber.i("ClickNoteActivity not selected")
        return false
    }

    private fun getMessage(): String {
        val resultMessage = "[" + titleTodo.text + "]\n"
        // TODO todoList 의 todos 를 string 으로 변환
        Timber.i("getMessage ${resultMessage}")
        return resultMessage
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

class TodoActions(
    val context: Context?,
    private val todoManager: TodoRealmManager,
    val viewModel: TodoViewModel
) {
    fun getTitle(): String? = todoManager.getTitle()
    var focusedTodo: Todo? = null
    var focusedView: View? = null

    fun getAllTodos(): RealmList<Todo> {
        return todoManager.getAllTodos()
    }

    fun updateTitle(input: String) = todoManager.updateTitle(input)

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteTodo(todo: Todo) = todoManager.delete(todo.todoId)

    fun setFocusToNextTodo(todo: Todo) {
        viewModel.setFocusToNextTodo(todo.createdAt)
    }

    fun hasFocus(todo: Todo): Boolean {
        return viewModel.hasFocus(todo)
    }

    fun setFocusToCurrentTodo(todo: Todo, view: View? = null) {
        viewModel.setFocusToCurrentTodo(todo)
    }

    fun clearAll() {
        todoManager.clear()
        viewModel.clearFocus()
    }

    fun insert() {
        todoManager.insert()
    }

    fun update(id: String, input: String) {
        todoManager.update(id, input)
    }

    fun toggleCheckTodo(todo: Todo) {
        todoManager.toggleCheck(todo.todoId)
    }

    fun hideKeyboard(view: View) {
        Util.hideKeyboard(context, view)
    }

    fun showKeyboard(view: View) {
        Util.showKeyboard(context, view)
    }
}