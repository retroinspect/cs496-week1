package com.example.sample.ui.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.database.Note
import com.example.sample.database.NoteRealmManager
import com.example.sample.database.Todo
import com.example.sample.database.TodoRealmManager
import io.realm.OrderedRealmCollection
import io.realm.Realm
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

            itemView.setOnLongClickListener {
                val longClickIntent = Intent(context, LongClickPopupActivity::class.java)
                longClickIntent.putExtra("note_id", item.id)
                context?.startActivity(longClickIntent)
                //deleteItem(item.id)
                return@setOnLongClickListener true
            }

            if (item.isTodo)
                setTodoPreview(itemView, item, context)
            else setMemoPreview(itemView, item)
        }

        private fun setMemoPreview(itemView: View, item: Note) {
            val title: TextView = itemView.findViewById(R.id.title_preview_note)
            val desc: TextView = itemView.findViewById(R.id.desc_preview_memo)

            title.text = item.title
            desc.text = "${item.memo?.desc}"

            if (title.text.isEmpty() && desc.text.isEmpty())
                title.text = "빈 메모"
        }

        private fun setTodoPreview(itemView: View, item: Note, context: Context?) {
            val title: TextView = itemView.findViewById(R.id.title_preview_note)
            val todoListPreview: LinearLayout = itemView.findViewById(R.id.todo_list_preview_note)

            val todos: List<Todo> = item.todos
            for (i in todos.indices) {
                val inflater = LayoutInflater.from(context)
                val todoItemPreview: LinearLayout =
                    inflater.inflate(R.layout.preview_todo_item, null) as LinearLayout

                if (todoItemPreview.parent != null) {
                    ((todoItemPreview.parent) as ViewGroup).removeView(todoItemPreview)
                }

                if (todos[i].isCompleted)
                    todoItemPreview.checkbox_todo.setImageResource(R.drawable.terms_check)
                else todoItemPreview.checkbox_todo.setImageResource(R.drawable.terms_uncheck)

                todoItemPreview.text_todo.text = todos[i].text
                todoListPreview.addView(todoItemPreview)
            }

            title.text = item.title

            if (title.text.isEmpty() && todos.isEmpty())
                title.text = "빈 투두 리스트"

        }
    }
}