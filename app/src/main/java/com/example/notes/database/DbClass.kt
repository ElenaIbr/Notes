package com.example.notes.database

import android.provider.BaseColumns

object DbClass: BaseColumns {
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_TIME = "time"
    const val COLUMN_URI = "uri"
    const val COLUMN_DOG_RES = "dog"


    const val DATABASE_VERSION = 1

    const val SQL_CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_CONTENT TEXT," +
                "$COLUMN_TIME TEXT," +
                "$COLUMN_URI TEXT," +
                "$COLUMN_DOG_RES INTEGER)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
}