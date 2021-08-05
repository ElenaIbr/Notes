package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.database.DbManager

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    val myDbManager = DbManager(this)
    val myAdapter = Adapter(ArrayList(), this)
    private var editText : EditText? = null
    var emptyList : TextView? = null
    var rcView : RecyclerView? = null

    val context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
        searchView()

    }

    override fun onResume() {
        super.onResume()
        editText?.text?.clear()
        editText?.clearFocus()
        myDbManager.openDb()
        fillArrayForAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun onClickAdd(view: View) {

        val intent = Intent(this, NoteActivity::class.java)
        startActivity(intent)
    }

    private fun searchView() {
        editText = findViewById(R.id.editt)
        emptyList = findViewById(R.id.emptLst)

        editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                myAdapter.updateAdapter(myDbManager.readDbData(s.toString()))

                val count = myAdapter.itemCount
                if(count==0){
                    emptyList?.visibility = View.VISIBLE
                }else{
                    emptyList?.visibility = View.GONE
                }
            }
        })
    }

    private fun initAdapter() {
        rcView = findViewById(R.id.rсView)

        val swapper = deleteBySwap()
        swapper.attachToRecyclerView(rcView)
        rcView?.adapter = myAdapter

        val layoutMan = StaggeredGridLayoutManager(
            2, // span count
            StaggeredGridLayoutManager.VERTICAL) // orientation

        rcView?.layoutManager = layoutMan
    }

    private fun fillArrayForAdapter() {
        myAdapter.updateAdapter(myDbManager.readDbData(""))
    }

    private fun deleteBySwap(): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper.
        SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.deleteLine(viewHolder.adapterPosition, myDbManager)
                Toast.makeText(context, getString(R.string.delete), Toast.LENGTH_SHORT).show()
            }
        })
    }
}