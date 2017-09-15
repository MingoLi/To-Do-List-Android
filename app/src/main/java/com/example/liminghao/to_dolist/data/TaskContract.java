package com.example.liminghao.to_dolist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class TaskContract {

    private TaskContract() {}

    public static final String AUTHORITY = "com.example.liminghao.to_dolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns{

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public final static String _ID = BaseColumns._ID;

        public final static String TABLE_NAME = "tasks";

        public final static String TASK_DESCRIPTION = "descriptions";

        public final static String TASK_PRIORITY = "priority";

        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;
    }
}
