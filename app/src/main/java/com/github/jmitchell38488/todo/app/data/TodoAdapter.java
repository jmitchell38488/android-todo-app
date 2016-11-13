package com.github.jmitchell38488.todo.app.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.dialog.DeleteTodoItemDialog;

import java.util.ArrayList;
import java.util.List;

@PerApp
public class TodoAdapter extends ArrayAdapter<TodoItem> {

    private final String dateFormat;
    private List<TodoItem> mItems;

    private TodoStorage mTodoStorage;
    private Activity mActivity;

    public TodoAdapter(Activity activity, Context context, TodoStorage todoStorage, List<TodoItem> items) {
        super(context, R.layout.list_item_fragment, R.id.list_fragment_title, items);
        dateFormat = context.getString(R.string.date_format);
        mItems = items;
        mTodoStorage = todoStorage;
        mActivity = activity;
    }

    private int getLayoutId(boolean hasDesc, boolean hasNotifications) {
        if (hasDesc) {
            return hasNotifications
                    ? R.layout.list_item_fragment_notifications
                    : R.layout.list_item_fragment;
        } else {
            return hasNotifications
                    ? R.layout.list_item_fragment_no_description_notifications
                    : R.layout.list_item_fragment_no_description;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int fposition = position;
        TodoItem item = getItem(position);
        String desc = item.getDescription();
        final boolean hasDesc = !TextUtils.isEmpty(desc);
        final boolean isCompleted = item.isCompleted();
        final boolean isPinned = item.isPinned();
        final boolean hasNotifications = isPinned ? true : false;

        final View mView = LayoutInflater.from(getContext()).inflate(getLayoutId(hasDesc, hasNotifications), parent, false);

        if (hasNotifications) {
            if (!isPinned) {
                mView.findViewById(R.id.list_item_notification_pinned)
                        .setVisibility(View.GONE);
            }

            mView.findViewById(R.id.list_item_notification_reminder)
                    .setVisibility(View.GONE);
        }

        final TextView titleView = (TextView) mView.findViewById(R.id.list_fragment_title);
        final TextView descriptionView = !hasDesc ? null : (TextView) mView.findViewById(R.id.list_fragment_description);
        titleView.setText(item.getTitle());

        if (hasDesc && descriptionView != null) {
            descriptionView.setText(desc);
        }

        if (isCompleted) {
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleView.setTextColor(Color.LTGRAY);

            if (hasDesc) {
                descriptionView.setPaintFlags(descriptionView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descriptionView.setTextColor(Color.LTGRAY);
            }
        }

        ImageView complete = (ImageView) mView.findViewById(R.id.list_item_category);
        ImageView delete = (ImageView) mView.findViewById(R.id.list_item_category_delete);

        if (isCompleted) {
            complete.setBackgroundResource(R.drawable.list_item_category_selected);
        }

        complete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TodoItem item = getItem(fposition);
                updateComplete(item, titleView, descriptionView, mView);
            }

        });

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TodoItem item = (TodoItem) getItem(fposition);
                Bundle arguments = new Bundle();
                arguments.putCharSequence("title", item.getTitle());
                arguments.putBoolean("delete", true);
                arguments.putInt("position", fposition);

                FragmentManager fragmentManager = ((ListActivity) mActivity).getSupportFragmentManager();
                DeleteTodoItemDialog dialogFragment = new DeleteTodoItemDialog();
                dialogFragment.setArguments(arguments);
                dialogFragment.show(fragmentManager, "delete_dialog");
            }

        });

        return mView;
    }

    private void updateComplete(TodoItem item, View titleView, View descView, View rootView) {
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) mTodoStorage.getTodos();

        for (TodoItem ditem : list) {
            if (ditem.getId() == item.getId()) {
                // Set completed
                ditem.setCompleted(!item.isCompleted());
                item.setCompleted(!item.isCompleted());

                // Set pinned
                if (item.isCompleted()) {
                    ditem.setPinned(false);
                }

                // Update UI
                setItemCompleted(item, titleView, descView, rootView);
            }
        }

        // Make sure that we reorder, put completed last
        TodoItemSorter.sort(list);
        mTodoStorage.saveTodos(list);

        // Sort the item list and then notify changed
        TodoItemSorter.sortAdapter(this);
        notifyDataSetChanged();
    }

    private void setItemCompleted(TodoItem item, View titleView, View descView, View rootView) {
        if (!item.isCompleted()) {
            ((TextView) titleView).setTextColor(mActivity.getResources().getColorStateList(R.color.list_selector_text));
            ((TextView) titleView).setPaintFlags(
                    ((TextView) titleView).getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            if (descView != null) {
                ((TextView) descView).setTextColor(mActivity.getResources().getColorStateList(R.color.list_selector_text));
                ((TextView) descView).setPaintFlags(
                        ((TextView) descView).getPaintFlags()
                                & (~Paint.STRIKE_THRU_TEXT_FLAG)
                );
            }
        } else {
            ((TextView) titleView).setPaintFlags(((TextView) titleView).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            ((TextView) titleView).setTextColor(Color.LTGRAY);

            if (descView != null) {
                ((TextView) descView).setPaintFlags(((TextView) descView).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ((TextView) descView).setTextColor(Color.LTGRAY);
            }
        }

        if (item.isPinned() && item.isCompleted()) {
            item.setPinned(false);

            View wrapper = rootView.findViewById(R.id.list_item_notification_wrapper);
            if (wrapper != null) {
                wrapper.findViewById(R.id.list_item_notification_pinned).setVisibility(View.GONE);
            }
        }
    }

}
