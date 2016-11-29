package com.github.jmitchell38488.todo.app.data.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;
import com.github.jmitchell38488.todo.app.ui.activity.TriggeredAlarmActivity;
import com.github.jmitchell38488.todo.app.util.NotificationUtility;

import javax.inject.Inject;

public class ReminderAlarmService extends IntentService {

    private final static String LOG_TAG = ReminderAlarmService.class.getSimpleName();
    private final static String THREAD_NAME = ReminderAlarmService.class.getSimpleName();

    @Inject
    TodoItemRepository mTodoItemRepository;

    public ReminderAlarmService() {
        this(THREAD_NAME);
    }

    public ReminderAlarmService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TodoApp.getComponent(getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Received alarm signal inside ReminderAlarmService");
        Bundle arguments = intent.getExtras();
        TodoItem item = arguments.getParcelable(Parcelable.KEY_TODOITEM);

        NotificationUtility.cancelAlarmNotification(getApplicationContext());
        NotificationUtility.createAlarmNotification(getApplicationContext(), item);

        Intent nIntent = new Intent(this, TriggeredAlarmActivity.class);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_TASK_ON_HOME |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        );
        nIntent.putExtras(arguments);
        startActivity(nIntent);
    }

}
