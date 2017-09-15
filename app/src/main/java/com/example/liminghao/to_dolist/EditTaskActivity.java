package com.example.liminghao.to_dolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.RadioButton;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.liminghao.to_dolist.data.TaskContract;
import com.example.liminghao.to_dolist.data.TaskContract.TaskEntry;


/**
 * Created by liminghao on 2017/7/30.
 */

public class EditTaskActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXIST_TASK_LOADER_ID = 0;

    private Uri mCurrentTaskUri;

    private EditText mTaskDescription;
    private RadioGroup mPriorityGroup;

    private int mPriority;

//    private boolean mTaskHasChanged = false;
//
//    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            mTaskHasChanged = true;
//            return false;
//        }
//    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Intent intent = getIntent();
        mCurrentTaskUri = intent.getData();

        mTaskDescription = (EditText) findViewById(R.id.edit_task_content);
        mPriorityGroup = (RadioGroup) findViewById(R.id.add_task_radio_group);
//
//        mTaskDescription.setOnTouchListener(mTouchListener);
//        mPriorityGroup.setOnTouchListener(mTouchListener);

        if(mCurrentTaskUri != null) {
            getSupportLoaderManager().initLoader(EXIST_TASK_LOADER_ID, null, this);
        } else {
            // set default value for priority
            ((RadioButton)findViewById(R.id.priority_low)).setChecked(true);
            mPriority = 3;
        }
    }

    // TODO handle empty string
    public void onClickAddTask(View view) {

        String input = ((EditText)findViewById(R.id.edit_task_content)).getText().toString();
        if(TextUtils.isEmpty(input)) {
            Toast.makeText(EditTaskActivity.this, R.string.task_empty_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values  = new ContentValues();
        values.put(TaskEntry.TASK_DESCRIPTION, input);
        values.put(TaskEntry.TASK_PRIORITY, mPriority);

//        Uri uri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);

        if(mCurrentTaskUri == null) {
            Uri uri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
            if( uri != null ) {
                Toast.makeText(getBaseContext(), R.string.task_saved_prompt, Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentTaskUri, values, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), R.string.task_saved_prompt, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void onClickDiscardTask(View view) {
        finish();
    }

    public void onClickDeleteTask(View view) {

        if(mCurrentTaskUri == null) {

        } else {
            int rowsAffected = getContentResolver().delete(mCurrentTaskUri, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), R.string.task_deleted_prompt, Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void onPrioritySelected(View view) {
        if(((RadioButton)findViewById(R.id.priority_low)).isChecked()) {
            mPriority = 3;
        } else if (((RadioButton)findViewById(R.id.priority_medium)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton)findViewById(R.id.priority_high)).isChecked()) {
            mPriority = 1;
        }
    }

    public void setPriorityRadioButton(int priority){
        switch (priority) {
            case 1:
                ((RadioButton)findViewById(R.id.priority_high)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.priority_medium)).setChecked(true);
                break;
            case 3:
                ((RadioButton)findViewById(R.id.priority_low)).setChecked(true);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(mCurrentTaskUri,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(EditTaskActivity.class.getSimpleName(), "Failed to asynchronously load data. ");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst()) {
            int idIndex = data.getColumnIndex(TaskEntry._ID);
            int descriptionIndex = data.getColumnIndex(TaskEntry.TASK_DESCRIPTION);
            int priorityIndex = data.getColumnIndex(TaskEntry.TASK_PRIORITY);

            final int id = data.getInt(idIndex);
            String description = data.getString(descriptionIndex);
            mPriority = data.getInt(priorityIndex);

            mTaskDescription.setText(description);
            setPriorityRadioButton(mPriority);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTaskDescription.setText("");
        setPriorityRadioButton(3);
    }

    @Override
    public void onBackPressed() {

        String input = ((EditText)findViewById(R.id.edit_task_content)).getText().toString();

        ContentValues values  = new ContentValues();
        values.put(TaskEntry.TASK_DESCRIPTION, input);
        values.put(TaskEntry.TASK_PRIORITY, mPriority);

        if(mCurrentTaskUri == null) { // new

            if(TextUtils.isEmpty(input)) {
                super.onBackPressed();
                return;
            }

            Uri uri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
            if( uri != null ) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
            }

        } else {                      // edit
            int rowsAffected = getContentResolver().update(mCurrentTaskUri, values, null, null);
            if(rowsAffected != 0 ) {
                Toast.makeText(getBaseContext(), ""+rowsAffected, Toast.LENGTH_SHORT).show();
            }

        }

        super.onBackPressed();
    }
}
