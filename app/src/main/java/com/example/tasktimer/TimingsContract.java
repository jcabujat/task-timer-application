package com.example.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static com.example.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

public class TimingsContract {

    static final String TABLE_NAME = "Timings";

    //Tasks table fields
    public static class Columns {

        public static final String _ID = BaseColumns._ID;
        public static final String TIMING_TASK_ID = "TaskId";
        public static final String TIMING_START_TIME = "StartTime";
        public static final String TIMING_DURATION = "Duration";

        private Columns() {
            // private constructor to prevent instantiation.
        }
    }

    /**
     * The URI to access the timing table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.android.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTimingsUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    public static long getTimingId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
