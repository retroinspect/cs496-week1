package com.example.sample.ui.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import com.example.sample.databinding.DetailTodoListBinding
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

class ClickTodoActivity : AppCompatActivity() {
    lateinit var mainIntent: Intent
    lateinit var titleView: TextView
    lateinit var contentsView: RecyclerView
    lateinit var todoManager: TodoRealmManager
    val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_todo_list)
        mainIntent = intent

        titleView = findViewById(R.id.title_todo)
        contentsView = findViewById(R.id.todo_list)

        val todoId: String? = mainIntent.getStringExtra("todo_id")
        if (todoId != null) {
            todoManager = TodoRealmManager(realm, todoId)
            setView()
        } else finish()
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
                kakaoAlertBuilder.setMessage("이 디바이스에 Kakao Talk이 설치되어있지 않습니다.\n설치하시겠습니까?")
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
        val resultMessage = "[" + titleView.text + "]\n"
        // TODO contentsView 의 todos 를 string 으로 변환
        Timber.i("getMessage ${resultMessage}")
        return resultMessage
    }

    // TODO refactor each view setup (mount listeners, set adapter to recyclerView...)
    private fun setClearButton(binding: DetailTodoListBinding) {
    }

    private fun setView() {
        val todoActions = TodoActions(baseContext, todoManager)
        val binding: DetailTodoListBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.detail_todo_list, container, false)

        binding.lifecycleOwner = this

        val todos = todoActions.getAllTodos()

        if (todos == null || todos.isEmpty())
            throw Exception("Todos cannot empty")

        val adapter = TodoAdapter(todoActions, todos)
        binding.todoList.adapter = adapter

        // visibility
        binding.titleTodo.visibility = GONE
        binding.editTitleTodo.visibility = VISIBLE

        val title = todoActions.getTitle()
        if (title != null && title.isNotEmpty()) {
            Timber.i("default setting (!null)")
            binding.titleTodo.visibility = GONE
            binding.editTitleTodo.setText(title)
            binding.editTitleTodo.requestFocus()
        } else {
            Timber.i("defalut setting (null)")
            binding.titleTodo.visibility = GONE
            binding.editTitleTodo.requestFocus()
        }

        binding.titleTodo.setOnClickListener {
            Timber.i("titleTodo click listener")
            binding.titleTodo.visibility = GONE
            binding.editTitleTodo.requestFocus()
        }

        binding.editTitleTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
            val input = binding.editTitleTodo.text.toString()
            Timber.i("editTodo click listener")
            if (Util.isEnterPressedDown(keyCode, event)) {
                Timber.i("editTodo Enter")
                binding.editTitleTodo.visibility = GONE
                binding.titleTodo.visibility = VISIBLE
                todoActions.updateTitle(input)
                binding.titleTodo.text = input
                binding.editTitleTodo.clearFocus()
                Util.hideKeyboard(baseContext, binding.root)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        val layoutManager = LinearLayoutManager(baseContext)
        binding.todoList.layoutManager = layoutManager

        binding.clearTodoAll.setOnClickListener {
            todoActions.clearAll()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

class TodoActions(
    val context: Context?,
    private val todoManager: TodoRealmManager
) {
    fun getTitle(): String? = todoManager.getTitle()
    var focusedTodo: Todo? = null

    fun getAllTodos(): RealmList<Todo>? {
        return todoManager.getAllTodos()
    }

    fun updateTitle(input: String) = todoManager.updateTitle(input)

    @RequiresApi(Build.VERSION_CODES.N)
    fun deleteTodo(todo: Todo) = todoManager.delete(todo.todoId)

    fun setFocus(createdAt: Date) {
        focusedTodo = todoManager.getFocusedTodo(createdAt)
    }

    fun hasFocus(todo: Todo): Boolean {
        return (todo.todoId == focusedTodo?.todoId)
    }

    fun getFocus(todo: Todo) {
        focusedTodo = todo
    }

    fun clearAll() {
        todoManager.clear()
        focusedTodo = null
    }

    fun insert() {
        todoManager.insert()
    }

    fun update(input: String?, id: String) {
        if (input != null) {
            todoManager.update(id, input)
        }
    }

    fun toggleCheckTodo(todo: Todo) {
        todoManager.toggleCheck(todo.todoId)
    }
}