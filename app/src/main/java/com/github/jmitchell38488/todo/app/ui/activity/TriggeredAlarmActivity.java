package com.github.jmitchell38488.todo.app.ui.activity;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.fragment.TriggeredAlarmFragment;

public class TriggeredAlarmActivity extends AppCompatActivity {

    protected Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
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
        ((TriggeredAlarmFragment) mFragment).handleApplicationStop();
        super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
