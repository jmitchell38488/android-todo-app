package com.github.jmitchell38488.todo.app.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;
import com.github.jmitchell38488.todo.app.ui.fragment.TriggeredAlarmFragment;
import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import javax.inject.Inject;

public class TriggeredAlarmActivity extends AppCompatActivity {

    private static final String LOG_TAG = TriggeredAlarmActivity.class.getSimpleName();

    @Inject TodoReminderRepository mTodoReminderRepository;
    @Inject ReminderAlarm mReminderAlarm;

    protected TriggeredAlarmFragment mFragment;
    protected TodoItem mTodoItem;
    protected TodoReminder mTodoReminder;

    protected int mAlarmId;
    protected BroadcastReceiver mBroadcastReceiver;
    protected Handler mRunnableHandler = new Handler();
    protected boolean stopped = false;

    protected MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Lifecycle: onResume");

        super.onCreate(savedInstanceState);
        TodoApp.getComponent(this).inject(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        setContentView(R.layout.activity_triggered_alarm);

        initializeWindow();

        Bundle args = getIntent().getExtras();

        mTodoItem = args.getParcelable(Parcelable.KEY_TODOITEM);
        mAlarmId = args.getInt(Parcelable.KEY_ALARM_ID);
        mTodoReminder = mTodoReminderRepository.getReminderById(mAlarmId);

        args.putParcelable(Parcelable.KEY_TODOREMINDER, mTodoReminder);

        mFragment = new TriggeredAlarmFragment();
        mFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_triggered_alarm_container, mFragment)
                .commit();

        mFragment.setSnoozeClickListener(v -> {
            stopped = false;
            finish();
        });

        mFragment.setDismissClickListener(v -> {
            mReminderAlarm.cancelAlarm(mTodoItem, mAlarmId);
            mTodoReminderRepository.deleteTodoReminder(mTodoReminder);

            // Make sure we flag this as stopped so that it isn't restarted
            stopped = true;
            finish();
        });

        Uri alarmTone = mTodoReminder.getSound() != null ?
                mTodoReminder.getSound() :
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        try {
            Log.d(LOG_TAG, "Setting media player URI: " + alarmTone.toString());
            //mMediaPlayer = MediaPlayer.create(this, );
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getApplicationContext(), alarmTone);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(100, 100);
            mMediaPlayer.prepare();
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Exception from media player: " + ex.getMessage());
        }
    }

    protected void initializeWindow() {
        // Set flags so we can wake the phone without requiring lock
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "Lifecycle: onStart");

        super.onStart();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    mFragment.updateTimeTick();
                }
            }
        };

        this.registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        // Automatically snooze the alarm if its running time exceeds more than 1 minute
        mRunnableHandler.postDelayed(() -> {
            stopped = false;
            finish();
        }, PreferencesUtility.getMaxAllowedTimeBeforeAutoSnooze(this) * 1000);
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "Lifecycle: onPause");

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "Lifecycle: onResume");

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            Log.d(LOG_TAG, "Playing alarm through Media Player");
            mMediaPlayer.start();
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "Lifecycle: onBackPressed");

        handleApplicationStop();
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG, "Lifecycle: onStop");

        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Lifecycle: onDestroy");

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

        mMediaPlayer = null;

        handleApplicationStop();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    /**
     * Helper method that handles resetting the alarm when the user snoozes it, hits the back/home
     * button, or the alarm times out. If the limit is reached, it will be automatically disabled.e
     */
    public void handleApplicationStop() {
        if (stopped) {
            return;
        }

        int times = mTodoReminder.getTimesSnoozed();

        // We don't want to snooze the alarm if it's hit the snooze limit
        if (times >= PreferencesUtility.getMaxAlarmSnoozeTimes(this)) {
            mReminderAlarm.cancelAlarm(mTodoItem, mAlarmId);
            mTodoReminderRepository.deleteTodoReminder(mTodoReminder);
        } else {
            mTodoReminder.setTimesSnoozed(times + 1);
            mTodoReminderRepository.saveTodoReminder(mTodoReminder);
            mReminderAlarm.snoozeAlarm(mTodoItem, mAlarmId);
        }
    }

}
