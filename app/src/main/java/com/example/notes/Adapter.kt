package com.example.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.database.DbManager

class Adapter(list : ArrayList<NoteItem>, context : Context) : RecyclerView.Adapter<Adapter.Holder>() {

    private var listArray = list
    private var contextC = context


    class Holder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.titleTv)
        private val desc: TextView = itemView.findViewById(R.id.decTv)
        private val time: TextView = itemView.findViewById(R.id.timeTV)
        private val img: ImageView = itemView.findViewById(R.id.dogImage)

        private val contextY = context

        @SuppressLint("SetTextI18n")
        fun setTitle(tit : NoteItem){

            title.text = getTxtSize(tit.title)
            desc.text = getTxtSize(tit.dec)
            time.text = tit.time
            img.setImageResource(tit.dog)


            itemView.setOnClickListener {
                val intent = Intent(contextY, NoteActivity::class.java)
                intent.putExtra(IntentConstants.TITLE_KEY, tit.title)
                intent.putExtra(IntentConstants.DES_KEY, tit.dec)
                intent.putExtra(IntentConstants.ID_KEY, tit.id)
                intent.putExtra(IntentConstants.URI_KEY, tit.uri)
                contextY.startActivity(intent)

            }
        }

        private fun getTxtSize(text: String, maxSize: Int = 20): String{
            var maxLetters = maxSize
            return if(text.length<maxLetters) {
                maxLetters = text.length
                text.substring(0,maxLetters)
            } else{
                text.substring(0,maxLetters)+"..."
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

    fun updateAdapter(listItem : ArrayList<NoteItem>) {
        listArray.clear()
        listArray.addAll(listItem)
        notifyDataSetChanged()
    }

    fun deleteLine(position : Int, dbManager: DbManager) {
        dbManager.deleteFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(position)
    }
}