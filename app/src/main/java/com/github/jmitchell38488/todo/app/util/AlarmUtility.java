package com.github.jmitchell38488.todo.app.util;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemEditHolder;

import java.util.HashMap;
import java.util.Map;

public class AlarmUtility {

    private static final String LOG_TAG = AlarmUtility.class.getSimpleName();
    private static final Handler handler = new Handler();

    public static Map<Uri, String> getAlarmSounds(Context context) {
        Map<Uri, String> map = new HashMap<>();

        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();

        while (cursor.moveToNext()) {
            String name = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String uriIndex = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            Uri uri = Uri.parse(uriIndex + "/" + id);

            map.put(uri, name);
        }

        return map;
    }

    public static String getAlarmSoundTitle(Context context, Uri sound) {
        Ringtone ringtone = RingtoneManager.getRingtone(context, sound);
        String title = ringtone.getTitle(context);

        return title;
    }

    public static void handleAlarmSnoozeDismiss(Context context, Bundle bundle,
                                                TodoReminderRepository todoReminderRepository,
                                                ReminderAlarm reminderAlarm) {
        Log.d(LOG_TAG, "Inside handleAlarmSnoozeDismiss(c, b, t, r)");

        TodoItem item = bundle.getParcelable(Parcelable.KEY_TODOITEM);
        TodoReminder reminder = bundle.getParcelable(Parcelable.KEY_TODOREMINDER);
        int alarmId = bundle.getInt(Parcelable.KEY_ALARM_ID);
        boolean dismiss = bundle.getBoolean(Parcelable.KEY_DISMISS_ALARM);

        // Cancel any existing current alarm notifications
        NotificationUtility.cancelCurrentAlarmNotification(context.getApplicationContext(), alarmId);

        int times = reminder.getTimesSnoozed();
        if (!dismiss && times >= PreferencesUtility.getMaxAlarmSnoozeTimes(context.getApplicationContext())) {
            dismiss = true;
        }

        // If snoozed
        if (!dismiss) {
            Log.d(LOG_TAG, "Snoozing alarm");
            reminder.setTimesSnoozed(times + 1);
            todoReminderRepository.saveTodoReminder(reminder);
            reminderAlarm.snoozeAlarm(item, alarmId);

            int snoozeMinutes = PreferencesUtility.getAlarmSnoozeTime(context.getApplicationContext());
            long snoozeTime = snoozeMinutes * DateUtility.TIME_1_MINUTE;
            long startTime = System.currentTimeMillis() + snoozeTime;

            // Delay the creation
            handler.postDelayed(() -> {
                    NotificationUtility.createSnoozedAlarmNotification(context.getApplicationContext(),
                            item, reminder, alarmId, startTime);
            }, 1500);
        // If dismissed
        } else {
            Log.d(LOG_TAG, "Dismissing alarm");
            reminderAlarm.cancelAlarm(item, alarmId);
            todoReminderRepository.deleteTodoReminder(reminder);
            NotificationUtility.cancelSnoozedAlarmNotification(context.getApplicationContext(), alarmId);
        }

    }
}
