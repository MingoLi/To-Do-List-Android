package com.example.liminghao.to_dolist;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.example.liminghao.to_dolist.data.TaskContract;

/**
 * Created by liminghao on 2017/7/31.
 */

public class TaskFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TaskFragment.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    TaskCursorAdapter mAdapter;
    EmptyRecyclerView mRecyclerView;

    public TaskFragment() { /* Empty constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.item_list, container, false);

        mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new TaskCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        // set page label
        // TODO 替换为图片
        ((TextView)rootView.findViewById(R.id.list_label)).setText("TASK");

        // set visibility
        (rootView.findViewById(R.id.note_bottom_layer)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.note_item_fab)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.list_note_background)).setVisibility(View.GONE);

        // set empty view
        ((TextView)rootView.findViewById(R.id.empty_view_title)).setText(R.string.task_empty_title);
        ((TextView)rootView.findViewById(R.id.empty_view_subtitle)).setText(R.string.task_empty_subtitle);
        ((ImageView)rootView.findViewById(R.id.empty_image_view)).setImageResource(R.drawable.gallery_by_pxstardust);

        View emptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(emptyView);

        // setup header
        RecyclerViewHeader header = (RecyclerViewHeader) rootView.findViewById(R.id.recycler_view_header);
        header.attachTo(mRecyclerView);


        // setup onClickListener
        mRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getActivity(), new RecyclerViewClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if( position < mAdapter.getItemCount() - 1 ) {
                    Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                    Uri contentUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, Long.parseLong(view.getTag().toString()));
                    intent.setData(contentUri);
                    startActivity(intent);
//                    Toast.makeText(getActivity(),""+ contentUri.toString(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // TODO not implemented
            }
        }));


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if( viewHolder instanceof TaskCursorAdapter.TaskViewHolder ) {
                    int id = (int) viewHolder.itemView.getTag();

                    String stringId = Integer.toString(id);
                    Uri uri = Uri.withAppendedPath(TaskContract.TaskEntry.CONTENT_URI, stringId);

                    getActivity().getContentResolver().delete(uri, null, null);

                    getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, TaskFragment.this);

                    Toast.makeText(getActivity(), R.string.task_deleted_prompt, Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        // Set action button click listener
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.task_item_fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent addTaskIntent = new Intent(getActivity(), EditTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

            Cursor mTaskData;

            @Override
            protected void onStartLoading() {
                if( mTaskData != null ) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getActivity().getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.TASK_PRIORITY);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data. ");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                // TODO move this to the place of implementation
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
