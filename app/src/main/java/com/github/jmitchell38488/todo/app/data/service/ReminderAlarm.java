package com.github.jmitchell38488.todo.app.data.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;

public class ReminderAlarm {

    private final static String LOG_TAG = ReminderAlarm.class.getSimpleName();

    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderAlarm(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    private int getRequestCode(long itemId, int alarmId) {
        return Integer.parseInt(String.format("%d09%d", itemId, alarmId));
    }

    public PendingIntent createAlarm(TodoItem item, int alarmId) {
        Log.d(LOG_TAG, String.format("Creating reminder alarm intent and pending intent " +
                "for todo item (%d) %s", item.getId(), item.getTitle()));

        int requestCode = getRequestCode(item.getId(), alarmId);

        Bundle arguments = new Bundle();
        arguments.putParcelable(Parcelable.KEY_TODOITEM, item);
        arguments.putInt(Parcelable.KEY_REQUEST_CODE, requestCode);
        arguments.putInt(Parcelable.KEY_ALARM_ID, alarmId);

        Intent intent = new Intent(mContext, ReminderAlarmService.class);
        intent.putExtras(arguments);

        PendingIntent pendingIntent = PendingIntent.getService(mContext, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);

        return pendingIntent;
    }

    public void cancelAlarm(TodoItem item, int alarmId) {
        PendingIntent pendingIntent = createAlarm(item, alarmId);
        mAlarmManager.cancel(pendingIntent);
        Log.d(LOG_TAG, String.format("Cancelled pending intent for todo item (%d) %s",
                item.getId(), item.getTitle()));
    }

    public void createAndStartAlarm(TodoItem item, int alarmId, long alarmTime) {
        if (alarmTime < System.currentTimeMillis()) {
            Log.d(LOG_TAG, String.format("Cancelled alarm for todo item (%d) %s, time %d is in the past",
                    item.getId(), item.getTitle(), alarmTime));

            return;
        }

        PendingIntent pendingIntent = createAlarm(item, alarmId);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }

    public void snoozeAlarm(TodoItem item, int alarmId) {
        long startTime = System.currentTimeMillis() + (60 * 1000);
        PendingIntent pendingIntent = createAlarm(item, alarmId);
        mAlarmManager.cancel(pendingIntent);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);

        Toast.makeText(mContext, "Alarm snoozed for 1 minute", Toast.LENGTH_SHORT).show();

        Log.d(LOG_TAG, String.format("Snoozed alarm for todo item (%d) %s for 1 minute",
                item.getId(), item.getTitle()));
    }

}
