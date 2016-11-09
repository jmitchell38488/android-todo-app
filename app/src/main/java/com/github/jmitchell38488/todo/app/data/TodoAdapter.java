package com.github.jmitchell38488.todo.app.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.annotation.PerApp;

import java.util.ArrayList;

@PerApp
public class TodoAdapter extends ArrayAdapter<TodoItem> {

    public TodoAdapter(Context context, ArrayList<TodoItem> items) {
        super(context, R.layout.list_item_fragment, R.id.list_fragment_title, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_fragment, parent, false);

        TodoItem item = getItem(position);
        TextView titleView = (TextView) mView.findViewById(R.id.list_fragment_title);
        titleView.setText(item.getTitle());

        TextView foo = (TextView) mView.findViewById(R.id.something_something);
        foo.setText("position: " + position + ", count: " + getCount());

        return mView;
    }
}
