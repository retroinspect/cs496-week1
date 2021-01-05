package com.example.sample.ui.notes

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.sample.R
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import io.realm.Realm

class LongClickPopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.long_click_popup)

        val intent: Intent = getIntent()
        val id = intent.getStringExtra("note_id")

        var closeButton = findViewById(R.id.popup_close) as Button
        var deleteButton = findViewById(R.id.popup_delete) as Button

        closeButton.setOnClickListener {
            finish()
        }

        deleteButton.setOnClickListener {
            val realm = Realm.getDefaultInstance()
            val noteManager: NoteRealmManager = NoteRealmManager(realm)
            if (id != null) {
                val note : Note? = noteManager.get(id)
                if (note != null) {
                    noteManager.delete(note)
                }
            }
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false
        }
        return true
    }

    override fun onBackPressed() {
        return
    }
}