package com.github.jmitchell38488.todo.app.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import javax.inject.Inject;

public class AutoStartReceiver extends BroadcastReceiver {

    @Inject
    PeriodicNotificationAlarm mNotificationAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            mNotificationAlarm.cancel();

            if (PreferencesUtility.userEnabledPeriodicNotifications()) {
                mNotificationAlarm.start();
            }
        }
    }

}
