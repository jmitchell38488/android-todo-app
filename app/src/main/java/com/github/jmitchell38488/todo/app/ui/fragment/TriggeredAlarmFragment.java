package com.github.jmitchell38488.todo.app.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;
import com.github.jmitchell38488.todo.app.util.DateUtility;
import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import javax.inject.Inject;

import butterknife.BindView;

public class TriggeredAlarmFragment extends BaseFragment {

    private static final String LOG_TAG = TriggeredAlarmFragment.class.getSimpleName();

    @Inject ReminderAlarm mReminderAlarm;
    @Inject TodoReminderRepository mTodoReminderRepository;

    @BindView(R.id.alarm_title) TextView titleView;
    @BindView(R.id.alarm_time) TextView timeView;
    @BindView(R.id.alarm_date) TextView dateView;
    @BindView(R.id.alarm_button_snooze) Button snoozeButton;
    @BindView(R.id.alarm_button_dismiss) Button dismissButton;

    protected TodoItem mTodoItem;
    protected TodoReminder mTodoReminder;
    protected int mAlarmId;
    protected BroadcastReceiver mBroadcastReceiver;
    protected Handler mRunnableHandler = new Handler();

    @Override
    public void onStart() {
        super.onStart();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    timeView.setText(DateUtility.getTimeForAlarm(getActivity()));
                    dateView.setText(DateUtility.getFormattedDateForAlarm(getActivity()));
                }
            }
        };

        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        // Automatically snooze the alarm if its running time exceeds more than 1 minute
        mRunnableHandler.postDelayed(() -> {
            int times = mTodoReminder.getTimesSnoozed();
            if (times >= PreferencesUtility.getMaxAlarmSnoozeTimes(getActivity())) {
                mReminderAlarm.cancelAlarm(mTodoItem, mAlarmId);
                mTodoReminderRepository.deleteTodoReminder(mTodoReminder);
            } else {
                mTodoReminder.setTimesSnoozed(times + 1);
                mTodoReminderRepository.saveTodoReminder(mTodoReminder);
                mReminderAlarm.snoozeAlarm(mTodoItem, mAlarmId);
            }

            getActivity().finish();
        }, PreferencesUtility.getMaxAllowedTimeBeforeAutoSnooze(getActivity()) * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TodoApp.getComponent(getActivity()).inject(this);
        setHasOptionsMenu(false);

        Bundle args = getArguments();
        mTodoItem = args.getParcelable(Parcelable.KEY_TODOITEM);
        mAlarmId = args.getInt(Parcelable.KEY_ALARM_ID);
        mTodoReminder = mTodoReminderRepository.getReminderById(mAlarmId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_triggered_alarm, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        snoozeButton.setVisibility(View.GONE);

        titleView.setText(mTodoItem.getTitle());
        timeView.setText(DateUtility.getTimeForAlarm(getActivity()));
        dateView.setText(DateUtility.getFormattedDateForAlarm(getActivity()));

        dismissButton.setOnClickListener(v -> {
            mReminderAlarm.cancelAlarm(mTodoItem, mAlarmId);
            mTodoReminderRepository.deleteTodoReminder(mTodoReminder);
            getActivity().finish();
        });

        // Snooze should only be available if we haven't hit the limit yet
        if (mTodoReminder.getTimesSnoozed() < PreferencesUtility.getMaxAlarmSnoozeTimes(getActivity())) {
            snoozeButton.setVisibility(View.VISIBLE);
            snoozeButton.setOnClickListener(v -> {
                // Make sure that we increment the no. of times snoozed
                mTodoReminder.setTimesSnoozed(mTodoReminder.getTimesSnoozed() + 1);
                mTodoReminderRepository.saveTodoReminder(mTodoReminder);

                mReminderAlarm.snoozeAlarm(mTodoItem, mAlarmId);
                getActivity().finish();
            });
        }
    }

    public void handleOnBackPressed() {
        int times = mTodoReminder.getTimesSnoozed();

        // We don't want to snooze the alarm if it's hit the snooze limit
        if (times >= PreferencesUtility.getMaxAlarmSnoozeTimes(getActivity())) {
            mReminderAlarm.cancelAlarm(mTodoItem, mAlarmId);
            mTodoReminderRepository.deleteTodoReminder(mTodoReminder);
        } else {
            mTodoReminder.setTimesSnoozed(times + 1);
            mTodoReminderRepository.saveTodoReminder(mTodoReminder);
            mReminderAlarm.snoozeAlarm(mTodoItem, mAlarmId);
        }

        getActivity().finish();
    }

}
