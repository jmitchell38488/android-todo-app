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
    private OnStartDragListener mDragStartListener;
    private ListChangeListener mListChangeListener = ListChangeListener.Placeholder;
    private ListClickListener mListClickListener = ListClickListener.Placeholder;

    public RecyclerListAdapter(Fragment fragment, List<TodoItem> items) {
        super(fragment.getActivity(), items);
        mDragStartListener = null;
        setHasStableIds(true);
    }

    public void setStartDragListener(OnStartDragListener listener) {
        mDragStartListener = listener;
    }

    public void setListChangeListener(ListChangeListener listener) {
        mListChangeListener = listener;
    }

    public void setListClickListener(ListClickListener listener) {
        mListClickListener = listener;
    }

    @Override
    protected TodoItemHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_fragment_notifications, parent, false);
        TodoItemHolder holder = new TodoItemHolder(view, mContext);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TodoItemHolder itemHolder = (TodoItemHolder) holder; 
        TodoItem item = mItems.get(position);

        /*
        if (mPendingRemoveList.contains(item) || mPendingCompleteList.contains(item)) {
            boolean removeItem = mPendingRemoveList.contains(item);
            View view = removeItem ?
                    itemHolder.getRemovePendingView() :
                    itemHolder.getCompletePendingView();

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 3);

            view.setMinimumHeight(item.height);
            view.setLayoutParams(lp);

            final int index = mItems.indexOf(item);

            if (removeItem) {
                itemHolder.updateViewPendingRemoval(item);
            } else {
                itemHolder.updateViewPendingComplete(item);
            }

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    TodoItem item = mItems.get(index);
                    Runnable pendingRemovalRunnable = mPendingRunnables.remove(item);

                    if (pendingRemovalRunnable != null) {
                        mRunnableHandler.removeCallbacks(pendingRemovalRunnable);

                        if (mPendingRemoveList.contains(item)) {
                            mPendingRemoveList.remove(item);
                        } else {
                            mPendingCompleteList.remove(item);
                        }

                        notifyItemChanged(index);
                    }
                }
            });
        } else {*/
            itemHolder.updateView(item);

            // Start a drag whenever the handle view it touched
            itemHolder.bindDragEvent(mDragStartListener);

            itemHolder.mView.setOnClickListener(view -> mListClickListener.onItemClick(view));
        //}
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

    public interface ListClickListener {
        void onItemClick(View view);

        ListClickListener Placeholder = new ListClickListener() {
            @Override
            public void onItemClick(View view) {
                // Empty default callback holder
            }
        };
    }
}