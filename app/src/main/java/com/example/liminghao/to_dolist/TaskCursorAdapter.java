package com.example.liminghao.to_dolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.liminghao.to_dolist.data.TaskContract.TaskEntry;


/**
 * Created by liminghao on 2017/7/29.
 */

public class TaskCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor mCursor;
    private Context context;

    private static final int FOOTER_VIEW = 1;

    // Constructor
    public TaskCursorAdapter(Context context) { this.context = context; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
//
//        return new TaskViewHolder(view);

        View v;

        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(context).inflate(R.layout.footer_layout, parent, false);

            TaskFooterViewHolder vh = new TaskFooterViewHolder(v);

            return vh;
        }

        v = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

//        int idIndex = mCursor.getColumnIndex(TaskEntry._ID);
//        int descriptionIndex = mCursor.getColumnIndex(TaskEntry.TASK_DESCRIPTION);
//        int priorityIndex = mCursor.getColumnIndex(TaskEntry.TASK_PRIORITY);
//
//        mCursor.moveToPosition(position);
//
//        final int id = mCursor.getInt(idIndex);
//        String description = mCursor.getString(descriptionIndex);
//        int priority = mCursor.getInt(priorityIndex);
//
//        holder.itemView.setTag(id);
//        holder.taskDescriptionView.setText(description);
//        holder.priorityView.setText(String.valueOf(priority));
//
//        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
//        int priorityColor = getPriorityColor(priority);
//        priorityCircle.setColor(priorityColor);

        try {
            if (holder instanceof TaskViewHolder) {
                TaskViewHolder vh = (TaskViewHolder) holder;

                int idIndex = mCursor.getColumnIndex(TaskEntry._ID);
                int descriptionIndex = mCursor.getColumnIndex(TaskEntry.TASK_DESCRIPTION);
                int priorityIndex = mCursor.getColumnIndex(TaskEntry.TASK_PRIORITY);

                mCursor.moveToPosition(position);

                final int id = mCursor.getInt(idIndex);
                String description = mCursor.getString(descriptionIndex);
                int priority = mCursor.getInt(priorityIndex);

                vh.itemView.setTag(id);
                vh.taskDescriptionView.setText(description);
                vh.priorityView.setText(String.valueOf(priority));

                // TODO priority color set here
                GradientDrawable priorityCircle = (GradientDrawable) vh.priorityView.getBackground();
                int priorityColor = getPriorityColor(priority);
                priorityCircle.setColor(priorityColor);

            } else if (holder instanceof TaskFooterViewHolder ) {
                TaskFooterViewHolder vh = (TaskFooterViewHolder) holder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 3: priorityColor = ContextCompat.getColor(context, R.color.low_priority_blue);
                break;
            case 2: priorityColor = ContextCompat.getColor(context, R.color.medium_priority_green);
                break;
            case 1: priorityColor = ContextCompat.getColor(context, R.color.high_priority_red);
                break;
            default:
                break;
        }

        return priorityColor;
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



    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskDescriptionView;
        TextView priorityView;

        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.task_content_view);
            priorityView = (TextView) itemView.findViewById(R.id.task_priority_view);
        }


    }

    public class TaskFooterViewHolder extends RecyclerView.ViewHolder {

        public TaskFooterViewHolder(View itemView) {
            super(itemView);

        }
    }

}

