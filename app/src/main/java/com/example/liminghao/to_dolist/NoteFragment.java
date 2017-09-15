package com.example.liminghao.to_dolist;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.example.liminghao.to_dolist.data.NoteContract;

public class NoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = NoteFragment.class.getSimpleName();
    private static final int NOTE_LOADER_ID = 1;

    NoteCursorAdapter mAdapter;
    EmptyRecyclerView mRecyclerView;

    public NoteFragment() { /* Empty constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.item_list, container, false);

        mRecyclerView = (EmptyRecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new NoteCursorAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        // set page label
        // TODO 替换为图片
        ((TextView)rootView.findViewById(R.id.list_label)).setText("NOTE");

        // set visibility
        (rootView.findViewById(R.id.edit_note_bottom_layer)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.task_item_fab)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.list_task_background)).setVisibility(View.GONE);

        // set empty view
        ((TextView)rootView.findViewById(R.id.empty_view_title)).setText(R.string.note_empty_title);
        ((TextView)rootView.findViewById(R.id.empty_view_subtitle)).setText(R.string.note_empty_subtitle);
        ((ImageView)rootView.findViewById(R.id.empty_image_view)).setImageResource(R.drawable.myfiles_by_pxstardust);

        View emptyView = rootView.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(emptyView);

        // setup header
        RecyclerViewHeader header = (RecyclerViewHeader) rootView.findViewById(R.id.recycler_view_header);
        header.attachTo(mRecyclerView);

        // setup clickListener
        mRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getActivity(), new RecyclerViewClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if( position < mAdapter.getItemCount() - 1 ) {
                    Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                    Uri contentUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, Long.parseLong(view.getTag().toString()));
                    intent.setData(contentUri);
                    startActivity(intent);
//                    Toast.makeText(getActivity(), "" + contentUri.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // TODO not implemented
            }
        }));


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if( viewHolder instanceof NoteCursorAdapter.NoteViewHolder ) {
                    int id = (int) viewHolder.itemView.getTag();

                    String stringId = Integer.toString(id);
                    Uri uri = Uri.withAppendedPath(NoteContract.NoteEntry.CONTENT_URI, stringId);

                    getActivity().getContentResolver().delete(uri, null, null);

                    getActivity().getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, NoteFragment.this);

                    Toast.makeText(getActivity(), R.string.note_deleted_prompt, Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        // Set action button click listener
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.note_item_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNoteIntent = new Intent(getActivity(), EditNoteActivity.class);
                startActivity(addNoteIntent);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(NOTE_LOADER_ID, null, this);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getActivity()) {

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
                    return getActivity().getContentResolver().query(NoteContract.NoteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data. ");
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
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
