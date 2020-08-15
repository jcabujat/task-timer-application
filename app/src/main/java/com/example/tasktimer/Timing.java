package com.example.tasktimer;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple timing object
 * Sets the start time when first created then calculate the duration when setDuration is called
 */

class Timing implements Serializable {
    private static final long serialVersionUID = 20200815L;
    private static final String TAG = "Timing";

    private long m_id;
    private Task mTask;
    private long mStartTime;
    private long mDuration;

    Timing(Task task) {
        mTask = task;
        // Initialize start time for new object and set duration to 0
        Date currentTime = new Date();
        mStartTime = currentTime.getTime() / 1000; // We only need seconds and not milliseconds
        mDuration = 0;
    }

    long getId() {
        return m_id;
    }

    void setId(long id) {
        this.m_id = id;
    }

    Task getTask() {
        return mTask;
    }

    void setTask(Task task) {
        mTask = task;
    }

    long getStartTime() {
        return mStartTime;
    }

    void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    long getDuration() {
        return mDuration;
    }

    void setDuration() {
        // Calculate the duration from start time to current time
        Date currentTime = new Date();
        mDuration = (currentTime.getTime() / 1000) - mStartTime; // we need time in seconds, not milliseconds
        Log.d(TAG, mTask.getName() + " - Start time: " + mStartTime + " |Duration: " + mDuration);
    }
}
