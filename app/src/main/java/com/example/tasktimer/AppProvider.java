package com.example.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Provider for the Task Timer App.
 * This is the only class that knows about {@link AppDatabase}
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    public static final UriMatcher sUriMatcher = buildMatcher();

    static final String CONTENT_AUTHORITY = "com.example.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final int TASKS = 100;
    public static final int TASKS_ID = 101;

    public static final int TIMINGS = 200;
    public static final int TIMINGS_ID = 201;

    public static final int TASK_TIMINGS = 300;
    public static final int TASK_TIMINGS_ID = 301;

    public static final int TASK_DURATION = 400;
    public static final int TASK_DURATION_ID = 401;

    private static UriMatcher buildMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // e.g. content://com.example.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);

        //e.g. content://com.example.tasktimer.provider/Tasks/2
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);

//        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASK_DURATION);
//        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASK_DURATION_ID);

        return matcher;

    }


    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

            case TIMINGS:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                break;

            case TIMINGS_ID:
                queryBuilder.setTables(TimingsContract.TABLE_NAME);
                long timingId = TimingsContract.getTimingId(uri);
                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);

//            case TASK_DURATION:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//
//            case TASK_DURATION_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId = DurationsContract.getDurationId(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

            case TIMINGS:
                return TimingsContract.CONTENT_TYPE;

            case TIMINGS_ID:
                return TimingsContract.CONTENT_ITEM_TYPE;

//            case TASK_DURATION:
//                return DurationsContract.CONTENT_TYPE;
//
//            case TASK_DURATION_ID:
//                return DurationsContract.CONTENT_ITEM_TYPE;
//
            default:
                throw new IllegalArgumentException("unknown Uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "Entering insert with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TasksContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TasksContract.buildTasksUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TimingsContract.TABLE_NAME, null, contentValues);
                if (recordId >= 0) {
                    returnUri = TimingsContract.buildTimingsUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (recordId >= 0) {
            // something was inserted
            Log.d(TAG, "insert: setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "insert: nothing was inserted");
        }

        Log.d(TAG, "Exiting insert: returning " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "delete: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ") ";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = mOpenHelper.getWritableDatabase();
                long timingId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ") ";
                }
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);

        }
        if (count > 0) {
            // something was deleted
            Log.d(TAG, "delete: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing was deleted");
        }

        Log.d(TAG, "Exiting delete: returning " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ") ";
                }
                count = db.update(TasksContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            case TIMINGS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TimingsContract.TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case TIMINGS_ID:
                db = mOpenHelper.getWritableDatabase();
                long timingId = TimingsContract.getTimingId(uri);
                selectionCriteria = TimingsContract.Columns._ID + " = " + timingId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ") ";
                }
                count = db.update(TimingsContract.TABLE_NAME, contentValues, selectionCriteria, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);

        }
        if (count > 0) {
            // something was deleted
            Log.d(TAG, "update: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing was updated");
        }
        Log.d(TAG, "Exiting update: returning " + count);
        return count;
    }
}
