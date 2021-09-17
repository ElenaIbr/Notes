package com.example.notes.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.notes.model.Note
import com.example.notes.helper.dogImgArr
import com.example.notes.helper.dogNumber

class DbManager(context: Context) {

    private val myDbHelper = DbHelper(context)
    private var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    fun insertToDb (title: String, content: String, time: String, uri: String) {
        val values = ContentValues()
        values.put(DbClass.COLUMN_NAME_TITLE, title)
        values.put(DbClass.COLUMN_NAME_CONTENT, content)
        values.put(DbClass.COLUMN_TIME, time)
        values.put(DbClass.COLUMN_URI, uri)
        values.put(DbClass.COLUMN_DOG_RES, dogImgArr[dogNumber])

        if (dogNumber < dogImgArr.size){
            values.put(DbClass.COLUMN_DOG_RES, dogImgArr[dogNumber])
            dogNumber +=1
        }
        else {
            dogNumber =0
            values.put(DbClass.COLUMN_DOG_RES, dogImgArr[dogNumber])
        }

        db?.insert(DbClass.TABLE_NAME, null, values)
    }

    fun updateLine (title: String, content: String, id: Int, time: String, uri: String) {
        val itemToUpdate = BaseColumns._ID + "=$id"
        val values = ContentValues()

        values.put(DbClass.COLUMN_NAME_TITLE, title)
        values.put(DbClass.COLUMN_NAME_CONTENT, content)
        values.put(DbClass.COLUMN_TIME, time)
        values.put(DbClass.COLUMN_URI, uri)

        db?.update(DbClass.TABLE_NAME, values, itemToUpdate, null)
    }

    fun readDbData(searchTxt : String) : ArrayList<Note> {
        val dataList = ArrayList<Note>()

        val select = "${DbClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            DbClass.TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            select,              // The columns for the WHERE clause
            arrayOf("%$searchTxt%"),          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )


        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataTitle = this.getString(this.getColumnIndex(DbClass.COLUMN_NAME_TITLE))
                val dataDes = this.getString(this.getColumnIndex(DbClass.COLUMN_NAME_CONTENT))
                val dataId = this.getInt(this.getColumnIndex(BaseColumns._ID))
                val time = this.getString(this.getColumnIndex(DbClass.COLUMN_TIME))
                val uri = this.getString(this.getColumnIndex(DbClass.COLUMN_URI))
                val dog = this.getInt(this.getColumnIndex(DbClass.COLUMN_DOG_RES))

                val noteItem = Note()
                noteItem.id = dataId
                noteItem.title = dataTitle.toString()
                noteItem.dec = dataDes.toString()
                noteItem.time = time
                noteItem.uri = uri
                noteItem.dog = dog


                dataList.add(noteItem)
            }
        }
        cursor?.close()
        return dataList
    }

    fun closeDb() {
        myDbHelper.close()
    }

    fun deleteFromDb (id: String) {
        val itemToDelete = BaseColumns._ID + "=$id"
        db?.delete(DbClass.TABLE_NAME, itemToDelete, null)
    }
}