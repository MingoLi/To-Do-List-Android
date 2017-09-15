package com.example.liminghao.to_dolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by liminghao on 2017/7/30.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteDatabse";

    private static final int VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    } // constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " (" +
                NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY, " +
                NoteContract.NoteEntry.NOTE_SUBJECT + " TEXT, " +
                NoteContract.NoteEntry.NOTE_CONTENT + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
