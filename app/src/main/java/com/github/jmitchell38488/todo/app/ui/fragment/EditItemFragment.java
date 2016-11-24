package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.model.TodoReminder;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemEditHolder;
import com.github.jmitchell38488.todo.app.util.ItemUtility;

import java.util.Calendar;

import javax.inject.Inject;

public class EditItemFragment extends Fragment {

    private View mView;
    private TodoItem mItem;
    private TodoReminder mReminder;
    private String mItemHash;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        TodoItem item = null;
        TodoReminder reminder = null;

        if (arguments != null && arguments.getParcelable(Parcelable.KEY_TODOITEM) != null) {
            item = arguments.getParcelable(Parcelable.KEY_TODOITEM);
        }

        if (arguments != null && arguments.getParcelable(Parcelable.KEY_TODOREMINDER) != null) {
            reminder = arguments.getParcelable(Parcelable.KEY_TODOREMINDER);
        }

        if (item == null) {
            item = new TodoItem();
        }

        if (reminder == null) {
            reminder = new TodoReminder();
        }

        mItem = item;
        mReminder = reminder;
        mItemHash = ItemUtility.md5(item.toString());
        mView = inflater.inflate(R.layout.fragment_edit_item, container, false);
        TodoItemEditHolder viewHolder = new TodoItemEditHolder(mView, getActivity(), mItem, mReminder);
        mView.setTag(viewHolder);
        return mView;
    }

    public TodoItem getUpdatedTodoItem() {
        TodoItemEditHolder holder = (TodoItemEditHolder) mView.getTag();
        mItem.setTitle(holder.titleView.getText().toString());
        mItem.setDescription(holder.descriptionView.getText().toString());
        mItem.setPinned(holder.pinnedSwitch.isChecked());
        mItem.setCompleted(holder.completedSwitch.isChecked());
        mItem.setLocked(holder.lockedSwitch.isChecked());

        return mItem;
    }

    public TodoReminder getUpdatedTodoReminder() {
        TodoItemEditHolder holder = (TodoItemEditHolder) mView.getTag();
        Calendar reminderDate = holder.reminderDate;

        if (holder.hasReminderDate) {
            mReminder.setYear(reminderDate.get(Calendar.YEAR));
            mReminder.setMonth(reminderDate.get(Calendar.MONTH));
            mReminder.setDay(reminderDate.get(Calendar.DAY_OF_MONTH));
        }

        // Specific time or all day?
        if (holder.hasReminderTime) {
            mReminder.setHour(reminderDate.get(Calendar.HOUR_OF_DAY));
            mReminder.setMinute(reminderDate.get(Calendar.MINUTE));
        } else {
            mReminder.setHour(0);
            mReminder.setMinute(0);
        }

        // Set active state
        mReminder.setActive(holder.hasReminderDate || holder.hasReminderTime);

        return mReminder;
    }

    public String getItemHash() {
        return mItemHash;
    }

}
