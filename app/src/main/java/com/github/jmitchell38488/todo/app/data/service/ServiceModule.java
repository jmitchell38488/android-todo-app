package com.github.jmitchell38488.todo.app.data.service;

import android.content.Context;

import com.github.jmitchell38488.todo.app.annotation.PerApp;

import javax.inject.Inject;
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

}
