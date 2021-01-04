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
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes, container, false)

        val model: RealmResults<Note> = noteManager.getAllNotes()
        val adapter = NoteAdapter(NoteActions(), model)

        binding.noteList.adapter = adapter

        val layoutManager = StaggeredGridLayoutManager(2, 1)
        binding.noteList.layoutManager = layoutManager
        binding.createNoteButton.setOnClickListener {
            noteManager.insert(false)
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
