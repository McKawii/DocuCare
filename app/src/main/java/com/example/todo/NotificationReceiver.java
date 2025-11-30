package com.example.todo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.model.Task;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = (Task) intent.getSerializableExtra("task");

        if (task == null) {
            Log.e(TAG, "Task is null in notification receiver");
            return;
        }

        Log.d(TAG, "Received notification for task: " + task.getTitle() + ", ID: " + task.getId());

        createNotificationChannel(context);

        Intent notificationIntent = new Intent(context, AddEditTaskActivity.class);
        notificationIntent.putExtra("editMode", true);
        notificationIntent.putExtra("task", task);
        notificationIntent.putExtra("notification", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, task.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_channel")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Task Reminder")
                .setContentText("Task \"" + task.getTitle() + "\" is due soon")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Notification permission not granted for task: " + task.getTitle());

                Intent permissionIntent = new Intent(context, MainActivity.class);
                permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                permissionIntent.putExtra("request_notification_permission", true);
                context.startActivity(permissionIntent);
                return;
            }
        }

        try {
            notificationManager.notify(task.getId(), builder.build());
            Log.d(TAG, "Notification shown successfully for task: " + task.getTitle());
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException when showing notification: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Channel";
            String description = "Channel for task notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("task_channel", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 100, 200});
            channel.enableLights(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }
}