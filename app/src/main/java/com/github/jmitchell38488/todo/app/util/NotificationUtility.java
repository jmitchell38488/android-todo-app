package com.github.jmitchell38488.todo.app.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarmButtonReceiver;

import java.text.SimpleDateFormat;

public class NotificationUtility {

    public static final int REQUEST_CODE_SNOOZE = 9999991;
    public static final int REQUEST_CODE_DISMISS = 9999992;
    public static final int REQUEST_CODE_STOP = 9999993;

    private static final String LOG_TAG = NotificationUtility.class.getSimpleName();
    private static final String NOTIFICATION_TAG = NotificationUtility.class.getSimpleName();

    private static NotificationManager mNotificationManager;

    public static void cancelPendingAlarmNotification(Context context, int notificationId) {
        cancelAlarmNotification(context, notificationId);
    }

    public static void cancelSnoozedAlarmNotification(Context context, int notificationId) {
        cancelAlarmNotification(context, notificationId);
    }

    public static void cancelCurrentAlarmNotification(Context context, int notificationId) {
        cancelAlarmNotification(context, notificationId);
    }

    protected static void cancelAlarmNotification(Context context, int notificationId) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        mNotificationManager.cancel(NOTIFICATION_TAG, notificationId);
    }

    public static PendingIntent getSnoozeIntent(Context context, TodoItem item,
                                                TodoReminder reminder, int notificationId,
                                                int requestCode) {
        Bundle argsSnooze = new Bundle();
        argsSnooze.putParcelable(Parcelable.KEY_TODOITEM, item);
        argsSnooze.putParcelable(Parcelable.KEY_TODOREMINDER, reminder);
        argsSnooze.putInt(Parcelable.KEY_ALARM_ID, notificationId);
        argsSnooze.putBoolean(Parcelable.KEY_DISMISS_ALARM, false);

        Intent snoozeIntent = new Intent(context, ReminderAlarmButtonReceiver.class);
        snoozeIntent.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_SNOOZE_ALARM);
        snoozeIntent.putExtras(argsSnooze);

        PendingIntent snoozePending = PendingIntent.getBroadcast(context, requestCode, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);

        return snoozePending;
    }

    public static PendingIntent getDismissIntent(Context context, TodoItem item,
                                                 TodoReminder reminder, int notificationId,
                                                 int requestCode) {
        Bundle argsDismiss = new Bundle();
        argsDismiss.putParcelable(Parcelable.KEY_TODOITEM, item);
        argsDismiss.putParcelable(Parcelable.KEY_TODOREMINDER, reminder);
        argsDismiss.putInt(Parcelable.KEY_ALARM_ID, notificationId);
        argsDismiss.putBoolean(Parcelable.KEY_DISMISS_ALARM, true);

        Intent dismissIntent = new Intent(context, ReminderAlarmButtonReceiver.class);
        dismissIntent.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_DISMISS_ALARM);
        dismissIntent.putExtras(argsDismiss);
        PendingIntent dismissPending = PendingIntent.getBroadcast(context, requestCode, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

        return dismissPending;
    }

    public static void createPendingAlarmNotification(Context context, TodoItem item, TodoReminder reminder, int notificationId) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public static void createSnoozedAlarmNotification(Context context, TodoItem item,
                                  TodoReminder reminder, int notificationId, long timeInMillis) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format_alarm_notification_12));

        String date = format.format(timeInMillis > 0 ? timeInMillis : DateUtility.getTimeInMillisFromTodoReminder(reminder));

        // Set the notification title
        String title = context.getString(R.string.notification_current_title2);
        if (item.getTitle() != null && item.getTitle().length() > 0) {
            title = context.getString(R.string.notification_current_title, item.getTitle());
        }

        // Set the notification description
        String description = context.getString(R.string.notification_current_description2, date);
        if (item.getDescription() != null && item.getDescription().length() > 0) {
            description = context.getString(R.string.notification_current_description, item.getDescription(), date);
        }

        int iconId = R.mipmap.icon_alarm;
        int iconIdDismiss = R.mipmap.icon_alarm_dismiss;

        PendingIntent dismissPending = getDismissIntent(context, item, reminder, notificationId, REQUEST_CODE_DISMISS);
        PendingIntent dismissContent = getDismissIntent(context, item, reminder, notificationId, REQUEST_CODE_STOP);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(dismissContent)
                        .addAction(iconIdDismiss, context.getString(R.string.alarm_button_dismiss), dismissPending)
                        .setOngoing(true);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_ONLY_ALERT_ONCE;

        mNotificationManager.notify(NOTIFICATION_TAG, notificationId, notification);
    }

    public static void createCurrentAlarmNotification(Context context, TodoItem item, TodoReminder reminder, int notificationId) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format_alarm_notification_12));

        String date = format.format(DateUtility.getTimeInMillisFromTodoReminder(reminder));

        // Set the notification title
        String title = context.getString(R.string.notification_current_title2);
        if (item.getTitle() != null && item.getTitle().length() > 0) {
            title = context.getString(R.string.notification_current_title, item.getTitle());
        }

        // Set the notification description
        String description = context.getString(R.string.notification_current_description2, date);
        if (item.getDescription() != null && item.getDescription().length() > 0) {
            description = context.getString(R.string.notification_current_description, item.getDescription(), date);
        }

        int iconId = R.mipmap.icon_alarm;
        int iconIdSnooze = R.mipmap.icon_alarm_snooze;
        int iconIdDismiss = R.mipmap.icon_alarm_dismiss;

        PendingIntent snoozePending = getSnoozeIntent(context, item, reminder, notificationId, REQUEST_CODE_SNOOZE);
        PendingIntent dismissContent = getSnoozeIntent(context, item, reminder, notificationId, REQUEST_CODE_STOP);
        PendingIntent dismissPending = getDismissIntent(context, item, reminder, notificationId, REQUEST_CODE_DISMISS);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(dismissContent)
                        .addAction(iconIdSnooze, context.getString(R.string.alarm_button_snooze), snoozePending)
                        .addAction(iconIdDismiss, context.getString(R.string.alarm_button_dismiss), dismissPending)
                        .setOngoing(true);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_ONLY_ALERT_ONCE;

        mNotificationManager.notify(NOTIFICATION_TAG, notificationId, notification);
    }

}
