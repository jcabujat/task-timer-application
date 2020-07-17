package com.example.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Basic database class for this application.
 * <p>
 * The only class that should use this is AppProvider.
 */
class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 1;

    // Implement AppDatabase as a singleton class
    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object
     *
     * @param context the content provider's context
     * @return SQLite database helper object
     */
    static AppDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: new instance created");
            instance = new AppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;
//        sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, " +
//                "Description TEXT, SortOrder INTEGER);";
        sSQL = "CREATE TABLE " + TasksContract.TABLE_NAME + " ("
                + TasksContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.Columns.TASKS_NAME + " TEXT NOT NULL, "
                + TasksContract.Columns.TASK_DESCRIPTION + " TEXT, "
                + TasksContract.Columns.TASK_SORT_ORDER + " INTEGER);";
        Log.d(TAG, "SQL query: " + sSQL);
        sqLiteDatabase.execSQL(sSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade: starts");
        switch (i) {
            case 1:
                // upgrade logic from version 1
            default:
                throw new IllegalStateException("onUpgrade() with unknown new version" + i1);
        }

    }
}
