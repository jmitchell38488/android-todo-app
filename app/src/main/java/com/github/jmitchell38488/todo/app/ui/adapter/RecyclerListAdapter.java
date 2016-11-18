package com.github.jmitchell38488.todo.app.ui.adapter;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.helper.TodoItemHelper;
import com.github.jmitchell38488.todo.app.ui.listener.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecyclerListAdapter extends EndlessAdapter<TodoItem, TodoItemHolder> {

    // Listeners
    private ListChangeListener mListChangeListener = ListChangeListener.Placeholder;
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

    public List<TodoItem> getPendingActionList() {
        return mPendingActionList;
    }

    public void setPendingActionList(List<TodoItem> items) {
        mPendingActionList = items;
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
        mItems.remove(position);
        notifyItemRemoved(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return (!isLoadMore(position)) ? mItems.get(position).getId() : -1;
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

        mItems.remove(position);
        mItems.add(position, item);
        notifyItemChanged(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onItemChange(position);
            mListChangeListener.onDataChange();
        }
    }

    public void addItem(int position, TodoItem item) {
        mItems.add(position, item);
        notifyItemInserted(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    public interface OnItemClickedListener {

        void onItemClicked(TodoItem item, View view, int position);

        OnItemClickedListener Placeholder = new OnItemClickedListener() {
            @Override
            public void onItemClicked(TodoItem item, View view, int position) {
                // Empty default callback holder
            }
        };

    }

    public interface ListChangeListener {
        void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList);
        void onItemChange(int position);
        void onDataChange();

        ListChangeListener Placeholder = new ListChangeListener() {
            @Override
            public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList) {
                // Empty default callback holder
            }

            @Override
            public void onItemChange(int position) {
                // Empty default callback holder
            }

            @Override
            public void onDataChange() {
                // Empty default callback holder
            }
        };
    }

    public interface BindViewHolderListener {
        void onBindViewHolderListener(RecyclerView.ViewHolder holder, int position);
    }

    public interface ListClickListener {
        void onItemClick(View view);

        ListClickListener Placeholder = view -> {};
    }
}