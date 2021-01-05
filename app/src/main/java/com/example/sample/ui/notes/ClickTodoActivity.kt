package com.example.sample.ui.notes

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.Util
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import com.example.sample.databinding.FragmentTodosBinding
import com.example.sample.ui.todo.*
import com.example.sample.ui.todo.TodoActions
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.util.*

class ClickTodoActivity : AppCompatActivity() {
    lateinit var mainIntent : Intent
    lateinit var titleView : TextView
    lateinit var contentsView : RecyclerView
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_todos)
        mainIntent = getIntent()

        titleView = findViewById(R.id.title_todo) as TextView
        contentsView = findViewById(R.id.todo_list) as RecyclerView
        val todoId : String? = mainIntent.getStringExtra("todo_id")
        //recyclerView를 기존 data로 업데이트해두기
        updateRecyclerView(todoId)
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.notes_item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        val selected = item.itemId
        if (selected == R.id.share_kakaotalk) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                var message = getMessage()
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, message)
                intent.setPackage("com.kakao.talk")
                this.startActivity(intent)
                return true
            } catch(e : Exception) {
                //kakaotalk 미설치 에러
                var kakaoAlertBuilder : AlertDialog.Builder = AlertDialog.Builder(this)
                kakaoAlertBuilder.setTitle("공유할 수 없음")
                kakaoAlertBuilder.setMessage("이 디바이스에 Kakao Talk이 설치되어있지 않습니다.\n설치하시겠습니까?")
                kakaoAlertBuilder.setPositiveButton("예", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val installIntent = Intent(Intent.ACTION_VIEW)
                        installIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        installIntent.setData(Uri.parse("market://details?id=com.kakao.talk"))
                        startActivity(installIntent)
                    }
                })
                kakaoAlertBuilder.setNegativeButton("아니오", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        Toast.makeText(getApplicationContext(),"Pressed Cancel", Toast.LENGTH_SHORT).show()
                    }
                })
                kakaoAlertBuilder.create().show()
                return true
            }
        }
        if (selected == R.id.share_sms) {
            val intent = Intent(Intent.ACTION_VIEW)
            var message = getMessage()
            intent.type = "vnd.android-dir/mms-sms"
            intent.putExtra("sms_body", message)
            this.startActivity(intent)
            return true
        }
        Timber.i("ClickNoteActivity not selected")
        return false
    }

    fun getMessage() : String {
        var resultMessage = "["+titleView.text+"]\n"
        //contentsView의 todos를 string으로 변환
        Timber.i("getMessage ${resultMessage}")
        return resultMessage
    }

    fun updateRecyclerView(id : String?) {
        realm = Realm.getDefaultInstance()
        if (id != null) {
            val dataSource = TodoRealmManager(realm, id)
            val viewModelFactory = TodoViewModelFactory(dataSource, application)
            var viewModel : TodoViewModel = ViewModelProvider(
                this, viewModelFactory
            ).get(TodoViewModel::class.java)
            var binding : FragmentTodosBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_todos, container, false)
            binding.todoViewModel = viewModel
            binding.lifecycleOwner = this

            val adapter =
                dataSource.getAllTodos()?.let {
                    TodoAdapter(
                        TodoActions(viewModel, baseContext),
                        it
                    )
                }
            binding.todoList.adapter = adapter

            val title = dataSource.getTitle()
            if (title != null && title.isNotEmpty()) {
                binding.titleTodo.text = title
                binding.editTitleTodo.setText(title)
            }
            else {
                binding.titleTodo.visibility = GONE
                binding.editTitleTodo.visibility = VISIBLE
                binding.editTitleTodo.requestFocus()
            }

            binding.titleTodo.setOnClickListener {
                binding.editTitleTodo.visibility = VISIBLE
                binding.titleTodo.visibility = GONE
                binding.editTitleTodo.requestFocus()
            }

            binding.editTitleTodo.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
                val input = binding.editTitleTodo.text.toString()
                if (Util.isEnterPressedDown(keyCode, event)) {
                    viewModel.updateTitle(input)
                    binding.titleTodo.text = input
                    binding.editTitleTodo.clearFocus()
                    binding.editTitleTodo.visibility = GONE
                    binding.titleTodo.visibility = VISIBLE
                    Util.hideKeyboard(baseContext, binding.root)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            val layoutManager = LinearLayoutManager(baseContext)
            binding.todoList.layoutManager = layoutManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}