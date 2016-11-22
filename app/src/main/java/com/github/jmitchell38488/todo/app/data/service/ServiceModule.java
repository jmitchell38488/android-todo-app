package com.github.jmitchell38488.todo.app.data.service;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    Context mContext;

    public ServiceModule(Context context) {
        mContext = context;
    }

    @Provides @Singleton
    public PeriodicNotificationService providePeriodicNotificationService() {
        return new PeriodicNotificationService();
    }

    @Provides @Singleton
    public PeriodicNotificationAlarm providePeriodicNotificationAlarm() {
        return new PeriodicNotificationAlarm(mContext);
    }

    @Provides @Singleton
    public ReminderAlarmService provideReminderAlarmService() {
        return new ReminderAlarmService();
    }

    @Provides @Singleton
    public ReminderAlarm provideReminderAlarm() {
        return new ReminderAlarm(mContext);
    }

}
