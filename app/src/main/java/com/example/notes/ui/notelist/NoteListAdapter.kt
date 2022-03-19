package com.example.notes.ui.notelist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.ui.NoteActivity
import com.example.notes.R
import com.example.notes.database.DbManager
import com.example.notes.model.Note
import com.example.notes.utilits.IntentConstants

class NoteListAdapter(list : ArrayList<Note>, context : Context) : RecyclerView.Adapter<NoteListAdapter.Holder>() {

    private var listArray = list
    private var contextC = context


    class Holder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.titleTv)
        private val desc: TextView = itemView.findViewById(R.id.decTv)
        private val time: TextView = itemView.findViewById(R.id.timeTV)

        private val contextY = context

        @SuppressLint("SetTextI18n")
        fun setTitle(tit : Note){

            title.text = tit.title
            desc.text = tit.dec
            time.text = tit.time


            itemView.setOnClickListener {
                val intent = Intent(contextY, NoteActivity::class.java)
                intent.putExtra(IntentConstants.TITLE_KEY, tit.title)
                intent.putExtra(IntentConstants.DES_KEY, tit.dec)
                intent.putExtra(IntentConstants.ID_KEY, tit.id)
                intent.putExtra(IntentConstants.URI_KEY, tit.uri)
                contextY.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(inflater.inflate(R.layout.item_rc, parent, false), contextC)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setTitle(listArray[position])
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    fun updateAdapter(list : ArrayList<Note>) {
        listArray.clear()
        listArray.addAll(list)
        listArray.reverse()
        notifyDataSetChanged()
    }

    fun deleteLine(position : Int, dbManager: DbManager) {
        dbManager.deleteFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(position)
    }

}