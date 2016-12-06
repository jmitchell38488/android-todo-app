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
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        mNotificationManager.cancel(notificationId);
    }

    public static void createPendingAlarmNotification(Context context, TodoItem item, TodoReminder reminder, int notificationId) {

    }

    public static void createSnoozedAlarmNotification(Context context, TodoItem item,
                                  TodoReminder reminder, int notificationId, long timeInMillis) {

        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format_alarm_notification_12));

        String date = format.format(timeInMillis > 0 ? timeInMillis : DateUtility.getTimeInMillisFromTodoReminder(reminder));
        String title = context.getString(R.string.notification_current_title);
        String description = context.getString(R.string.notification_current_description, date);

        int iconId = R.mipmap.icon_alarm;
        int iconIdDismiss = R.mipmap.icon_alarm_dismiss;

        Bundle argsDismiss = new Bundle();
        argsDismiss.putParcelable(Parcelable.KEY_TODOITEM, item);
        argsDismiss.putParcelable(Parcelable.KEY_TODOREMINDER, reminder);
        argsDismiss.putInt(Parcelable.KEY_ALARM_ID, notificationId);
        argsDismiss.putBoolean(Parcelable.KEY_DISMISS_ALARM, true);

        Intent dismissIntent = new Intent(context, ReminderAlarmButtonReceiver.class);
        dismissIntent.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_DISMISS_ALARM);
        dismissIntent.putExtras(argsDismiss);

        PendingIntent dismissPending = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(dismissPending)
                        .addAction(iconIdDismiss, context.getString(R.string.alarm_button_dismiss), dismissPending)
                        .setOngoing(true);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_ONLY_ALERT_ONCE;

        mNotificationManager.notify(NOTIFICATION_TAG, notificationId, notification);
    }

    public static void createCurrentAlarmNotification(Context context, TodoItem item, TodoReminder reminder, int notificationId) {
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format_alarm_notification_12));

        String date = format.format(DateUtility.getTimeInMillisFromTodoReminder(reminder));
        String title = context.getString(R.string.notification_current_title);
        String description = context.getString(R.string.notification_current_description, date);

        int iconId = R.mipmap.icon_alarm;
        int iconIdSnooze = R.mipmap.icon_alarm_snooze;
        int iconIdDismiss = R.mipmap.icon_alarm_dismiss;

        Bundle argsSnooze = new Bundle();
        argsSnooze.putParcelable(Parcelable.KEY_TODOITEM, item);
        argsSnooze.putParcelable(Parcelable.KEY_TODOREMINDER, reminder);
        argsSnooze.putInt(Parcelable.KEY_ALARM_ID, notificationId);
        argsSnooze.putBoolean(Parcelable.KEY_DISMISS_ALARM, false);

        Bundle argsDismiss = (Bundle) argsSnooze.clone();
        argsDismiss.putBoolean(Parcelable.KEY_DISMISS_ALARM, true);

        Intent snoozeIntent = new Intent(context, ReminderAlarmButtonReceiver.class);
        snoozeIntent.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_SNOOZE_ALARM);
        snoozeIntent.putExtras(argsSnooze);

        Intent dismissIntent = new Intent(context, ReminderAlarmButtonReceiver.class);
        dismissIntent.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_DISMISS_ALARM);
        dismissIntent.putExtras(argsDismiss);

        PendingIntent snoozePending = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent dismissPending = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(description)
                        .setContentIntent(snoozePending)
                        .addAction(iconIdSnooze, context.getString(R.string.alarm_button_snooze), snoozePending)
                        .addAction(iconIdDismiss, context.getString(R.string.alarm_button_dismiss), dismissPending)
                        .setOngoing(true);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_ONLY_ALERT_ONCE;

        mNotificationManager.notify(NOTIFICATION_TAG, notificationId, notification);
    }

}
