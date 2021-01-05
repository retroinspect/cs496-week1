package com.example.sample.ui.notes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.database.Note
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter


class NoteAdapter(
    val noteActions: NoteActions,
    realmResult: OrderedRealmCollection<Note>,
) : RealmRecyclerViewAdapter<Note, NoteAdapter.ViewHolder>(realmResult, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.preview_memo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, noteActions)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Note,
            noteActions: NoteActions
        ) {
            itemView.setOnClickListener {
                val note = noteActions.get(item.id)
                val context = noteActions.getContext()
                if (note?.isTodo == true) {
                    //TodoActivity
                    val oneTodoIntent = Intent(context, ClickTodoActivity::class.java)
                    oneTodoIntent.putExtra("todo_id", item.id)
                    context?.startActivity(oneTodoIntent)
                }
                else if (note?.isTodo == false) {
                    //MemoActivity
                    val oneMemoIntent = Intent(context, ClickMemoActivity::class.java)
                    oneMemoIntent.putExtra("memo_id", item.id)
                    context?.startActivity(oneMemoIntent)
                }
            }

            val title: TextView = itemView.findViewById(R.id.title_preview_memo)
            val desc: TextView = itemView.findViewById(R.id.desc_preview_memo)
            title.text = "노트"
            if (item.isTodo) desc.text = "투두투두"
            else desc.text = "메모메모"

        }
    }

}

