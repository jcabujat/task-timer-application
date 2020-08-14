package com.example.tasktimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode {EDIT, ADD}

    private FragmentEditMode mMode;

    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }


    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    public boolean canClose() {
        return false; // TODO will change implementation later
    }


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        // Activities containing this fragment must implement its callback
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName() +
                    " must implement AddEditActivityFragment.OnSaveClicked interface.");
        }
        mSaveListener = (OnSaveClicked) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameTextView = view.findViewById(R.id.addedit_name);
        mDescriptionTextView = view.findViewById(R.id.addedit_description);
        mSortOrderTextView = view.findViewById(R.id.addedit_sortorder);
        mSaveButton = view.findViewById(R.id.addedit_save);

//        Bundle arguments = getActivity().getIntent().getExtras(); // To be changed later
        Bundle arguments = getArguments();

        final Task task;
        if (arguments != null) {
            Log.d(TAG, "onCreateView: task details found, editing...");

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                // No task, meaning we need to add new task
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: No arguments passed, adding a new record");
            mMode = FragmentEditMode.ADD;
        }


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sortOrder; // get int value for sort order
                if (mSortOrderTextView.length() > 0) {
                    sortOrder = Integer.parseInt(mSortOrderTextView.getText().toString());
                } else {
                    sortOrder = 0;
                }


                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();


                switch (mMode) {
                    // Update database only when there are changes in at least one field
                    case EDIT:
                        if (task == null) {
                            break;
                        }
                        if (!mNameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }
                        if (!mDescriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASK_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if (sortOrder != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder);
                        }
                        if (values.size() != 0) {
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TasksContract.buildTasksUri(task.getId()), values, null, null);
                            Toast.makeText(getContext(), "Edit details saved successfully", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case ADD:
                        if (mNameTextView.length() > 0) {
                            Log.d(TAG, "onClick: adding new Task");
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASK_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASK_SORT_ORDER, sortOrder);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                            Toast.makeText(getContext(), "Task was added successfully", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done editing");

                if (mSaveListener != null) {
                    mSaveListener.onSaveClicked();
                }

//                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");

        return view;
    }


}