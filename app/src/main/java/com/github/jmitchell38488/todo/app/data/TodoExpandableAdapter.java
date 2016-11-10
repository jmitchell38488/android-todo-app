package com.github.jmitchell38488.todo.app.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.annotation.PerApp;
import com.github.jmitchell38488.todo.app.util.DateUtility;

import java.util.Date;
import java.util.List;
import java.util.Map;

@PerApp
public class TodoExpandableAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mListHeaders;
    private Map<String, List<TodoItem>> mListItems;

    public TodoExpandableAdapter(Context context, List<String> listHeaders, Map<String, List<TodoItem>> listItems) {
        this.mContext = context;
        this.mListHeaders = listHeaders;
        this.mListItems = listItems;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListItems.get(mListHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_fragment, null);
        }

        // Get TodoItem from the stack
        TodoItem item = (TodoItem) getChild(groupPosition, childPosition);

        // Apply layout changes
        TextView titleView = (TextView) convertView.findViewById(R.id.list_fragment_title);
        titleView.setText(item.getTitle());

        TextView dateView = (TextView) convertView.findViewById(R.id.list_fragment_duedate);
        Date now = new Date();
        long dueTime = DateUtility.convertDateStringToLong(mContext, item.getDateDue());
        StringBuilder builder = new StringBuilder();

        if (dueTime > 0) {
            if (dueTime < now.getTime()) {
                builder.append("Overdue")
                       .append(" ")
                       .append(mContext.getString(R.string.today))
                       .append(" ");
            }
        }

        builder.append(item.getDateDue());
        dateView.setText(builder.toString());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListItems.get(mListHeaders.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_title, null);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.list_fragment_section_title);
        titleView.setText(title);

        ((ExpandableListView) parent).expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
