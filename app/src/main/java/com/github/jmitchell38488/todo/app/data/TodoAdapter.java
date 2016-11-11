package com.github.jmitchell38488.todo.app.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.annotation.PerApp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@PerApp
public class TodoAdapter extends ArrayAdapter<TodoItem> {

    private final String dateFormat;
    private List<TodoItem> mItems;

    public TodoAdapter(Context context, List<TodoItem> items) {
        super(context, R.layout.list_item_fragment, R.id.list_fragment_title, items);
        dateFormat = context.getString(R.string.date_format);
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_fragment, parent, false);

        TodoItem item = getItem(position);
        TextView titleView = (TextView) mView.findViewById(R.id.list_fragment_title);
        titleView.setText(item.getTitle());

        TextView descriptionView = (TextView) mView.findViewById(R.id.list_fragment_description);
        descriptionView.setText(item.getDescription());

        return mView;
    }

}
