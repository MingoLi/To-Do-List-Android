package com.example.liminghao.to_dolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.liminghao.to_dolist.data.TaskContract.TaskEntry;


public class TaskDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskDatabse";

    private static final int VERSION = 1;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    } // constructor

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskEntry.TASK_DESCRIPTION + " TEXT NOT NULL, " +
                TaskEntry.TASK_PRIORITY + " INTEGER NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}
