package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.util.DateUtility;
import com.github.jmitchell38488.todo.app.util.PreferencesUtility;

import butterknife.BindView;

public class TriggeredAlarmFragment extends BaseFragment {

    @BindView(R.id.alarm_title) TextView titleView;
    @BindView(R.id.alarm_time) TextView timeView;
    @BindView(R.id.alarm_date) TextView dateView;
    @BindView(R.id.alarm_button_snooze) Button snoozeButton;
    @BindView(R.id.alarm_button_dismiss) Button dismissButton;

    protected TodoItem mTodoItem;
    protected TodoReminder mTodoReminder;

    protected View.OnClickListener snoozeListener;
    protected View.OnClickListener dismissListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        Bundle args = getArguments();
        mTodoItem = args.getParcelable(Parcelable.KEY_TODOITEM);
        mTodoReminder = args.getParcelable(Parcelable.KEY_TODOREMINDER);
    }

    public void updateTimeTick() {
        timeView.setText(DateUtility.getTimeForAlarm(getActivity()));
        dateView.setText(DateUtility.getFormattedDateForAlarm(getActivity()));
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

        // Snooze should only be available if we haven't hit the limit yet
        if (mTodoReminder.getTimesSnoozed() < PreferencesUtility.getMaxAlarmSnoozeTimes(mActivity)) {
            snoozeButton.setVisibility(View.VISIBLE);
        }

        snoozeButton.setOnClickListener(snoozeListener);
        dismissButton.setOnClickListener(dismissListener);
    }

    public void setSnoozeClickListener(View.OnClickListener listener) {
        snoozeListener = listener;
    }

    public void setDismissClickListener(View.OnClickListener listener) {
        dismissListener = listener;
    }

}
