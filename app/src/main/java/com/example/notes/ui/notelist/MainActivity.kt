package com.example.notes.ui.notelist

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.ui.NoteActivity
import com.example.notes.R
import com.example.notes.app.MyApplication
import com.example.notes.databinding.ActivityMainBinding

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    //lateinit var myDbManager : DbManager
    lateinit var myAdapter : NoteListAdapter

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //myDbManager = DbManager(this)
        myAdapter = NoteListAdapter(ArrayList(), this)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initAdapter()
        searchView()
    }

    override fun onResume() {
        super.onResume()
        mBinding.editt.text?.clear()
        mBinding.editt.clearFocus()
        fillArrayForAdapter()

        if (myAdapter.itemCount == 0) {
            mBinding.emptLst.visibility = View.VISIBLE
        } else {
            mBinding.emptLst.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //myDbManager.closeDb()
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
                myAdapter.updateAdapter((application as MyApplication).myDbManager.readDbData(s.toString()))
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
        myAdapter.updateAdapter((application as MyApplication).myDbManager.readDbData(""))
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
                showDialog(
                    onYesClick = {
                        myAdapter.deleteLine(viewHolder.adapterPosition, (application as MyApplication).myDbManager)
                        mBinding.emptLst.visibility = View.VISIBLE
                        Toast.makeText(context, getString(R.string.delete), Toast.LENGTH_SHORT).show()
                    }
                )
            }
        })
    }

    fun showDialog(onYesClick: () -> Unit) {
        val builder = AlertDialog.Builder(this)
            .create()
        val view = layoutInflater.inflate(R.layout.delete_dialog,null)
        val cancelButton = view.findViewById<TextView>(R.id.dialogButtonCancel)
        val yesButton = view.findViewById<TextView>(R.id.dialogButtonOK)
        builder.setView(view)
        cancelButton.setOnClickListener {
            builder.dismiss()
            myAdapter.updateAdapter((application as MyApplication).myDbManager.readDbData(""))
            mBinding.emptLst.visibility = View.GONE
        }
        yesButton.setOnClickListener {
            builder.dismiss()
            onYesClick()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }
}