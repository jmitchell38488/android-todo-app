package com.github.jmitchell38488.todo.app.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.listener.ItemTouchListener;
import com.github.jmitchell38488.todo.app.util.TodoItemSorter;
import com.github.jmitchell38488.todo.app.ui.listener.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RecyclerListAdapter extends EndlessAdapter<TodoItem, TodoItemHolder> implements ItemTouchListener {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    // Pending list
    private List<TodoItem> mPendingRemoveList;
    private List<TodoItem> mPendingCompleteList;

    // Listeners
    private OnStartDragListener mDragStartListener;
    private ListChangeListener mListChangeListener;
    private ListClickListener mListClickListener;

    private boolean undoOn;
    private Handler mRunnableHandler = new Handler();
    HashMap<TodoItem, Runnable> mPendingRunnables = new HashMap<>();

    public interface ListChangeListener {
        public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList);
        public void onItemChange(int position);
        public void onDataChange();
    }

    public interface ListClickListener {
        public void onItemClick(View view);
    }

    public RecyclerListAdapter(Context context, List<TodoItem> items,
                               OnStartDragListener dragStartListener,
                               ListChangeListener listChangeListener,
                               ListClickListener listClickListener) {
        super(context, items);

        mDragStartListener = dragStartListener;
        mListChangeListener = listChangeListener;
        mListClickListener = listClickListener;

        mPendingRemoveList = new ArrayList<>();
        mPendingCompleteList = new ArrayList<>();
        TodoItemSorter.sort(mItems);
    }

    public void setListData(List<TodoItem> listData) {
        mItems = listData;
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
        } else {
            itemHolder.updateView(item);

            // Start a drag whenever the handle view it touched
            itemHolder.bindDragEvent(mDragStartListener);

            itemHolder.mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mListClickListener.onItemClick(view);
                }

            });
        }
    }

    public void setItemPendingRemoval(int position) {
        final TodoItem item = mItems.get(position);

        if (!mPendingRemoveList.contains(item)) {
            mPendingRemoveList.add(item);
            notifyItemChanged(position);

            Runnable pending = new Runnable() {
                @Override
                public void run() {
                    remove(mItems.indexOf(item));
                }
            };

            mRunnableHandler.postDelayed(pending, PENDING_REMOVAL_TIMEOUT);
            mPendingRunnables.put(item, pending);
        }
    }

    public void setItemPendingComplete(int position) {
        final TodoItem item = mItems.get(position);

        if (!mPendingCompleteList.contains(item)) {
            mPendingCompleteList.add(item);
            notifyItemChanged(position);

            Runnable pending = new Runnable() {
                @Override
                public void run() {
                    complete(mItems.indexOf(item));
                }
            };

            mRunnableHandler.postDelayed(pending, PENDING_REMOVAL_TIMEOUT);
            mPendingRunnables.put(item, pending);
        }
    }

    public void remove(int position) {
        final TodoItem item = mItems.get(position);
        if (mPendingRemoveList.contains(item)) {
            mPendingRemoveList.remove(item);
        }

        mItems.remove(position);
        notifyItemRemoved(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    public void complete(int position) {
        TodoItem item = mItems.get(position);

        if (mPendingCompleteList.contains(item)) {
            mPendingCompleteList.remove(item);
        }

        int nPosition = getFirstCompletedPosition();

        if (!item.isCompleted()) {
            nPosition--;
        }

        item.setCompleted(!item.isCompleted());

        if (item.isCompleted()) {
            item.setPinned(false);
        }

        // No completed items yet
        if (nPosition < 0) {
            nPosition = mItems.size() - 1;
        }

        remove(position);
        addItem(nPosition, item);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    @Override
    public void onItemDismiss(int position) {
        if (undoOn) {
            setItemPendingRemoval(position);
        } else {
            remove(position);
        }
    }

    public void onItemComplete(int position) {
        if (undoOn) {
            setItemPendingComplete(position);
        } else {
            complete(position);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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

    public int getFirstUnpinnedPosition() {
        int position = INVALID_POSITION;
        synchronized (SEMAPHORE) {
            for (int i = 0, k = mItems.size(); i < k; i++) {
                if (!getItem(i).isPinned()) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    public int getFirstCompletedPosition() {
        int position = INVALID_POSITION;
        synchronized (SEMAPHORE) {
            for (int i = 0, k = mItems.size(); i < k; i++) {
                if (getItem(i).isCompleted()) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isPendingRemoval(int position) {
        return mPendingRunnables.containsKey(mItems.get(position));
    }

    public boolean isPendingComplete(int position) {
        return mPendingRunnables.containsKey(mItems.get(position));
    }
}