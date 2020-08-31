package com.example.tasktimer;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationsReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener, AppDialog.DialogEvents, View.OnClickListener {

    private static final String TAG = "DurationsReport";

    private static final int LOADER_ID = 1;
    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    private static final String DELETION_DATE = "DELETION_DATE";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    // module level arguments - so when we change sort order, for example, the selection
    // is retained (and vice-versa).
    private Bundle mArgs = new Bundle();
    private boolean mDisplayWeek = true;

    private DurationsRVAdapter mAdapter;

    private final GregorianCalendar mCalendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durations_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE, 0);
            // it'll be zero when the activity's first created, so don't set the value
            if (timeInMillis != 0) {
                mCalendar.setTimeInMillis(timeInMillis);
                // make sure this part is cleared because we filter the database by seconds
                mCalendar.clear(GregorianCalendar.HOUR_OF_DAY);
                mCalendar.clear(GregorianCalendar.MINUTE);
                mCalendar.clear(GregorianCalendar.SECOND);
            }
            mDisplayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true);

        }

        applyFilter();

        // Set the listener for the buttons so we can sort the report
        TextView taskName = findViewById(R.id.td_name_heading);
        taskName.setOnClickListener(this);

        TextView taskDescription = findViewById(R.id.td_description_heading);
        if (taskDescription != null) {
            taskDescription.setOnClickListener(this);
        }

        TextView taskDate = findViewById(R.id.td_start_heading);
        taskDate.setOnClickListener(this);

        TextView taskDuration = findViewById(R.id.td_duration_heading);
        taskDuration.setOnClickListener(this);

        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create an empty adapter we will use, to display the loaded data.
        if (mAdapter == null) {
            mAdapter = new DurationsRVAdapter(this, null);
        }
        recyclerView.setAdapter(mAdapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, mArgs, this);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_DATE, mCalendar.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK, mDisplayWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.rm_filter_period:
                mDisplayWeek = !mDisplayWeek; // toggle between showing a week or a day
                applyFilter();
                invalidateOptionsMenu(); // forced call to onPrepareOptionsMenu to redraw our changed menu
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                return true;
            case R.id.rm_filter_date:
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER); // The actual filtering is done in onDateSet();
                return true;
            case R.id.rm_delete:
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE); // The actual deletion is done in onDateSet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.rm_filter_period);
        if (item != null) {
            // switch the icon and title to represent 7 days or 1 day for filtering the report
            if (mDisplayWeek) {
                item.setIcon(R.drawable.ic_baseline_filter_1_24);
                item.setTitle(R.string.rm_title_filter_day);
            } else {
                item.setIcon(R.drawable.ic_baseline_filter_7_24);
                item.setTitle(R.string.rm_title_filter_week);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: called");

        switch (view.getId()) {
            case R.id.td_name_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_NAME);
                break;
            case R.id.td_description_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_DESCRIPTION);
                break;
            case R.id.td_start_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_START_DATE);
                break;
            case R.id.td_duration_heading:
                mArgs.putString(SORT_ORDER_PARAM, DurationsContract.Columns.DURATIONS_DURATION);
                break;
        }
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
    }

    private void showDatePickerDialog(String title, int dialogId) {
        Log.d(TAG, "showDatePickerDialog: entering..");
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, mCalendar.getTime());

        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        Log.d(TAG, "showDatePickerDialog: exiting.");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.d(TAG, "onDateSet: called");

        // Check the id so we know what to do with the result
        int dialogId = (int) datePicker.getTag();
        mCalendar.set(year, month, day, 0, 0, 0);
        switch (dialogId) {
            case DIALOG_FILTER:
                applyFilter();
                LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
                break;
            case DIALOG_DELETE:
                String fromDate = DateFormat.getDateFormat(this).format(mCalendar.getTimeInMillis());
                AppDialog dialog = new AppDialog();
                Bundle args = new Bundle();
                args.putInt(AppDialog.DIALOG_ID, 1); // we only have one dialog in this activity
                args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.delete_timings_message, fromDate));
                args.putLong(DELETION_DATE, mCalendar.getTimeInMillis());
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), null);
                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving DatePickerDialog result");
        }
    }

    private void deleteRecords(long timeInMillis) {
        Log.d(TAG, "deleteRecords: entering...");

        long longDate = timeInMillis / 1000; // we need time in seconds
        String[] selectionArgs = new String[]{Long.toString(longDate)};
        String selection = TimingsContract.Columns.TIMING_START_TIME + " < ?";

        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingsContract.CONTENT_URI, selection, selectionArgs);
        applyFilter();
        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);

        Log.d(TAG, "deleteRecords: exiting..");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        long deletionDate = args.getLong(DELETION_DATE);
        // clear all timing records prior to the date selected
        deleteRecords(deletionDate);
        // re-query in case we've deleted the records that are currently being shown
//        LoaderManager.getInstance(this).restartLoader(LOADER_ID, mArgs, this);
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    private void applyFilter() {
        Log.d(TAG, "applyFilter: entering");
        if (mDisplayWeek) {
            // show records for entire week
            Date currentCalendarDate = mCalendar.getTime();

            // we have a date so find out which day of week it is
            int dayOfWeek = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = mCalendar.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: first day of calendar week is " + weekStart);
            Log.d(TAG, "applyFilter: day of week is " + dayOfWeek);
            Log.d(TAG, "applyFilter: current date is " + currentCalendarDate);

            // calculate week start and end dates using mCalendar variable - this will be put back to original after the calculations
            mCalendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart);
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            // calculate calendar week end date
            mCalendar.add(GregorianCalendar.DATE, 6);
            String endDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));

            // put the start and end dates to selection arguments
            String[] selectionArgs = new String[]{startDate, endDate};

            // put back the mCalendar to original value
            mCalendar.setTime(currentCalendarDate);

            Log.d(TAG, "applyFilter: (7) Start date is " + startDate + ", End date is " + endDate);
            mArgs.putString(SELECTION_PARAM, "StartDate BETWEEN ? AND ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);

        } else {
            // re-query for current day
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate};
            Log.d(TAG, "applyFilter: (1) Start date is " + startDate);
            mArgs.putString(SELECTION_PARAM, "StartDate = ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                String[] projection = {BaseColumns._ID,
                        DurationsContract.Columns.DURATIONS_NAME,
                        DurationsContract.Columns.DURATIONS_DESCRIPTION,
                        DurationsContract.Columns.DURATIONS_START_TIME,
                        DurationsContract.Columns.DURATIONS_START_DATE,
                        DurationsContract.Columns.DURATIONS_DURATION};

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                return new CursorLoader(this,
                        DurationsContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }
}