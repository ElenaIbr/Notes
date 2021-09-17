package com.example.notes.ui.notelist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.ui.NoteActivity
import com.example.notes.R
import com.example.notes.database.DbManager
import com.example.notes.databinding.ActivityMainBinding

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    val myDbManager = DbManager(this)
    val myAdapter = NoteListAdapter(ArrayList(), this)

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initAdapter()
        searchView()
    }

    override fun onResume() {
        super.onResume()
        mBinding.editt.text?.clear()
        mBinding.editt.clearFocus()
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

        mBinding.editt.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                myAdapter.updateAdapter(myDbManager.readDbData(s.toString()))

                val count = myAdapter.itemCount
                if(count==0){
                    mBinding.emptLst.visibility = View.VISIBLE
                    mBinding.noNotesDog.visibility = View.VISIBLE
                }else{
                    mBinding.emptLst.visibility = View.GONE
                    mBinding.noNotesDog.visibility = View.GONE
                }
            }
        })
    }

    private fun initAdapter() {

        val swapper = deleteBySwap()
        swapper.attachToRecyclerView(mBinding.rView)
        mBinding.rView.adapter = myAdapter

        val layoutManager = StaggeredGridLayoutManager(
            2, // span count
            StaggeredGridLayoutManager.VERTICAL) // orientation

        mBinding.rView.layoutManager = layoutManager
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