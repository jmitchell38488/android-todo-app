package com.github.jmitchell38488.todo.app.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.view.holder.ListItemViewHolder;
import com.github.jmitchell38488.todo.app.ui.view.holder.SectionViewHolder;
import com.github.jmitchell38488.todo.app.util.DateUtility;
import com.twotoasters.sectioncursoradapter.adapter.SectionArrayAdapter;

import java.util.List;

public class TodoSectionAdapter extends SectionArrayAdapter<String, TodoItem, SectionViewHolder, ListItemViewHolder> {

    private final Context mContext;

    public TodoSectionAdapter(Context context, List<TodoItem> arrayData) {
        super(context, R.layout.list_item_title, R.layout.list_item_fragment);
        mContext = context;
        setDataAndBuildSections(arrayData);
    }

    @Override
    protected String getSectionFromItem(TodoItem item) {
        if (item.isCompleted()) {
            return mContext.getString(R.string.status_complete);
        }

        long itemDate = DateUtility.convertDateStringToLong(mContext, item.getDateDue());
        long dateNow = System.currentTimeMillis();

        if (itemDate < dateNow) {
            return mContext.getString(R.string.status_overdue);
        }

        return mContext.getString(R.string.status_todo);
    }

    @Override
    protected SectionViewHolder createSectionViewHolder(View sectionView, String section) {
        return new SectionViewHolder(sectionView);
    }

    @Override
    protected void bindSectionViewHolder(int sectionPosition, SectionViewHolder sectionViewHolder, ViewGroup parent, String section) {
        sectionViewHolder.titleView.setText(section);
    }

    @Override
    protected ListItemViewHolder createItemViewHolder(int sectionPosition, View itemView, TodoItem item) {
        return new ListItemViewHolder(itemView);
    }

    @Override
    protected void bindItemViewHolder(int sectionPosition, int itemPosition, ListItemViewHolder itemViewHolder, ViewGroup parent, TodoItem item) {
        itemViewHolder.dateView.setText(DateUtility.getFormattedMonthDay(mContext,
                DateUtility.convertDateStringToLong(mContext, item.getDateDue())));

        itemViewHolder.titleView.setText(item.getTitle());
    }
}
