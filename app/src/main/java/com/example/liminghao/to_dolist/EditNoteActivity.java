package com.example.liminghao.to_dolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.liminghao.to_dolist.data.NoteContract;

public class EditNoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXIST_NOTE_LOADER_ID = 1;

    private Uri mCurrentNoteUri;

    private EditText mSubjectText;
    private EditText mContentText;

//    private boolean mNoteHasChanged = false;
//
//    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            mNoteHasChanged = true;
//            return false;
//        }
//    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        if(mCurrentNoteUri != null) {
            getSupportLoaderManager().initLoader(EXIST_NOTE_LOADER_ID, null, this);
        }

        mSubjectText = (EditText) findViewById(R.id.edit_note_subject);
        mContentText = (EditText) findViewById(R.id.edit_note_content);
//
//        mSubjectText.setOnTouchListener(mTouchListener);
//        mContentText.setOnTouchListener(mTouchListener);
    }



    public void onClickSaveNote(View view) {

        String content = ((EditText)findViewById(R.id.edit_note_content)).getText().toString();
        String subject = ((EditText)findViewById(R.id.edit_note_subject)).getText().toString();

        if(TextUtils.isEmpty(content) && TextUtils.isEmpty(subject)) {
            Toast.makeText(EditNoteActivity.this, R.string.note_empty_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values  = new ContentValues();
        values.put(NoteContract.NoteEntry.NOTE_SUBJECT, subject);
        values.put(NoteContract.NoteEntry.NOTE_CONTENT, content);



        if(mCurrentNoteUri == null) {
            Uri uri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);
            if( uri != null ) {
                Toast.makeText(getBaseContext(), R.string.note_saved_prompt, Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), R.string.note_saved_prompt, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void onClickDiscardNote(View view) {
        finish();
    }

    public void onClickDeleteNote(View view) {

        if(mCurrentNoteUri == null) {

        } else {
            int rowsAffected = getContentResolver().delete(mCurrentNoteUri, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), R.string.note_deleted_prompt, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mNoteData;

            @Override
            protected void onStartLoading() {
                if (mNoteData != null) {
                    deliverResult(mNoteData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(mCurrentNoteUri,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(EditNoteActivity.class.getSimpleName(), "Failed to asynchronously load data. ");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mNoteData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst()){
            int idIndex = data.getColumnIndex(NoteContract.NoteEntry._ID);
            int subjectIndex = data.getColumnIndex(NoteContract.NoteEntry.NOTE_SUBJECT);
            int contentIndex = data.getColumnIndex(NoteContract.NoteEntry.NOTE_CONTENT);

            final int id = data.getInt(idIndex);
            String subject = data.getString(subjectIndex);
            String content = data.getString(contentIndex);

            mSubjectText.setText(subject);
            mContentText.setText(content);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSubjectText.setText("");
        mContentText.setText("");
    }

    @Override
    public void onBackPressed() {

        String content = ((EditText)findViewById(R.id.edit_note_content)).getText().toString();
        String subject = ((EditText)findViewById(R.id.edit_note_subject)).getText().toString();

        ContentValues values  = new ContentValues();
        values.put(NoteContract.NoteEntry.NOTE_SUBJECT, subject);
        values.put(NoteContract.NoteEntry.NOTE_CONTENT, content);

        if(mCurrentNoteUri == null) {

            if(TextUtils.isEmpty(content) && TextUtils.isEmpty(subject)) {
                super.onBackPressed();
                return;
            }

            Uri uri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);
            if( uri != null ) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), ""+rowsAffected, Toast.LENGTH_SHORT).show();
            }
        }

        super.onBackPressed();
    }
}
