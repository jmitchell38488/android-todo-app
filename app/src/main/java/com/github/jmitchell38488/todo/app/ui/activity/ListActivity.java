package com.github.jmitchell38488.todo.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.data.provider.TodoContract;
import com.github.jmitchell38488.todo.app.data.repository.TodoReminderRepository;
import com.github.jmitchell38488.todo.app.data.service.PeriodicNotificationAlarm;
import com.github.jmitchell38488.todo.app.data.service.ReminderAlarm;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.SortedListFragment;
import com.github.jmitchell38488.todo.app.util.DateUtility;
import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends BaseActivity implements ListFragment.ActivityListClickListener {

    static final int REQUEST_CODE = 0;
    static final long POST_DELAYED_TIME = 1000;

    private static final String LOG_TAG = ListActivity.class.getSimpleName();
    private static final String STATE_MODE = "state_mode";
    private static final String ITEMS_FRAGMENT_TAG = "fragment_items";

    @BindView(R.id.fab) FloatingActionButton fab;
    private SortedListFragment mFragment;
    private String mMode = null;
    private boolean mTwoPane = false;

    @Inject ReminderAlarm mReminderAlarm;
    @Inject PeriodicNotificationAlarm mNotificationAlarm;
    @Inject TodoReminderRepository mTodoReminderRepository;

    protected Handler mRunnableHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TodoApp.getComponent(this).inject(this);

        // The feature was disabled but it's still active
        if (!PreferencesUtility.userEnabledPeriodicNotifications() &&
                PreferencesUtility.isPeriodicNotificationsActive()) {
            mNotificationAlarm.cancel();
            PreferencesUtility.setPeriodicNotificationsActive(false);
        }

        // The feature was enabled, but it isn't active
        if (PreferencesUtility.userEnabledPeriodicNotifications() &&
                PreferencesUtility.isPeriodicNotificationsActive()) {
            mNotificationAlarm.start();
            PreferencesUtility.setPeriodicNotificationsActive(true);
        }

        if (savedInstanceState == null) {
            mFragment = SortedListFragment.newInstance(Sort.DEFAULT, Filter.DEFAULT);
            mFragment.setActivityListClickListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, mFragment)
                    .commit();
        } else {
            mFragment = (SortedListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
            mFragment.setActivityListClickListener(this);
        }

        ButterKnife.bind(this);

        fab.setOnClickListener(view -> {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Parcelable.KEY_TODOITEM, null);

            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtras(arguments);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_MODE, mMode);
        getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Bundle arguments) {
        TodoItem item = arguments.getParcelable(Parcelable.KEY_TODOITEM);
        List<TodoReminder> reminders = mTodoReminderRepository.getAllByTodoItemId(item.getId());

        if (!reminders.isEmpty()) {
            arguments.putParcelable(Parcelable.KEY_TODOREMINDER, reminders.get(0));
        }

        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtras(arguments);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle args = data.getExtras();
                TodoItem item = args.getParcelable(Parcelable.KEY_TODOITEM);
                TodoReminder reminder = args.getParcelable(Parcelable.KEY_TODOREMINDER);

                Log.d(LOG_TAG, reminder.toString());

                if (item.getId() > 0) {
                    // Find the matching item
                    if (mFragment.getAdapter().containsItemId(item.getId())) {
                        int position = mFragment.getAdapter().getPositionOfItemId(item.getId());
                        TodoItem listItem = mFragment.getAdapter().getItem(position);
                        listItem.setTitle(item.getTitle());
                        listItem.setDescription(item.getDescription());
                        listItem.setPinned(item.isPinned());
                        listItem.setCompleted(item.isCompleted());
                        listItem.setLocked(item.isLocked());

                        mFragment.onItemChange(position);
                        Toast.makeText(this, getString(R.string.action_save_saved), Toast.LENGTH_SHORT).show();

                        // Delete if this is set to inactive
                        if (reminder.getId() > 0 && !reminder.isActive()) {
                            cancelAlarm(listItem, reminder);
                            mTodoReminderRepository.deleteTodoReminder(reminder);
                        }

                        // Who knows, could be new, just set the id anyway for sanity sake
                        if (reminder.isActive()) {
                            reminder.setItemId(listItem.getId());

                            // Since this uses async saving, we can't guarantee that the ID will be
                            // updated immediately
                            mTodoReminderRepository.saveTodoReminder(reminder);
                            mRunnableHandler.postDelayed(() -> setAlarm(listItem, reminder), POST_DELAYED_TIME);
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.action_save_failed), Toast.LENGTH_SHORT).show();
                    }

                // This is a new item that needs to be inserted
                } else {
                    int position = -1;
                    if (item.isPinned()) {
                        position = 0;
                    } else if (item.isCompleted()) {
                        position = mFragment.getFirstCompletedPosition();
                    } else {
                        position = mFragment.getFirstUnpinnedPosition();
                    }

                    mFragment.getAdapter().addItem(position, item);

                    // Insert the reminder as well
                    if (reminder.isActive()) {
                        // Since this uses async saving, we can't guarantee that the ID will be
                        // updated immediately from either the reminder or the item, so the alarm
                        // will be set approximately 2s after the initial save action
                        mRunnableHandler.postDelayed(() -> {
                            Log.d(LOG_TAG, String.format("Setting item id (%d) for reminder", item.getId()));
                            reminder.setItemId(item.getId());
                            mTodoReminderRepository.saveTodoReminder(reminder);
                            mRunnableHandler.postDelayed(() -> setAlarm(item, reminder), POST_DELAYED_TIME);
                        }, POST_DELAYED_TIME);

                    }
                }
            }
        }
    }

    protected void cancelAlarm(TodoItem item, TodoReminder reminder) {
        mReminderAlarm.cancelAlarm(item, (int) reminder.getId());
        Log.d(LOG_TAG, String.format("Cancelling alarm for task %s", item.getTitle()));
    }

    protected void setAlarm(TodoItem item, TodoReminder reminder) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, reminder.getYear());
        calendar.set(Calendar.MONTH, reminder.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, reminder.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMinute());

        long time = calendar.getTimeInMillis();
        Log.d(LOG_TAG, String.format("Setting alarm time %d (sys: %d), difference: %d seconds",
                time, System.currentTimeMillis(), (time - System.currentTimeMillis()) / 1000));

        mReminderAlarm.createAndStartAlarm(item, (int) reminder.getId(), calendar.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date_format_date_alarm_toast));
        String date = format.format(calendar.getTimeInMillis());

        Toast.makeText(this, getString(R.string.alarm_set_message, date), Toast.LENGTH_LONG);

        Log.d(LOG_TAG, String.format("Creating alarm for task %s at %s", item.getTitle(), calendar.toString()));
    }

}
