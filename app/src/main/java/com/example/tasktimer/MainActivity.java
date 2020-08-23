package com.example.tasktimer;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tasktimer.debug.TestData;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked,
        AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";
    private AlertDialog mDialog = null;

    // Whether or not the activity is in 2-pane mode
    // i.e. running in landscape in a tablet.
    private boolean mTwoPane = false;

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;
    private static final int DIALOG_ID_CANCEL_EDIT_UP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTwoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        Log.d(TAG, "onCreate: twoPane is " + mTwoPane);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // if AddEditActivityFragment exists, we are editing
        Boolean editing = fragmentManager.findFragmentById(R.id.task_detail_container) != null;
        Log.d(TAG, "onCreate: editing is " + editing);

        // We need references to the containers so we can show or hide them as necessary
        View addEditFragment = findViewById(R.id.task_detail_container);
        View mainFragment = findViewById(R.id.fragment);

        if (mTwoPane) {
            Log.d(TAG, "onCreate: in two-pane mode");
            mainFragment.setVisibility(View.VISIBLE);
            addEditFragment.setVisibility(View.VISIBLE);
        } else if (editing) {
            Log.d(TAG, "onCreate: single-pane, editing mode");
            // hide the left-hand layout (i.e. mainFragment) to make room for editing
            mainFragment.setVisibility(View.GONE);
            addEditFragment.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate: single-pane, task browsing mode");
            // show mainFragment and hide addEditFragment
            mainFragment.setVisibility(View.VISIBLE);
            addEditFragment.setVisibility(View.GONE);
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

        View addEditFragment = findViewById(R.id.task_detail_container);
        View mainFragment = findViewById(R.id.fragment);

        if (!mTwoPane) {
            // hide addEditFragment and show mainFragment
            addEditFragment.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (BuildConfig.DEBUG) {
            MenuItem generate = menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }
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
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: back arrow pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.task_detail_container);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;  // indicate we are handling this
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void showAboutDialog() {
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);

        // adding a button to dismiss the dialog
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d(TAG, "onClick: Entering builder.setPositiveButton.onClick, showing = " + mDialog.isShowing());
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        TextView about_url = messageView.findViewById(R.id.about_url);
        if (about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) v).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found, cannot visit world-wide web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        mDialog.show();
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();

        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);
        args.putLong("TaskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");

        Log.d(TAG, "taskEditRequest: in two-pane mode (landscape mode or using tablet)");
        AddEditActivityFragment fragment = new AddEditActivityFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(), task);
        fragment.setArguments(arguments);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.task_detail_container, fragment)
                .commit();

        if (!mTwoPane) {
            Log.d(TAG, "taskEditRequest: single-pane mode, editing");
            View addEditFragment = findViewById(R.id.task_detail_container);
            View mainFragment = findViewById(R.id.fragment);
            View addEditLayout = findViewById(R.id.task_detail_container);
            mainFragment.setVisibility(View.GONE);
            addEditLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: starts");

        switch (dialogId) {
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task ID is zero");
                getContentResolver().delete(TasksContract.buildTasksUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                // no action required
                break;
            default:
                break;
        }

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: starts");
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                // no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                // If we're editing, remove the fragment. Otherwise, close the app
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.task_detail_container);
                if (fragment != null) {
                    // we were editing
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                    if (mTwoPane) {
                        // in Landscape, so quit only if the back button was used
                        if (dialogId == DIALOG_ID_CANCEL_EDIT) {
                            finish();
                        }
                    } else {
                        // hide the edit container in single pane mode
                        // and make sure the left-hand container is visible
                        View addEditLayout = findViewById(R.id.task_detail_container);
                        View mainFragment = findViewById(R.id.fragment);
                        // We're just removed the editing fragment, so hide the frame
                        addEditLayout.setVisibility(View.GONE);

                        // and make sure the MainActivityFragment is visible
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                } else {
                    // not editing, so quit regardless of orientation
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: starts");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_detail_container);
        if (fragment == null || fragment.canClose()) {
            super.onBackPressed();
        } else {
            // show dialogue to get confirmation to quit editing
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.d(TAG, "onAttachFragment: called, fragment is " + fragment.toString());
        super.onAttachFragment(fragment);
    }

    private void showConfirmationDialog(int dialogId) {
        // show dialogue to get confirmation to quit editing
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        // Required to satisfy the interface
    }
}