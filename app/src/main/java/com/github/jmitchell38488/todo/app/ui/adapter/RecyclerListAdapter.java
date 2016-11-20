package com.github.jmitchell38488.todo.app.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerListAdapter extends StandardAdapter<TodoItem, TodoItemHolder> {

    // Listeners
    private ListChangeListener mListChangeListener;
    private BindViewHolderListener mOnBindViewHolderListener;

    private List<TodoItem> mPendingActionList;

    public RecyclerListAdapter(Fragment fragment, List<TodoItem> items) {
        super(fragment.getActivity(), items);
        mPendingActionList = new ArrayList<>();
        setHasStableIds(true);
    }

    public void setListChangeListener(ListChangeListener listener) {
        mListChangeListener = listener;
    }

    public void setBindViewHolderListener(BindViewHolderListener listener) {
        mOnBindViewHolderListener = listener;
    }

    public boolean pendingActionListContains(TodoItem item) {
        return mPendingActionList.contains(item);
    }

    public void addItemToPendingActionList(TodoItem item) {
        if (!mPendingActionList.contains(item)) {
            mPendingActionList.add(item);
        }
    }

    public void removeItemFromPendingActionList(TodoItem item) {
        if (mPendingActionList.contains(item)) {
            mPendingActionList.remove(item);
        }
    }

    @Override
    protected TodoItemHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_fragment_notifications, parent, false);
        TodoItemHolder holder = new TodoItemHolder(view, mContext);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Let the listener take care of view binding, it's just easier
        if (mOnBindViewHolderListener != null) {
            mOnBindViewHolderListener.onBindViewHolderListener(holder, position);
        }
    }

    public void remove(int position) {
        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onItemRemoved(position);
            mListChangeListener.onDataChange();
        }

        super.remove(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    public TodoItem getItem(int position) {
        if (mItems.size() > position) {
            return mItems.get(position);
        }

        return null;
    }

    public List<TodoItem> getItems() {
        return mItems;
    }

    public void replace(int position, TodoItem item) {
        if (mItems.size() < position) {
            return;
        }

        // Remove, but make sure that we trigger list update first
        if (mListChangeListener != null) {
            mListChangeListener.onItemRemoved(position);
            mListChangeListener.onDataChange();
        }

        mItems.remove(position);
        notifyItemChanged(position);

        if (mListChangeListener != null) {
            mListChangeListener.onItemAdded(position);
            mListChangeListener.onDataChange();
        }

        mItems.add(position, item);
        notifyItemChanged(position);
    }

    public void addItem(int position, TodoItem item) {
        mItems.add(position, item);
        notifyItemInserted(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onItemAdded(position);
            mListChangeListener.onDataChange();
        }
    }

    public void add(@NonNull List<TodoItem> newItems) {
        super.add(newItems);

        if (mListChangeListener != null) {
            mListChangeListener.onDataChange();
        }
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        if (mListChangeListener != null) {
            mListChangeListener.onItemChange(fromPosition);
            mListChangeListener.onItemChange(toPosition);
            mListChangeListener.onDataChange();
        }
    }

    public interface ListChangeListener {
        void onDataChange();
        void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList);
        void onItemChange(int position);
        void onItemRemoved(int position);
        void onItemAdded(int position);
    }

    public interface BindViewHolderListener {
        void onBindViewHolderListener(RecyclerView.ViewHolder holder, int position);
    }

    public interface ListClickListener {
        void onItemClick(View view);
    }
}