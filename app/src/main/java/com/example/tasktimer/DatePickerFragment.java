package com.example.tasktimer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";

    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    int mDialogId = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use current date initially
        final GregorianCalendar cal = new GregorianCalendar();
        String title = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mDialogId = arguments.getInt(DATE_PICKER_ID);
            title = arguments.getString(DATE_PICKER_TITLE);

            // If a date is passed, use it. Otherwise use the current date
            Date givenDate = (Date) arguments.getSerializable(DATE_PICKER_DATE);
            if (givenDate != null) {
                cal.setTime(givenDate);
                Log.d(TAG, "onCreateDialog: retrieved date = " + givenDate.toString());
            }
        }

        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_MONTH);

        @SuppressWarnings("ConstantConditions")
        DatePickerDialog dpd = new DatePickerDialog(getContext(), this, year, month, day);
        if (title != null) {
            dpd.setTitle(title);
        }

        return dpd;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof DatePickerDialog.OnDateSetListener)) {
            throw new ClassCastException(context.toString() + " must implement DatePickerDialog.OnDateSetListener interface.");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Log.d(TAG, "onDateSet: entering..");
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        if (listener != null) {
            // Notify caller of the user-selected values
            datePicker.setTag(mDialogId); // pass the id back in the tag, to save the caller from storing their own copy
            listener.onDateSet(datePicker, year, month, day);
        }

        Log.d(TAG, "onDateSet: exiting...");
    }
}

