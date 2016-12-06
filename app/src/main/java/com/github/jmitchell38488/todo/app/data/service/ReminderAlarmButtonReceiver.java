package com.github.jmitchell38488.todo.app.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReminderAlarmButtonReceiver extends BroadcastReceiver {

    private final String LOG_TAG = ReminderAlarmButtonReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Received intent: " + intent.getAction());

        Intent local = new Intent();
        intent.putExtras(intent.getExtras());
        local.setAction(com.github.jmitchell38488.todo.app.data.Intent.ACTION_STOP_ALARM);
        context.sendBroadcast(local);

        Log.d(LOG_TAG, "Broadcasting intent: " + com.github.jmitchell38488.todo.app.data.Intent.ACTION_STOP_ALARM);
    }

}
