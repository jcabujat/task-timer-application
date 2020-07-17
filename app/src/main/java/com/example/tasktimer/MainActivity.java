package com.example.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] projection = {TasksContract.Columns._ID,
                TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASK_DESCRIPTION,
                TasksContract.Columns.TASK_SORT_ORDER};

        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();

        //** inserting new record in Tasks table
//        values.put(TasksContract.Columns.TASKS_NAME, "New Task 1");
//        values.put(TasksContract.Columns.TASK_DESCRIPTION, "Description 1");
//        values.put(TasksContract.Columns.TASK_SORT_ORDER, 2);
//        Uri uri = contentResolver.insert(TasksContract.CONTENT_URI, values);  // uri is not used. it is needed only to catch the returned uri of the insert method of contentResolver

        //** updating one specific record in Tasks table
//        values.put(TasksContract.Columns.TASKS_NAME, "Content Provider");
//        values.put(TasksContract.Columns.TASK_DESCRIPTION, "Record content provider video");
//        int count = contentResolver.update(TasksContract.buildTasksUri(4), values, null, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated.");

        //** updating multiple records in Tasks table
//        values.put(TasksContract.Columns.TASK_SORT_ORDER, "99");
//        values.put(TasksContract.Columns.TASK_DESCRIPTION, "Completed");
//        String selection = TasksContract.Columns.TASK_SORT_ORDER + " = " + 2;
//        int count = contentResolver.update(TasksContract.CONTENT_URI, values, selection, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated.");

        //** deleting specific record in Task table
//        int count = contentResolver.delete(TasksContract.buildTasksUri(3), null, null);
//        Log.d(TAG, "onCreate: " + count + " record(s) deleted.");

        //** updating records and tagging them for deletion
//        values.put(TasksContract.Columns.TASK_DESCRIPTION, "For deletion");
//        String selection = TasksContract.Columns.TASK_SORT_ORDER + " = ?";
//        String[] args = {"99"};
//        int count = contentResolver.update(TasksContract.CONTENT_URI, values, selection, args);
//        Log.d(TAG, "onCreate: " + count + " record(s) updated.");

        //** deleting multiple records that were tagged for deletion
//        String selection = TasksContract.Columns.TASK_DESCRIPTION + " = ?";
//        String[] args = {"For deletion"};
//        int count = contentResolver.delete(TasksContract.CONTENT_URI, selection, args);
//        Log.d(TAG, "onCreate: " + count + " record(s) deleted.");


        Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI,
//        Cursor cursor = contentResolver.query(TasksContract.buildTasksUri(3), // query table for specific record only
                projection,
                null,
                null,
                TasksContract.Columns.TASK_SORT_ORDER);

        if (cursor != null) {
            Log.d(TAG, "onCreate: number of rows = " + cursor.getCount());
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + " : " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: =============================");
            }
            cursor.close();
        }

//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}