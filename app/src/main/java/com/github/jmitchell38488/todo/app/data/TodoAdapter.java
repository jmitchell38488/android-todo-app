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
import com.github.jmitchell38488.todo.app.util.ItemUtility;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int fposition = position;
        TodoItem item = getItem(position);
        String desc = item.getDescription();
        boolean hasDesc = !TextUtils.isEmpty(desc);
        boolean iscomplete = item.isCompleted();

        View mView;

        if (hasDesc) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_fragment, parent, false);
        } else {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_fragment_no_description, parent, false);
        }

        TextView titleView = (TextView) mView.findViewById(R.id.list_fragment_title);
        titleView.setText(item.getTitle());

        if (iscomplete) {
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            titleView.setTextColor(Color.LTGRAY);
        }

        if (hasDesc) {
            TextView descriptionView = (TextView) mView.findViewById(R.id.list_fragment_description);
            descriptionView.setText(item.getDescription());

            if (iscomplete) {
                descriptionView.setPaintFlags(descriptionView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descriptionView.setTextColor(Color.LTGRAY);
            }
        }

        ImageView complete = (ImageView) mView.findViewById(R.id.list_item_category);
        ImageView delete = (ImageView) mView.findViewById(R.id.list_item_category_delete);

        if (iscomplete) {
            complete.setBackgroundResource(R.drawable.list_item_category_selected);
        }

        complete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TodoItem item = getItem(fposition);
                updateComplete(item);
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

    private void updateComplete(TodoItem item) {
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) mTodoStorage.getTodos();

        for (TodoItem ditem : list) {
            if (ditem.getId() == item.getId()) {
                ditem.setCompleted(!item.isCompleted());
            }
        }

        // Make sure that we reorder, put completed last
        list = (ArrayList<TodoItem>) ItemUtility.reorderTodoItemList(list);
        mTodoStorage.saveTodos(list);

        // Refresh the adaptor
        clear();
        addAll(list);
        notifyDataSetChanged();
    }

}
