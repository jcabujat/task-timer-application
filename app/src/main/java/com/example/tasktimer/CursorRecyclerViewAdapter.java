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
    private OnTaskClickListener mListener;

    interface OnTaskClickListener {
        void onEditClick(@NonNull Task task);

        void onDeleteClick(@NonNull Task task);

        void onTaskLongClick(@NonNull Task task);
    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        mCursor = cursor;
        mListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

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

            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER)));

            holder.name.setText(task.getName());
            holder.description.setText(task.getDescription());
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.tli_edit:
                            if (mListener != null) {
                                mListener.onEditClick(task);
                            }
                            break;
                        case R.id.tli_delete:
                            if (mListener != null) {
                                mListener.onDeleteClick(task);
                            }
                            break;
                        default:
                            Log.d(TAG, "onClick: found unexpected button id " + view.getId());
                    }
                }
            };

            View.OnLongClickListener buttonLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mListener != null) {
                        mListener.onTaskLongClick(task);
                        return true;
                    }
                    return false;
                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
            holder.itemView.setOnLongClickListener(buttonLongClickListener);
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
        int numItems = getItemCount(); // store old item count
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of data set
            notifyItemRangeRemoved(0, numItems);
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
//        private static final String TAG = "TaskViewHolder";

        TextView name;
        TextView description;
        ImageButton editButton;
        ImageButton deleteButton;
        View itemView;

        public TaskViewHolder(View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit);
            this.deleteButton = itemView.findViewById(R.id.tli_delete);
            this.itemView = itemView;
        }
    }
}
