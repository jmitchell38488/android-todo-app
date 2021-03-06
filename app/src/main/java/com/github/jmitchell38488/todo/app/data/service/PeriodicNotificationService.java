package com.github.jmitchell38488.todo.app.data.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;

import javax.inject.Inject;

public class PeriodicNotificationService extends IntentService {

    private static final int NOTIFICATION_SERVICE_ID = 10001;
    private static final String NOTIFICATION_TAG =
            PeriodicNotificationService.class.getSimpleName();

    private final static String LOG_TAG = PeriodicNotificationService.class.getSimpleName();
    private final static String THREAD_NAME = PeriodicNotificationService.class.getSimpleName();

    @Inject
    TodoItemRepository mTodoItemRepository;

    public PeriodicNotificationService() {
        this(THREAD_NAME);
    }

    public PeriodicNotificationService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TodoApp.getComponent(getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int pendingCount = mTodoItemRepository.getItemCount(Filter.DEFAULT);

        if (pendingCount < 0) {
            Log.d(LOG_TAG, "There are no pending to do items, will not notify the user");
            return;
        }

        int pendingImportantCount = mTodoItemRepository.getItemCount(Filter.PINNED);
        String content = "";

        if (pendingImportantCount < 1) {
            if (pendingCount == 1) {
                content = getString(R.string.notification_periodic_1);
            } else {
                content = getString(R.string.notification_periodic_2, pendingCount);
            }
        } else {
            if (pendingCount == 1) {
                content = getString(R.string.notification_periodic_i1);
            } else {
                content = getString(R.string.notification_periodic_i2, pendingCount, pendingImportantCount);
            }
        }

        Intent resultIntent = new Intent(getApplicationContext(), ListActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

        int iconId = R.mipmap.ic_launcher;
        String title = getString(R.string.notification_periodic_title);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                content + "\n\n" + getString(R.string.notification_periodic_extra)))
                        .setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL |
                Notification.FLAG_ONLY_ALERT_ONCE;

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_SERVICE_ID, notification);

        Log.d(LOG_TAG, String.format("Triggering notification (%s)", title));
    }

}
