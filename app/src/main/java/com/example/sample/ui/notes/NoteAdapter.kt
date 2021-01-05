package com.example.sample.ui.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.database.Note
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.preview_todo_item.view.*
import timber.log.Timber


class NoteAdapter(
    val context: Context?,
    val noteActions: NoteActions,
    realmResult: OrderedRealmCollection<Note>,
) : RealmRecyclerViewAdapter<Note, NoteAdapter.ViewHolder>(realmResult, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.preview_note, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, noteActions, context)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: Note,
            noteActions: NoteActions,
            context: Context?
        ) {
            itemView.setOnClickListener {
                Timber.i(item.id)
                // TODO 노트 종류에 따라 적절한 Activity로 이동하도록 하기
            }

            // TODO noteActions 에 delete 등을 추가해서 longClickListener 로 삭제/공유하도록 하기
//            itemView.setOnLongClickListener {
//                Timber.i("LongClick: ${item.id}")
//                return@setOnLongClickListener true
//            }

            if (item.isTodo)
                setTodoPreview(itemView, item, context)
            else setMemoPreview(itemView, item)
        }

        private fun setMemoPreview(itemView: View, item: Note) {
            val title: TextView = itemView.findViewById(R.id.title_preview_note)
            val desc: TextView = itemView.findViewById(R.id.desc_preview_memo)

            title.text = "메모 ${item.id} ${item.title}"
            desc.text = "${item.memo?.desc}"
        }

        private fun setTodoPreview(itemView: View, item: Note, context: Context?) {
            val title: TextView = itemView.findViewById(R.id.title_preview_note)
            val todoListPreview: LinearLayout = itemView.findViewById(R.id.todo_list_preview_note)

//            val todos: List<Todo> = item.todos
//            val dummy: List<String> = listOf("안녕", "안녕2", "안녕3")
//            for (i in dummy.indices) {
//                val inflater = LayoutInflater.from(context)
//                val todoItemPreview: LinearLayout =
//                    inflater.inflate(R.layout.preview_todo_item, todoListPreview) as LinearLayout
//                todoItemPreview.checkbox_todo.isChecked = true
//                todoItemPreview.text_todo.text = dummy[i]
//                todoItemPreview.addView(todoItemPreview)
//            }

            title.text = "투두 ${item.id}"
        }
    }
}