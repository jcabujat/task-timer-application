package com.example.tasktimer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked {
    private static final String TAG = "MainActivity";

    // Whether or not the activity is in 2-pane mode
    // i.e. running in landscape in a tablet.
    private boolean mTwoPane = false;

    public static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.task_detail_container) != null) {
            // Detail container view will present only in large screen layouts (res/value-land and res/value-sw600dp)
            // If this view is present, then activity should be in two-pane mode.
            mTwoPane = true;
        }

//        String[] projection = {TasksContract.Columns._ID,
//                TasksContract.Columns.TASKS_NAME,
//                TasksContract.Columns.TASK_DESCRIPTION,
//                TasksContract.Columns.TASK_SORT_ORDER};
//
//        ContentResolver contentResolver = getContentResolver();
//        ContentValues values = new ContentValues();

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


//        Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI,
//        Cursor cursor = contentResolver.query(TasksContract.buildTasksUri(3), // query table for specific record only
//                projection,
//                null,
//                null,
//                TasksContract.Columns.TASK_SORT_ORDER);

//        if (cursor != null) {
//            Log.d(TAG, "onCreate: number of rows = " + cursor.getCount());
//            while (cursor.moveToNext()) {
//                for (int i = 0; i < cursor.getColumnCount(); i++) {
//                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + " : " + cursor.getString(i));
//                }
//                Log.d(TAG, "onCreate: =============================");
//            }
//            cursor.close();
//        }

//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();

    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_detail_container);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
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
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDuration:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        getContentResolver().delete(TasksContract.buildTasksUri(task.getId()), null, null);
        Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_LONG).show();
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");
        if (mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (landscape mode or using tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.task_detail_container, fragment);
//            fragmentTransaction.commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.task_detail_container, fragment)
                    .commit();

        } else {
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            // in single-pane mode, start the detail activity for the selected item Id.
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (task != null) { // editing a task
                detailIntent.putExtra(Task.class.getSimpleName(), task);
                startActivity(detailIntent);
            } else { // adding a new Task
                startActivity(detailIntent);
            }
        }
    }
}