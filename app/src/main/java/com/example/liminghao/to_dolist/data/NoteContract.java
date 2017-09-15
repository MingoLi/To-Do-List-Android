package com.example.liminghao.to_dolist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by liminghao on 2017/7/30.
 */

public final class NoteContract {

    private NoteContract() {}

    public static final String AUTHORITY = "com.example.liminghao.to_dolist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASKS = "notes";

    public static final class NoteEntry implements BaseColumns {

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public final static String _ID = BaseColumns._ID;

        public final static String TABLE_NAME = "notes";

        public final static String NOTE_CONTENT = "contents";

        public final static String NOTE_SUBJECT = "subjects";

        public final static String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_TASKS;
    }
}