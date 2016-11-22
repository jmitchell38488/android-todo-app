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
        long interval = 24 * 60 * 60 * 1000;
        long startTime = getStartTime();

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, mPendingItent);
        Log.d(LOG_TAG, String.format("Starting Pending Intent (start: %d, interval: %d)",
                startTime, interval));
    }

    private long getStartTime() {
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // if 9am less than now, set the alarm to start tomorrow instead of today
        if (calendar.getTimeInMillis() < currentTimeMillis) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        }

        return calendar.getTimeInMillis();
    }

}
