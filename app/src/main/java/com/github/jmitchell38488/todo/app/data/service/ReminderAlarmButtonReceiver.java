package com.github.jmitchell38488.todo.app.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.util.DateUtility;
import com.github.jmitchell38488.todo.app.util.NotificationUtility;
import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import javax.inject.Inject;

public class ReminderAlarmButtonReceiver extends BroadcastReceiver {

    @Inject TodoReminderRepository mTodoReminderRepository;
    @Inject ReminderAlarm mReminderAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        TodoItem item = args.getParcelable(Parcelable.KEY_TODOITEM);
        TodoReminder reminder = args.getParcelable(Parcelable.KEY_TODOREMINDER);
        int alarmId = args.getInt(Parcelable.KEY_ALARM_ID);
        boolean dismiss = args.getBoolean(Parcelable.KEY_DISMISS_ALARM);

        // Cancel the current notification, we don't need to display it since it was dismissed/snoozed
        NotificationUtility.cancelCurrentAlarmNotification(context, alarmId);

        int times = reminder.getTimesSnoozed();
        if (times >= PreferencesUtility.getMaxAlarmSnoozeTimes(context)) {
            dismiss = true;
        }

        // If snoozed
        if (!dismiss) {
            reminder.setTimesSnoozed(times + 1);
            mTodoReminderRepository.saveTodoReminder(reminder);
            mReminderAlarm.snoozeAlarm(item, alarmId);

            int snoozeMinutes = PreferencesUtility.getAlarmSnoozeTime(context);
            long snoozeTime = snoozeMinutes * DateUtility.TIME_1_MINUTE;
            long startTime = System.currentTimeMillis() + snoozeTime;

            NotificationUtility.createSnoozedAlarmNotification(context, item, reminder, alarmId, startTime);

        // If dismissed
        } else {
            mReminderAlarm.cancelAlarm(item, alarmId);
            mTodoReminderRepository.deleteTodoReminder(reminder);
        }
    }

}
