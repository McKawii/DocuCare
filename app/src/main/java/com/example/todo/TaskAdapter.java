package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.model.Task;

import java.util.Date;
import java.util.List;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private TaskClickInterface taskClickInterface;

    public TaskAdapter(TaskClickInterface taskClickInterface) {
        super(DIFF_CALLBACK);
        this.taskClickInterface = taskClickInterface;
    }

    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.taskTitle.setText(currentTask.getTitle());
        holder.taskStatus.setChecked(currentTask.isCompleted());
        Date dueDate = currentTask.getDueTime();
        if (dueDate != null) {
            holder.taskDueDate.setText(dueDate.toString());
            holder.taskDueDate.setVisibility(View.VISIBLE);
            if(currentTask.isNotificationEnabled()){
                holder.taskAlarm.setVisibility(View.VISIBLE);
            } else {
                holder.taskAlarm.setVisibility(View.GONE);
            }
        } else {
            holder.taskDueDate.setVisibility(View.GONE);
            holder.taskAlarm.setVisibility(View.GONE);
        }


        List<String> attachments = currentTask.getAttachmentsPaths();
        if (attachments != null && !attachments.isEmpty()) {
            holder.attachmentIcon.setVisibility(View.VISIBLE);
        } else {
            holder.attachmentIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (taskClickInterface != null) {
                taskClickInterface.onTaskClick(currentTask);
            }
        });

        holder.taskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (taskClickInterface != null) {
                if(buttonView.isShown()){
                    taskClickInterface.onTaskCheckBoxClick(currentTask);
                }
            }
        });

    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskTitle;
        private final CheckBox taskStatus;
        private final ImageView attachmentIcon;
        private final TextView taskDueDate;
        private final ImageView taskAlarm;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskStatus = itemView.findViewById(R.id.checkboxCompleated);
            attachmentIcon = itemView.findViewById(R.id.attachmentIcon);
            taskDueDate = itemView.findViewById(R.id.endDate);
            taskAlarm = itemView.findViewById(R.id.alarmIcon);
        }
    }

}
