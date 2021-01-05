package com.example.sample.ui.notes

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.sample.R
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import com.example.sample.databinding.FragmentNotesBinding
import io.realm.Realm
import io.realm.RealmResults

class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    val realm: Realm = Realm.getDefaultInstance()
    val noteManager = NoteRealmManager(realm)
    var isFabOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val model: RealmResults<Note> = noteManager.getAllNotes()
        val adapter = NoteAdapter(context, NoteActions(noteManager, context), model)

        binding.noteList.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(2, 1)
        binding.noteList.layoutManager = layoutManager

        //fab sub button 생성해야함

        binding.noteAddButton.setOnClickListener {
            toggleFab()
        }
        binding.createMemoButton.setOnClickListener {
            toggleFab()
            val newMemoId : String = noteManager.insert(false)
            val oneMemoIntent = Intent(context, ClickMemoActivity::class.java)
            oneMemoIntent.putExtra("memo_id", newMemoId)
            context?.startActivity(oneMemoIntent)
        }
        binding.createTodoListButton.setOnClickListener {
            toggleFab()
            val newTodoId : String = noteManager.insert(true)
            val oneTodoIntent = Intent(context, ClickTodoActivity::class.java)
            oneTodoIntent.putExtra("todo_id", newTodoId)
            context?.startActivity(oneTodoIntent)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    fun toggleFab() {
        if (isFabOpen) {
            val fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_close)

            binding.noteAddButton.setImageResource(R.drawable.ic_baseline_add_24)
            binding.createMemoButton.startAnimation(fab_close)
            binding.createTodoListButton.startAnimation(fab_close)
            binding.createMemoButton.setClickable(false)
            binding.createTodoListButton.setClickable(false)
            isFabOpen = false
        } else {
            val fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open)

            binding.noteAddButton.setImageResource(R.drawable.ic_baseline_close_24)
            binding.createMemoButton.startAnimation(fab_open)
            binding.createTodoListButton.startAnimation(fab_open)
            binding.createMemoButton.setClickable(true)
            binding.createTodoListButton.setClickable(true)
            isFabOpen = true
        }
    }
}

class NoteActions(val noteRealmManager: NoteRealmManager, context : Context?) {
    val sendContext : Context? = context

    fun get(id: String): Note? {
        return noteRealmManager.get(id)
    }

    fun getContext() : Context? {
        return sendContext
    }
}
