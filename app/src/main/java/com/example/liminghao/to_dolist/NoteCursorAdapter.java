package com.example.liminghao.to_dolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.liminghao.to_dolist.data.NoteContract.NoteEntry;
import com.example.liminghao.to_dolist.data.TaskContract;

/**
 * Created by liminghao on 2017/7/31.
 */

public class NoteCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor mCursor;
    private Context context;

    private static final int FOOTER_VIEW = 1;

    // Constructor
    public NoteCursorAdapter(Context context) { this.context = context; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View view = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
//
//        return new NoteCursorAdapter.NoteViewHolder(view);

        View v;

        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(context).inflate(R.layout.footer_layout, parent, false);

            NoteFooterViewHolder vh = new NoteFooterViewHolder(v);

            return vh;
        }

        v = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);

        return new NoteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        int idIndex = mCursor.getColumnIndex(NoteEntry._ID);
//        int subjectIndex = mCursor.getColumnIndex(NoteEntry.NOTE_SUBJECT);
//        int contentIndex = mCursor.getColumnIndex(NoteEntry.NOTE_CONTENT);
//
//        mCursor.moveToPosition(position);
//
//        final int id = mCursor.getInt(idIndex);
//        String subject = mCursor.getString(subjectIndex);
//        String content = mCursor.getString(contentIndex);
//
//        holder.itemView.setTag(id);
//        holder.noteSubjectView.setText(subject);
//        holder.noteContentView.setText(content);

        try {
            if (holder instanceof NoteCursorAdapter.NoteViewHolder) {
                NoteViewHolder vh = (NoteViewHolder) holder;

                int idIndex = mCursor.getColumnIndex(NoteEntry._ID);
                int subjectIndex = mCursor.getColumnIndex(NoteEntry.NOTE_SUBJECT);
                int contentIndex = mCursor.getColumnIndex(NoteEntry.NOTE_CONTENT);

                mCursor.moveToPosition(position);

                final int id = mCursor.getInt(idIndex);
                String subject = mCursor.getString(subjectIndex);
                String content = mCursor.getString(contentIndex);

                vh.itemView.setTag(id);
                vh.noteSubjectView.setText(subject);
                vh.noteContentView.setText(content);

            } else if (holder instanceof NoteFooterViewHolder) {
                NoteFooterViewHolder vh = (NoteFooterViewHolder) holder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
//        if(mCursor == null) {
//            return 0;
//        }
//
//        return mCursor.getCount();

        if (mCursor == null) {
            return 0;
        }

        // TODO return 0
        if (mCursor.getCount() == 0) {
            //Return 1 here to show nothing
            return 0;
        }

        // Add extra view to show the footer view
        return mCursor.getCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mCursor.getCount()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    public Cursor swapCursor(Cursor c) {
        if( mCursor == c ) {
            return null;
        }

        Cursor temp = mCursor;
        mCursor = c;

        if( c != null ) {
            this.notifyDataSetChanged();
        }

        return temp;
    }



    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteSubjectView;
        TextView noteContentView;

        public NoteViewHolder(View itemView) {
            super(itemView);

            noteSubjectView = (TextView) itemView.findViewById(R.id.note_subject_view);
            noteContentView = (TextView) itemView.findViewById(R.id.note_content_view);
        }

    }

    public class NoteFooterViewHolder extends RecyclerView.ViewHolder {

        public NoteFooterViewHolder(View itemView) {
            super(itemView);

        }
    }
}

