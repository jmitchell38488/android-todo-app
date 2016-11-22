package com.github.jmitchell38488.todo.app.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;

import javax.inject.Inject;

public class TriggeredAlarmActivity extends AppCompatActivity {

    @Inject
    ReminderAlarm mReminderAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TodoApp.getComponent(getApplication()).inject(this);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_triggered_alarm);

        Button dismiss = (Button) findViewById(R.id.button_dismiss);
        Button snooze = (Button) findViewById(R.id.button_snooze);

        Bundle args = getIntent().getExtras();
        final TodoItem item = args.getParcelable(Parcelable.KEY_TODOITEM);
        final int alarmId = args.getInt(Parcelable.KEY_ALARM_ID);

        dismiss.setOnClickListener(view -> {
            mReminderAlarm.cancelAlarm(item, alarmId);
            TriggeredAlarmActivity.this.finish();
        });

        snooze.setOnClickListener(view -> {
            mReminderAlarm.snoozeAlarm(item, alarmId);
            TriggeredAlarmActivity.this.finish();
        });
    }
}
