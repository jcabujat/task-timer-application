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
    public static final int DATABASE_VERSION = 3;

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

        addTimingsTable(sqLiteDatabase);
        addDurationsView(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                // upgrade logic from version 1
                addTimingsTable(sqLiteDatabase);
                // fall through, to include version 2 upgrade logic as well
            case 2:
                // upgrade logic from version 2
                addDurationsView(sqLiteDatabase);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown new version" + newVersion);
        }

    }

    private void addTimingsTable(SQLiteDatabase sqLiteDatabase) {
        String sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.Columns.TIMING_TASK_ID + " INTEGER NOT NULL, "
                + TimingsContract.Columns.TIMING_START_TIME + " INTEGER, "
                + TimingsContract.Columns.TIMING_DURATION + " INTEGER);";
        Log.d(TAG, "SQL query: " + sSQL);
        sqLiteDatabase.execSQL(sSQL);

        sSQL = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
                + " FOR EACH ROW "
                + " BEGIN"
                + " DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.Columns._ID + " = OLD." + TasksContract.Columns._ID + ";"
                + " END;";
        Log.d(TAG, "SQL query: " + sSQL);
        sqLiteDatabase.execSQL(sSQL);
    }

    private void addDurationsView(SQLiteDatabase db) {
        /* Build the string for the below SQL command:
         CREATE VIEW vwTaskDurations AS
         SELECT Timings._id,
         Tasks.Name,
         Tasks.Description,
         Timings.StartTime,
         DATE(Timings.StartTime, 'unixepoch') AS StartDate,
         SUM(Timings.Duration) AS Duration
         FROM Tasks INNER JOIN Timings
         ON Tasks._id = Timings.TaskId
         GROUP BY Tasks._id, StartDate;
         */

        String sSQL = "CREATE VIEW " + DurationsContract.TABLE_NAME
                + " AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns._ID + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASKS_NAME + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.Columns.TASK_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMING_START_TIME + ","
                + " DATE(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMING_START_TIME + ", 'unixepoch')"
                + " AS " + DurationsContract.Columns.DURATIONS_START_DATE + ","
                + " SUM(" + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMING_DURATION + ")"
                + " AS " + DurationsContract.Columns.DURATIONS_DURATION
                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.Columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.Columns.TIMING_TASK_ID
                + " GROUP BY " + DurationsContract.Columns.DURATIONS_START_DATE + ", " + DurationsContract.Columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
    }
}
