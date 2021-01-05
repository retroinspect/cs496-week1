package com.example.sample.ui.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.R
import com.example.sample.database.TodoRealmManager
import com.example.sample.ui.todo.TodoFragment
import io.realm.Realm
import timber.log.Timber

class ClickTodoActivity : AppCompatActivity() {
    lateinit var mainIntent: Intent
    lateinit var todoManager: TodoRealmManager
    val realm = Realm.getDefaultInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainIntent = intent
        setContentView(R.layout.detail_todo_list)

        val todoListId: String? = mainIntent.getStringExtra("todo_id")
        if (todoListId == null) {
            Timber.i("TodoList id is null")
            finish()
        } else {
            val todoFragment = TodoFragment()
            supportFragmentManager.beginTransaction().replace(R.id.detail_todo_list, todoFragment)
                .commit()

            val bundle = Bundle()
            bundle.putString("noteId", todoListId)
            todoFragment.arguments = bundle
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
        val resultMessage = "[" + todoManager.getTitle() + "]\n"
        // TODO todoList 의 todos 를 string 으로 변환
        Timber.i("getMessage ${resultMessage}")
        return resultMessage
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

//class TodoActions(
//    val context: Context?,
//    private val todoManager: TodoRealmManager,
//    val viewModel: TodoViewModel
//) {
//    fun getTitle(): String? = todoManager.getTitle()
//    var focusedTodo: Todo? = null
//    var focusedView: View? = null
//
//    fun getAllTodos(): RealmList<Todo> {
//        return todoManager.getAllTodos()
//    }
//
//    fun updateTitle(input: String) = todoManager.updateTitle(input)
//
//    @RequiresApi(Build.VERSION_CODES.N)
//    fun deleteTodo(todo: Todo) = todoManager.delete(todo.todoId)
//
//    fun setFocusToNextTodo(todo: Todo) {
//        viewModel.setFocusToNextTodo(todo.createdAt)
//    }
//
//    fun hasFocus(todo: Todo): Boolean {
//        return viewModel.hasFocus(todo)
//    }
//
//    fun setFocusToCurrentTodo(todo: Todo, view: View? = null) {
//        viewModel.setFocusToCurrentTodo(todo)
//    }
//
//    fun clearAll() {
//        todoManager.clear()
//        viewModel.clearFocus()
//    }
//
//    fun insert() {
//        todoManager.insert()
//    }
//
//    fun update(id: String, input: String) {
//        todoManager.update(id, input)
//    }
//
//    fun toggleCheckTodo(todo: Todo) {
//        todoManager.toggleCheck(todo.todoId)
//    }
//
//    fun hideKeyboard(view: View) {
//        Util.hideKeyboard(context, view)
//    }
//
//    fun showKeyboard(view: View) {
//        Util.showKeyboard(context, view)
//    }
//}