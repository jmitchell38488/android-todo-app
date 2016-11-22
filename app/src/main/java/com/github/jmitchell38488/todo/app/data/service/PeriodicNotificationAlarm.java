package com.github.jmitchell38488.todo.app.data.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class PeriodicNotificationAlarm {

    private final static String LOG_TAG = PeriodicNotificationAlarm.class.getSimpleName();

    private Context mContext;
    private Intent mIntent;
    private PendingIntent mPendingItent;
    private AlarmManager mAlarmManager;

    public PeriodicNotificationAlarm(Context context) {
        mContext = context;
    }

    public void createPendingAlarm() {
        Log.d(LOG_TAG, "Creating periodic notification intent structure");
        mIntent = new Intent(mContext, PeriodicNotificationService.class);
        mPendingItent = PendingIntent.getService(mContext, 0, mIntent, 0);
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void cancel() {
        if (mIntent == null) {
            createPendingAlarm();
        }

        mAlarmManager.cancel(mPendingItent);
        Log.d(LOG_TAG, "Cancelled Pending Intent");
    }

    public void start() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + (1000 * 60));
        /*calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 8);*/

        long startTime = calendar.getTimeInMillis();
        long interval = 1000 * 60;

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, mPendingItent);
        Log.d(LOG_TAG, String.format("Starting Pending Intent (start: %d, interval: %d)",
                startTime, interval));
    }

}
