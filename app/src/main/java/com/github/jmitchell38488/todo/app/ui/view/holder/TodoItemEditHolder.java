package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;
import com.github.jmitchell38488.todo.app.util.AlarmUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoItemEditHolder {

    private View mView;
    private Context mContext;
    private TodoItem mItem;
    private TodoReminder mReminder;
    private InputMethodManager mInputMethodManager;

    public Calendar reminderDate;

    public boolean hasReminderDate = false;
    public boolean hasReminderTime = false;
    public boolean isNew = false;

    @BindView(R.id.item_edit_title) public RobotoLightEditText titleView;
    @BindView(R.id.item_edit_description) public RobotoLightEditText descriptionView;
    @BindView(R.id.item_edit_pinned) public Switch pinnedSwitch;
    @BindView(R.id.item_edit_completed) public Switch completedSwitch;
    @BindView(R.id.item_edit_locked) public Switch lockedSwitch;

    @BindView(R.id.edit_item_time_selector) RelativeLayout timeSelector;
    @BindView(R.id.edit_item_sound_selector) RelativeLayout soundSelector;
    @BindView(R.id.item_edit_completed_container) RelativeLayout completedSelector;

    @BindView(R.id.item_edit_date_field) TextView dateField;
    @BindView(R.id.item_edit_date_icon) TextView dateIcon;
    @BindView(R.id.item_edit_date_delete) TextView dateDelete;

    @BindView(R.id.item_edit_time_field) TextView timeField;
    @BindView(R.id.item_edit_time_icon) TextView timeIcon;
    @BindView(R.id.item_edit_time_delete) TextView timeDelete;

    @BindView(R.id.item_edit_sound_icon) public TextView soundIcon;
    @BindView(R.id.item_edit_sound_field) public TextView soundField;
    @BindView(R.id.item_edit_sound_delete) public TextView soundDelete;

    public TodoItemEditHolder(View view, Context context,
                              @Nullable TodoItem item,
                              @Nullable TodoReminder reminder) {
        mView = view;
        mContext = context;
        mItem = item;
        mReminder = reminder;
        mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (mReminder.isActive()) {
            hasReminderDate = true;
            if (reminder.getHour() > 0 || reminder.getMinute() > 0) {
                hasReminderTime = true;
            }
        }

        ButterKnife.bind(this, mView);

        updateView();
    }

    public void updateView() {
        if (!hasReminderTime) {
            timeSelector.setVisibility(View.GONE);
            soundSelector.setVisibility(View.GONE);
        }

        pinnedSwitch.setChecked(mItem.isPinned());
        pinnedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setPinned(isChecked);
        });

        completedSwitch.setChecked(mItem.isCompleted());
        completedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setCompleted(isChecked);
        });

        lockedSwitch.setChecked(mItem.isLocked());
        lockedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setLocked(isChecked);
        });

        if (!TextUtils.isEmpty(mItem.getTitle())) {
            titleView.setText(mItem.getTitle());
        }

        if (!TextUtils.isEmpty(mItem.getDescription())) {
            descriptionView.setText(mItem.getDescription());
        }

        dateDelete.setVisibility(View.GONE);
        timeDelete.setVisibility(View.GONE);
        soundDelete.setVisibility(View.GONE);

        reminderDate = Calendar.getInstance();
        if (mReminder.isActive()) {
            reminderDate.set(Calendar.YEAR, mReminder.getYear());
            reminderDate.set(Calendar.MONTH, mReminder.getMonth());
            reminderDate.set(Calendar.DAY_OF_MONTH, mReminder.getDay());
            reminderDate.set(Calendar.HOUR_OF_DAY, mReminder.getHour());
            reminderDate.set(Calendar.MINUTE, mReminder.getMinute());

            if (hasReminderDate) {
                SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.date_format_date_alarm_field));
                dateField.setText(format.format(reminderDate.getTime()));
                dateDelete.setVisibility(View.VISIBLE);
            }

            if (hasReminderTime) {
                SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.date_format_time));
                timeField.setText(format.format(reminderDate.getTime()));
                timeDelete.setVisibility(View.VISIBLE);
            }
        }

        DatePickerDialog.OnDateSetListener dateCallback = (view, year, monthOfYear, dayOfMonth) -> {
            reminderDate.set(Calendar.YEAR, year);
            reminderDate.set(Calendar.MONTH, monthOfYear);
            reminderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.date_format_date_alarm_field));
            dateField.setText(format.format(reminderDate.getTime()));

            hasReminderDate = true;
            timeSelector.setVisibility(View.VISIBLE);
            soundSelector.setVisibility(View.VISIBLE);
            dateDelete.setVisibility(View.VISIBLE);
        };

        TimePickerDialog.OnTimeSetListener timeCallback = (view, hourOfDay, minute) -> {
            reminderDate.set(Calendar.HOUR, hourOfDay >= 12 && hourOfDay < 25 ? hourOfDay - 12 : hourOfDay);
            reminderDate.set(Calendar.MINUTE, minute);
            reminderDate.set(Calendar.AM_PM, hourOfDay >= 12 && hourOfDay < 24 ? Calendar.PM : Calendar.AM);

            SimpleDateFormat format = new SimpleDateFormat(mContext.getString(R.string.date_format_time));
            timeField.setText(format.format(reminderDate.getTime()));
            hasReminderTime = true;
            timeDelete.setVisibility(View.VISIBLE);
        };

        View.OnClickListener dateClick = (view) ->
            new DatePickerDialog(mContext, dateCallback,
                    reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH),
                    reminderDate.get(Calendar.DAY_OF_MONTH)).show();

        View.OnFocusChangeListener dateFocus = (view, focused) -> {
            mInputMethodManager.hideSoftInputFromWindow(dateField.getWindowToken(), 0);
            if (!focused) {
                return;
            }

            new DatePickerDialog(mContext, dateCallback,
                    reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH),
                    reminderDate.get(Calendar.DAY_OF_MONTH)).show();
        };

        View.OnClickListener timeClick = (view) ->
            new TimePickerDialog(mContext, timeCallback,
                    reminderDate.get(Calendar.HOUR),
                    reminderDate.get(Calendar.MINUTE), false).show();

        View.OnFocusChangeListener timeFocus = (view, focused) -> {
            mInputMethodManager.hideSoftInputFromWindow(timeField.getWindowToken(), 0);
            if (!focused) {
                return;
            }

            new TimePickerDialog(mContext, timeCallback,
                    reminderDate.get(Calendar.HOUR),
                    reminderDate.get(Calendar.MINUTE), false).show();
        };

        dateField.setOnFocusChangeListener(dateFocus);
        dateField.setOnClickListener(dateClick);
        dateIcon.setOnClickListener(dateClick);
        dateDelete.setOnClickListener(view -> {
            dateField.setText("");
            hasReminderDate = false;
            hasReminderTime = false;
            timeSelector.setVisibility(View.GONE);
            soundSelector.setVisibility(View.GONE);
            dateDelete.setVisibility(View.GONE);
        });

        timeField.setOnFocusChangeListener(timeFocus);
        timeField.setOnClickListener(timeClick);
        timeIcon.setOnClickListener(timeClick);
        timeDelete.setOnClickListener(view -> {
            timeField.setText("");
            hasReminderTime = false;
            timeDelete.setVisibility(View.GONE);
        });

        if (mReminder.getSound() != null) {
            soundField.setText(AlarmUtility.getAlarmSoundTitle(mContext, mReminder.getSound()));
            soundDelete.setVisibility(View.VISIBLE);
        }

        if (isNew) {
            completedSelector.setVisibility(View.GONE);
        }
    }

    public void setSoundClickListener(View.OnClickListener listener) {
        soundField.setOnClickListener(listener);
        soundIcon.setOnClickListener(listener);
    }

    public void setSoundDeleteClickListener(View.OnClickListener listener) {
        soundDelete.setOnClickListener(listener);
    }

    public void setSoundFieldTitle(String title) {
        soundField.setText(title);
    }

    /**
     * Helper function to return a hash of all the values combined to detect whether or not there
     * have been value changes. This is required for detecting back button/return to home actions
     * when the user is still editing
     */
    public void getHolderValueHash() {

    }

}