package com.example.liminghao.to_dolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.liminghao.to_dolist.R;

import static com.example.liminghao.to_dolist.data.TaskContract.TaskEntry;
import static com.example.liminghao.to_dolist.data.NoteContract.NoteEntry;


public class MyContentProvider extends ContentProvider {

    public static final int TASKS = 100;

    public static final int TASK_WITH_ID = 101;

    public static final int NOTES = 200;

    public static final int NOTE_WITH_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // For Task
        sUriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        sUriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);

        // For Note
        sUriMatcher.addURI(NoteContract.AUTHORITY, NoteContract.PATH_TASKS, NOTES);
        sUriMatcher.addURI(NoteContract.AUTHORITY, NoteContract.PATH_TASKS + "/#", NOTE_WITH_ID);
    }

    private TaskDbHelper mTaskDbHelper;

    private NoteDbHelper mNoteDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        mNoteDbHelper = new NoteDbHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase taskDb = mTaskDbHelper.getReadableDatabase();

        final SQLiteDatabase noteDb = mNoteDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch(match) {
            case TASKS:
                retCursor = taskDb.query(TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TASK_WITH_ID:
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                retCursor = taskDb.query(TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTES:
                retCursor = noteDb.query(NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NOTE_WITH_ID:
                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                retCursor = noteDb.query(NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        // TODO check values illegibility


        final SQLiteDatabase taskDb = mTaskDbHelper.getWritableDatabase();
        final SQLiteDatabase noteDb = mNoteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;          // Uri to be returned
        long id;

        switch (match) {
            case TASKS:
                // TODO checking does not work
                // Check legibility
                String task = values.getAsString(TaskEntry.TASK_DESCRIPTION);
                if( task == null ) {
                    throw new IllegalArgumentException(getContext().getString(R.string.task_required));
                }

                id = taskDb.insert(TaskEntry.TABLE_NAME, null, values);
                if( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case NOTES:
                // TODO check if this works
                String note = values.getAsString(NoteEntry.NOTE_CONTENT);
                if( note == null ) {
                    throw new IllegalArgumentException(getContext().getString(R.string.note_required));
                }

                id = noteDb.insert(NoteEntry.TABLE_NAME, null, values);
                if( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase taskDb = mTaskDbHelper.getWritableDatabase();
        final SQLiteDatabase noteDb = mNoteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int deleted;
        String id;

        switch (match) {
            case TASK_WITH_ID:
                id = uri.getPathSegments().get(1);
                deleted = taskDb.delete(TaskEntry.TABLE_NAME, "_ID=?", new String[]{id});
                break;
            case NOTE_WITH_ID:
                id = uri.getPathSegments().get(1);
                deleted = noteDb.delete(NoteEntry.TABLE_NAME, "_ID=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase taskDb = mTaskDbHelper.getWritableDatabase();
        SQLiteDatabase noteDb = mNoteDbHelper.getWritableDatabase();
        int rowUpdated = 0;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case TASK_WITH_ID:

                if( values.containsKey(TaskEntry.TASK_DESCRIPTION) ) {
                    String task = values.getAsString(TaskEntry.TASK_DESCRIPTION);
                    if( task == null ) {
                        throw new IllegalArgumentException(getContext().getString(R.string.task_required));
                    }
                }

                if( values.size() == 0 ) {
                    return 0;
                }

                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowUpdated = taskDb.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case NOTE_WITH_ID:
                if( values.containsKey(NoteEntry.NOTE_CONTENT) ) {
                    String note = values.getAsString(NoteEntry.NOTE_CONTENT);
                    if( note == null ) {
                        throw new IllegalArgumentException(getContext().getString(R.string.note_required));
                    }
                }

                if( values.size() == 0 ) {
                    return 0;
                }

                selection = NoteEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                rowUpdated = noteDb.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Update is not supported for: " + uri);
        }

        if( rowUpdated != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case TASK_WITH_ID:
                return TaskEntry.CONTENT_ITEM_TYPE;
            case TASKS:
                return TaskEntry.CONTENT_LIST_TYPE;
            case NOTE_WITH_ID:
                return NoteEntry.CONTENT_ITEM_TYPE;
            case NOTES:
                return NoteEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match );
        }
    }

}
