package com.example.tasktimer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;

    public CursorRecyclerViewAdapter(Cursor cursor) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        mCursor = cursor;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new ViewHolder requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");

        if (mCursor == null || mCursor.getCount() == 0) {
            Log.d(TAG, "onBindViewHolder: No tasks yet, providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions_detail);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move to position " + position);
            }
            holder.name.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)));
            holder.description.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)));
            holder.editButton.setVisibility(View.VISIBLE); // TODO add onClick listener
            holder.deleteButton.setVisibility(View.VISIBLE); // TODO add onClick listener
        }

    }

    @Override
    public int getItemCount() {
        if (mCursor == null || mCursor.getCount() == 0) {
            return 1; // to populate single view holder for the instructions when there is no task.
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     *
     * @param newCursor The new Cursor to be used
     * @return Returns the previous Cursor, or null if there wasn't one.
     * If the new Cursor is the same as the old one, return null as well.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: created");

            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit);
            this.deleteButton = itemView.findViewById(R.id.tli_delete);
        }
    }

}
