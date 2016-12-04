package com.github.jmitchell38488.todo.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;
import com.github.jmitchell38488.todo.app.ui.fragment.EditItemFragment;

import javax.inject.Inject;

public class EditItemActivity extends BaseActivity {

    private static final String LOG_TAG = EditItemActivity.class.getSimpleName();

    public static final int REQUEST_CODE_SOUND = 5;

    EditItemFragment mFragment;

    @Inject ReminderAlarm mReminderAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        TodoApp.getComponent(getApplication()).inject(this);

        TodoItem item;
        Bundle args = getIntent().getExtras();

        Bundle arguments = new Bundle();
        arguments.putAll(args);

        mFragment = new EditItemFragment();
        mFragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_list_container, mFragment)
                .commit();

        // Enable buttons
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        item = args.getParcelable(Parcelable.KEY_TODOITEM);

        // Change text
        int stringId = item == null ? R.string.action_create : R.string.action_edit;
        ((TextView) mToolbar.findViewById(R.id.logo_main)).setText(getString(stringId));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Log.d(LOG_TAG, "Triggering intent {SettingsActivity}");
                //Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                //startActivity(intent);
                break;

            case R.id.action_save: {
                Log.d(LOG_TAG, "Saving item");

                TodoItem todoItem = mFragment.getUpdatedTodoItem();
                TodoReminder todoReminder = mFragment.getUpdatedTodoReminder();

                Bundle args = new Bundle();
                args.putParcelable(Parcelable.KEY_TODOITEM, todoItem);
                args.putParcelable(Parcelable.KEY_TODOREMINDER, todoReminder);

                Intent intent = new Intent();
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SOUND) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                mFragment.setTodoReminderAlarmSound(uri != null ? uri : null);
            }
        }
    }

}
