package com.github.jmitchell38488.todo.app.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.fragment.TriggeredAlarmFragment;
import com.github.jmitchell38488.todo.app.util.DateUtility;

public class TriggeredAlarmActivity extends AppCompatActivity {

    protected Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_triggered_alarm);

        mFragment = new TriggeredAlarmFragment();
        Bundle args = getIntent().getExtras();
        mFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_triggered_alarm_container, mFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        ((TriggeredAlarmFragment) mFragment).handleOnBackPressed();
        super.onBackPressed();
    }

}
