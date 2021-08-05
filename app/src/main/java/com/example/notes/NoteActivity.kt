package com.example.notes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.notes.database.DbManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private val myDbManager = DbManager(this)

    private var titleTv: EditText? = null //title
    private var desTv: EditText? = null //description
    private var imageView: ImageView? = null //image

    private var editBtn: FloatingActionButton? = null
    private var saveBtn: FloatingActionButton? = null
    private var addPicBtn: FloatingActionButton? = null
    private var deleteInBtn: ImageButton? = null

    private val imgRequestCode = 10//request to gallery
    private var uriImg: String? = null
    private var imgView: String? = null

    private var imLayout: FrameLayout? = null

    private var forEdit: Boolean = false

    private var elementId = 0



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        titleTv = findViewById(R.id.editTitle)
        desTv = findViewById(R.id.editContent)
        editBtn = findViewById(R.id.editBtn)
        saveBtn = findViewById(R.id.saveBtn)
        imLayout = findViewById(R.id.imLayout)
        imageView = findViewById(R.id.imContent)
        addPicBtn = findViewById(R.id.addImBtn)
        deleteInBtn = findViewById(R.id.deleteInBtn)

        desTv?.requestFocus()

        addPicBtn?.visibility = View.VISIBLE
        saveBtn?.visibility = View.VISIBLE

        imLayout?.setOnClickListener {
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra(IntentConstants.URI_KEY, imgView)
            startActivity(intent)
        }

        editBtn?.setOnClickListener {
            btnState(1)
        }
        getInit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == imgRequestCode){
            imageView?.setImageURI(data?.data)

            uriImg = data?.data.toString()
            imgView = data?.data.toString()

            Log.d("MyLog", "$imgView")

            contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imLayout?.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    fun saveNewNote(view: View) {
        btnState(2)
        val title = findViewById<TextView>(R.id.editTitle).text
        val content = findViewById<TextView>(R.id.editContent).text

        addPicBtn?.visibility = View.GONE

        Log.d("MyLog", "$uriImg")

        if (forEdit) {
            myDbManager.updateLine(title.toString(), content.toString(), elementId, getCurTime(), imgView.toString())
            Toast.makeText(view.context, "Changes saved!", Toast.LENGTH_SHORT).show()
        } else {

            myDbManager.insertToDb(title.toString(), content.toString(), getCurTime(), uriImg.toString())
            Toast.makeText(view.context, "New note saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun getInit() {

        val i = intent

        if (i.getStringExtra(IntentConstants.TITLE_KEY) != null) {

            forEdit = true
            btnState(2)
            titleTv?.setText(i.getStringExtra(IntentConstants.TITLE_KEY))
            desTv?.setText(i.getStringExtra(IntentConstants.DES_KEY))
            elementId = i.getIntExtra(IntentConstants.ID_KEY, 0)

            if(i.getStringExtra(IntentConstants.URI_KEY) != "null") {
                imLayout?.visibility = View.VISIBLE
                imageView?.setImageURI(Uri.parse(i.getStringExtra(IntentConstants.URI_KEY)))
                imgView = i.getStringExtra(IntentConstants.URI_KEY)
            }
            else{
                imLayout?.visibility = View.GONE
            }
        }
    }

    private fun enableElements(isEnabled: Boolean) {
        titleTv?.isEnabled = isEnabled
        desTv?.isEnabled = isEnabled
    }

    private fun getCurTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)
    }

    private fun btnState(state : Int) {

        when (state) {
            1 -> {
                enableElements(true)
                saveBtn?.visibility = View.VISIBLE
                editBtn?.visibility = View.GONE
                addPicBtn?.visibility = View.VISIBLE
                deleteInBtn?.visibility = View.VISIBLE
            }

            2 -> {
                enableElements(false)
                saveBtn?.visibility = View.GONE
                editBtn?.visibility = View.VISIBLE
                addPicBtn?.visibility = View.GONE
                deleteInBtn?.visibility = View.GONE
            }
        }
    }

    fun addImage(view: View) {

        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.type = "image/*"
        startActivityForResult(i, imgRequestCode)

        deleteInBtn?.visibility = View.GONE
    }

    fun deleteImg(view: View) {
        imLayout?.visibility = View.GONE
        imgView = "null"
    }
}
