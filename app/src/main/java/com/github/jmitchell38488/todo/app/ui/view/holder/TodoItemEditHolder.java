package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
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

    boolean hasTriggeredDateClick = false;
    boolean hasTriggeredTimeClick = false;

    @BindView(R.id.item_edit_title_icon) TextView titleIcon;
    @BindView(R.id.item_edit_title) public EditText titleView;

    @BindView(R.id.item_edit_description_icon) TextView descriptionIcon;
    @BindView(R.id.item_edit_description) public TextView descriptionView;

    @BindView(R.id.edit_item_time_selector) RelativeLayout timeSelector;
    @BindView(R.id.edit_item_sound_selector) RelativeLayout soundSelector;
    @BindView(R.id.item_edit_pinned_container) RelativeLayout pinnedSelector;
    @BindView(R.id.item_edit_locked_container) RelativeLayout lockedSelector;
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

    @BindView(R.id.item_edit_pinned_icon) TextView pinnedIcon;
    @BindView(R.id.item_edit_pinned_text) TextView pinnedField;
    @BindView(R.id.item_edit_pinned) public Switch pinnedSwitch;

    @BindView(R.id.item_edit_locked_icon) TextView lockedIcon;
    @BindView(R.id.item_edit_locked_text) TextView lockedField;
    @BindView(R.id.item_edit_locked) public Switch lockedSwitch;

    @BindView(R.id.item_edit_completed_icon) TextView completedIcon;
    @BindView(R.id.item_edit_completed_text) TextView completedField;
    @BindView(R.id.item_edit_completed) public Switch completedSwitch;


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
    }

    public void updateView() {
        if (!hasReminderTime) {
            timeSelector.setVisibility(View.GONE);
            soundSelector.setVisibility(View.GONE);
        }

        titleIcon.setOnClickListener(v -> {
            titleView.setEnabled(true);
            titleView.requestFocus();
        });

        descriptionIcon.setOnClickListener(v -> descriptionView.performClick());
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        pinnedIcon.setOnClickListener(v -> pinnedSwitch.setChecked(!mItem.isPinned()));
        pinnedField.setOnClickListener(v -> pinnedSwitch.setChecked(!mItem.isPinned()));
        pinnedSwitch.setChecked(mItem.isPinned());
        pinnedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setPinned(isChecked);
        });

        lockedIcon.setOnClickListener(v -> lockedSwitch.setChecked(!mItem.isLocked()));
        lockedField.setOnClickListener(v -> lockedSwitch.setChecked(!mItem.isLocked()));
        lockedSwitch.setChecked(mItem.isLocked());
        lockedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setLocked(isChecked);
        });

        completedIcon.setOnClickListener(v -> completedSwitch.setChecked(!mItem.isCompleted()));
        completedField.setOnClickListener(v -> completedSwitch.setChecked(!mItem.isCompleted()));
        completedSwitch.setChecked(mItem.isCompleted());
        completedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setCompleted(isChecked);
            if (mItem.isCompleted()) {
                lockedSelector.setVisibility(View.GONE);
                pinnedSelector.setVisibility(View.GONE);
            } else {
                lockedSelector.setVisibility(View.VISIBLE);
                pinnedSelector.setVisibility(View.VISIBLE);
            }
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

        if (mItem.isCompleted()) {
            lockedSelector.setVisibility(View.GONE);
            pinnedSelector.setVisibility(View.GONE);
        }

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

        View.OnClickListener dateClick = (view) -> {
            handleOnDatePickerClick(view);
        };

        View.OnFocusChangeListener dateFocus = (view, focused) -> {
            mInputMethodManager.hideSoftInputFromWindow(dateField.getWindowToken(), 0);
            if (!focused) {
                return;
            }

            handleOnDatePickerClick(view);
        };

        View.OnClickListener timeClick = (view) -> {
            handleOnTimePickerClick(view);
        };


        View.OnFocusChangeListener timeFocus = (view, focused) -> {
            mInputMethodManager.hideSoftInputFromWindow(timeField.getWindowToken(), 0);
            if (!focused) {
                return;
            }

            handleOnTimePickerClick(view);
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

    private void handleOnDatePickerClick(View view) {
        if (hasTriggeredDateClick) {
            return;
        }

        hasTriggeredDateClick = true;

        DatePickerDialog dialog = new DatePickerDialog(mContext, R.style.AlertDialogTheme, dateCallback,
                reminderDate.get(Calendar.YEAR), reminderDate.get(Calendar.MONTH),
                reminderDate.get(Calendar.DAY_OF_MONTH));

        // Reset the triggered value when it is dismissed
        dialog.setOnCancelListener(d -> hasTriggeredDateClick = false);
        dialog.setOnDismissListener(d -> hasTriggeredDateClick = false);
        dialog.show();
    }

    private void handleOnTimePickerClick(View view) {
        if (hasTriggeredTimeClick) {
            return;
        }

        hasTriggeredTimeClick = true;
        TimePickerDialog dialog = new TimePickerDialog(mContext, R.style.AlertDialogTheme, timeCallback,
                reminderDate.get(Calendar.HOUR),
                reminderDate.get(Calendar.MINUTE), false);

        // Reset the triggered value when it is dismissed
        dialog.setOnCancelListener(d -> hasTriggeredTimeClick = false);
        dialog.setOnDismissListener(d -> hasTriggeredTimeClick = false);
        dialog.show();
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

    public void setDescriptionClickListener(View.OnClickListener listener) {
        descriptionView.setOnClickListener(listener);
    }

    public void setDescriptionText(String text) {
        descriptionView.setText(text);
    }

}