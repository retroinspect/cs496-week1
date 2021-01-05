package com.example.sample.ui.notes

import android.os.Bundle
import android.provider.MediaStore.Video
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sample.R
import com.example.sample.database.MemoRealmManager
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import com.example.sample.database.TodoRealmManager
import com.example.sample.databinding.FragmentNotesBinding
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmResults
import java.util.*


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    val realm: Realm = Realm.getDefaultInstance()
    val noteManager = NoteRealmManager(realm)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val todoListId = noteManager.insert(true)
        val memoId = noteManager.insert(false)

        val todoManager = TodoRealmManager(realm, todoListId)
        val memoManager = MemoRealmManager(realm, memoId)

        todoManager.insert("안녕", true)
        todoManager.insert("코딩해야지")
        todoManager.insert("오늘 잘 수 있을까")
        memoManager.update(title = "메모의 제목이다", desc = "메모의 상세 설명이다")


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val model: RealmResults<Note> = noteManager.getAllNotes()
        val adapter = NoteAdapter(context, NoteActions(), model)

        binding.noteList.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        binding.noteList.layoutManager = layoutManager
        binding.createMemoButton.setOnClickListener {
            noteManager.insert(false)
        }
        binding.createTodoListButton.setOnClickListener {
            noteManager.insert(true)
        }
        binding.clearNoteButton.setOnClickListener {
            noteManager.clear()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

class NoteActions() {

}
