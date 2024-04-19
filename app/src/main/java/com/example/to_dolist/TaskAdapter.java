package com.example.to_dolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> taskList;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_task, parent, false);
        }

        // Get the task at the specified position
        Task task = taskList.get(position);

        // Set task details to views in list item layout
        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewDueDate = convertView.findViewById(R.id.textViewDueDate);

        textViewTitle.setText(task.getTitle());
        textViewDueDate.setText((CharSequence) task.getDueDate());

        return convertView;
    }
}
