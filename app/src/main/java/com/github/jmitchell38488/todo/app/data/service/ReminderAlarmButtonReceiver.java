package com.github.jmitchell38488.todo.app.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.ui.activity.TriggeredAlarmActivity;
import com.github.jmitchell38488.todo.app.util.AlarmUtility;

import javax.inject.Inject;

public class ReminderAlarmButtonReceiver extends BroadcastReceiver {

    private final String LOG_TAG = ReminderAlarmButtonReceiver.class.getSimpleName();

    @Inject TodoReminderRepository mTodoReminderRepository;
    @Inject ReminderAlarm mReminderAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Received intent: " + intent.getAction());

        TodoApp.getComponent(context).inject(this);

        Bundle args = intent.getExtras();
        args.putBoolean(Parcelable.KEY_DISMISS_ALARM, false);

        // The activity is running; the flag is set in the activity in onStart & onStop
        if (TriggeredAlarmActivity.isRunning) {
            if (intent.getAction().compareTo(com.github.jmitchell38488.todo.app.data.Intent.ACTION_DISMISS_ALARM) == 0) {
                args.putBoolean(Parcelable.KEY_DISMISS_ALARM, true);
            }

            Intent local = new Intent();
            local.putExtras(args);
            local.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_STOP_ALARM);
            context.sendBroadcast(local);
            Log.d(LOG_TAG, "Broadcasting intent: " + com.github.jmitchell38488.todo.app.data.Intent.ACTION_STOP_ALARM);

            return;
        } else if (intent.getAction().compareTo(com.github.jmitchell38488.todo.app.data.Intent.ACTION_DISMISS_ALARM) == 0) {
            // Activity is not running
            args.putBoolean(Parcelable.KEY_DISMISS_ALARM, true);
            AlarmUtility.handleAlarmSnoozeDismiss(context, args, mTodoReminderRepository, mReminderAlarm);
        }
    }

}
