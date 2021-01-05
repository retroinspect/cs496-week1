package com.example.sample.ui.notes

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.R
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import io.realm.Realm
import timber.log.Timber

class ClickMemoActivity : AppCompatActivity() {
    lateinit var mainIntent : Intent
    lateinit var titleView : EditText
    lateinit var contentsView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.click_memo)
        mainIntent = getIntent()
        titleView = findViewById(R.id.edit_memo_title) as EditText
        contentsView = findViewById(R.id.edit_memo_contents) as EditText
        val memoId : String? = mainIntent.getStringExtra("memo_id")

        val realm: Realm = Realm.getDefaultInstance()
        val noteManager = NoteRealmManager(realm)
        if (memoId != null) {
            val note : Note? = noteManager.get(memoId)
            val title = note?.title
            val contents = note?.memo?.desc
            titleView.setText(title)
            contentsView.setText(contents)
        }

        //save 되는지 확인하기
        val memoSaveButton : Button = findViewById(R.id.edit_memo_save)
        memoSaveButton.setOnClickListener {
            finish()
        }
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
        resultMessage += contentsView.text
        Timber.i("getMessage ${resultMessage}")
        return resultMessage
    }
}