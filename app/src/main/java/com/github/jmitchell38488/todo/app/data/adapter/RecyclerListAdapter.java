package com.github.jmitchell38488.todo.app.data.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.helper.ItemTouchHelperAdapter;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<TodoItemHolder>
        implements ItemTouchHelperAdapter, TodoItemHolder.OnCompleteClickListener {

    private TodoStorage mTodoStorage;
    private List<TodoItem> mItems;
    private Context mContext;
    private OnStartDragListener mDragStartListener;
    private ListChangeListener mListChangeListener;
    private ListClickListener mListClickListener;

    public interface ListChangeListener {
        public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList);
        public void onItemChange(int position);
    }

    public interface ListClickListener {
        public void onItemClick(View view, int position);
    }

    public RecyclerListAdapter(Context context, TodoStorage todoStorage,
                               OnStartDragListener dragStartListener,
                               ListChangeListener listChangeListener,
                               ListClickListener listClickListener) {
        mContext = context;
        mTodoStorage = todoStorage;
        mDragStartListener = dragStartListener;
        mListChangeListener = listChangeListener;
        mListClickListener = listClickListener;

        mItems = mTodoStorage.getTodos();
        TodoItemSorter.sort(mItems);
    }

    public void setListData(List<TodoItem> listData) {
        mItems = listData;
    }

    @Override
    public TodoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_fragment_notifications, parent, false);
        TodoItemHolder holder = new TodoItemHolder(view, mContext);
        holder.attachClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TodoItemHolder holder, final int position) {
        holder.updateView(mItems.get(position));

        // Start a drag whenever the handle view it touched
        holder.bindDragEvent(mDragStartListener);

        holder.completeHandle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.onClick();
            }

        });

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListClickListener.onItemClick(v, position);
            }

        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onCompleteClick() {
        reorderList();
    }

    public void reorderList() {
        // Clone the items, Map is <id, i>
        HashMap<Integer, Integer> preSortMap = new HashMap<>();
        for (int i = 0; i < mItems.size(); i++) {
            preSortMap.put(mItems.get(i).getId(), i);
        }

        List<TodoItem> mItemsCopy = copyItems();
        TodoItemSorter.sort(mItemsCopy);

        if (mListChangeListener != null) {
            mListChangeListener.onOrderChange(mItems, mItemsCopy);
        }
    }

    private List<TodoItem> copyItems() {
        List<TodoItem> list = new ArrayList<>();
        for (TodoItem item : mItems) {
            list.add(item);
        }

        return list;
    }

    public TodoItem getItem(int position) {
        if (mItems.size() > position) {
            return mItems.get(position);
        }

        return null;
    }

    public void replace(int position, TodoItem item) {
        if (mItems.size() > position) {
            mItems.remove(position);
            mItems.add(position, item);

            mListChangeListener.onItemChange(position);
        }
    }

    public void addItem(int position, TodoItem item) {
        mItems.add(position, item);

        // reorder the list
        reorderList();
    }
}