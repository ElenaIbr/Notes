package com.example.notes.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.notes.app.MyApplication
import com.example.notes.databinding.ActivityNoteBinding
import com.example.notes.utilits.IntentConstants
import java.text.SimpleDateFormat
import java.util.*
import com.example.notes.R


class NoteActivity : AppCompatActivity() {

    //private val myDbManager = DbManager(this)

    private lateinit var mBinding: ActivityNoteBinding

    //request to gallery
    private val imgRequestCode = 10
    private var uriImg: String? = null
    private var imgView: String? = null

    private var forEdit: Boolean = false
    private var elementId = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mBinding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.editTitle.requestFocus()
        mBinding.addImBtn.visibility = View.VISIBLE
        mBinding.saveBtn.visibility = View.VISIBLE

        mBinding.returnToList.setOnClickListener {
            onBackPressed()
        }
        mBinding.imLayout.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra(IntentConstants.URI_KEY, imgView)
            startActivity(intent)
        }
        mBinding.editBtn.setOnClickListener {
            btnState(1)
        }

        getInit()
    }

    override fun onResume() {
        super.onResume()

        mBinding.shareBtn.setOnClickListener {
            val titleSend = findViewById<TextView>(R.id.editTitle).text
            val contentSend = findViewById<TextView>(R.id.editContent).text
            shareContent(
                title = titleSend.toString(),
                content = contentSend.toString(),
                uriImg
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //myDbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == imgRequestCode){
            mBinding.imContent.setImageURI(data?.data)

            uriImg = data?.data.toString()
            imgView = data?.data.toString()

            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mBinding.imLayout.visibility = View.VISIBLE
        }
    }

    fun saveNewNote(view: View) {
        btnState(2)
        val title = findViewById<TextView>(R.id.editTitle).text
        val content = findViewById<TextView>(R.id.editContent).text

        mBinding.addImBtn.visibility = View.GONE

        if (forEdit) {
            (application as MyApplication).myDbManager.updateLine(title.toString(), content.toString(), elementId, getCurTime(), imgView.toString())
            Toast.makeText(view.context, "Changes saved!", Toast.LENGTH_SHORT).show()
        } else {
            (application as MyApplication).myDbManager.insertToDb(title.toString(), content.toString(), getCurTime(), uriImg.toString())
            Toast.makeText(view.context, "New note saved!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    private fun getInit() {

        val i = intent

        if (i.getStringExtra(IntentConstants.TITLE_KEY) != null) {
            forEdit = true
            btnState(2)
            mBinding.editTitle.setText(i.getStringExtra(IntentConstants.TITLE_KEY))
            mBinding.editContent.setText(i.getStringExtra(IntentConstants.DES_KEY))
            elementId = i.getIntExtra(IntentConstants.ID_KEY, 0)

            if(i.getStringExtra(IntentConstants.URI_KEY) != "null") {
                mBinding.imLayout.visibility = View.VISIBLE
                mBinding.imContent.setImageURI(Uri.parse(i.getStringExtra(IntentConstants.URI_KEY)))
                imgView = i.getStringExtra(IntentConstants.URI_KEY)
            }
            else{
                mBinding.imLayout.visibility = View.GONE
            }
        }
    }

    private fun enableElements(isEnabled: Boolean) {
        mBinding.editTitle.isEnabled = isEnabled
        mBinding.editContent.isEnabled = isEnabled
    }

    private fun getCurTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM kk:mm", Locale.getDefault())
        return formatter.format(time)
    }

    private fun btnState(state : Int) {
        when (state) {
            //hide edit buttons
            1 -> {
                enableElements(true)
                mBinding.saveBtn.visibility = View.VISIBLE
                mBinding.editBtn.visibility = View.GONE
                mBinding.addImBtn.visibility = View.VISIBLE
                mBinding.deleteInBtn.visibility = View.VISIBLE
            }

            //show edit buttons
            2 -> {
                enableElements(false)
                mBinding.saveBtn.visibility = View.GONE
                mBinding.editBtn.visibility = View.VISIBLE
                mBinding.addImBtn.visibility = View.GONE
                mBinding.deleteInBtn.visibility = View.GONE
            }
        }
    }

    fun addImage(view: View) {

        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.type = "image/*"
        startActivityForResult(i, imgRequestCode)
        mBinding.deleteInBtn.visibility = View.GONE
    }

    fun deleteImg(view: View) {
        mBinding.imLayout.visibility = View.GONE
        imgView = "null"
    }

    private fun shareContent(title: String, content: String, uri: String?) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share")
        startActivity(shareIntent)
    }
}