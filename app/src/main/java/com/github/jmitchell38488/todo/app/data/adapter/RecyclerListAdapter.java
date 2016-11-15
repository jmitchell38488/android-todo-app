package com.github.jmitchell38488.todo.app.data.adapter;

import android.content.Context;
import android.os.Handler;
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
        implements ItemTouchHelperAdapter {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private TodoStorage mTodoStorage;
    private List<TodoItem> mItems;
    private List<TodoItem> mPendingRemoval;

    private Context mContext;
    private OnStartDragListener mDragStartListener;
    private ListChangeListener mListChangeListener;
    private ListClickListener mListClickListener;
    private View.OnClickListener mCompleteClickListener;

    private boolean undoOn;
    private Handler mHandler = new Handler();
    HashMap<TodoItem, Runnable> mPendingRunnables = new HashMap<>();

    private final static Object SEMAPHORE = new Object();

    public interface ListChangeListener {
        public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList);
        public void onItemChange(int position);
        public void onDataChange();
    }

    public interface ListClickListener {
        public void onItemClick(View view);
    }

    public RecyclerListAdapter(Context context, TodoStorage todoStorage,
                               OnStartDragListener dragStartListener,
                               ListChangeListener listChangeListener,
                               ListClickListener listClickListener,
                               View.OnClickListener completeClickListener) {
        mContext = context;
        mTodoStorage = todoStorage;
        mDragStartListener = dragStartListener;
        mListChangeListener = listChangeListener;
        mListClickListener = listClickListener;
        mCompleteClickListener = completeClickListener;

        mItems = mTodoStorage.getTodos();
        mPendingRemoval = new ArrayList<>();
        TodoItemSorter.sort(mItems);
    }

    public void setListData(List<TodoItem> listData) {
        mItems = listData;
    }

    @Override
    public TodoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_fragment_notifications, parent, false);
        TodoItemHolder holder = new TodoItemHolder(view, mContext);
        return holder;
    }

    @Override
    public void onBindViewHolder(TodoItemHolder holder, int position) {
        final TodoItem item = mItems.get(position);

        if (mPendingRemoval.contains(item)) {
            holder.updateViewPendingRemoval(item);

            holder.getRemovePendingView().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Runnable pendingRemovalRunnable = mPendingRunnables.get(item);
                    mPendingRunnables.remove(item);

                    if (pendingRemovalRunnable != null) {
                        mHandler.removeCallbacks(pendingRemovalRunnable);
                        mPendingRemoval.remove(item);
                        notifyItemChanged(mItems.indexOf(item));
                    }
                }

            });
            // user wants to undo the removal, let's cancel the pending task

        } else {
            holder.updateView(item);

            // Start a drag whenever the handle view it touched
            holder.bindDragEvent(mDragStartListener);

            holder.completeHandle.setOnClickListener(mCompleteClickListener);

            holder.mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mListClickListener.onItemClick(view);
                }

            });
        }
    }

    public void setItemPendingRemoval(int position) {
        final TodoItem item = mItems.get(position);

        if (!mPendingRemoval.contains(item)) {
            mPendingRemoval.add(item);
            notifyItemChanged(position);

            Runnable pending = new Runnable() {
                @Override
                public void run() {
                    remove(mItems.indexOf(item));
                }
            };

            mHandler.postDelayed(pending, PENDING_REMOVAL_TIMEOUT);
            mPendingRunnables.put(item, pending);
        }
    }

    public void remove(int position) {
        final TodoItem item = mItems.get(position);
        if (mPendingRemoval.contains(item)) {
            mPendingRemoval.remove(item);
        }

        mItems.remove(position);
        notifyItemRemoved(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }
    }

    @Override
    public void onItemDismiss(int position) {
        setItemPendingRemoval(position);
        /*mItems.remove(position);
        notifyItemRemoved(position);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
        }*/
    }

    public void onItemComplete(int position) {
        int nPosition = getFirstCompletedPosition();
        TodoItem item = mItems.get(position);

        if (!item.isCompleted()) {
            nPosition--;
        }

        item.setCompleted(!item.isCompleted());

        if (item.isCompleted()) {
            item.setPinned(false);
        }

        onItemDismiss(position);
        addItem(nPosition, item);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onDataChange();
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

    public void reorderList() {
        // Clone the items, Map is <id, i>
        HashMap<Integer, Integer> preSortMap = new HashMap<>();
        for (int i = 0; i < mItems.size(); i++) {
            preSortMap.put(mItems.get(i).getId(), i);
        }

        List<TodoItem> mItemsCopy = copyItems();
        TodoItemSorter.sort(mItemsCopy);

        if (mListChangeListener != null) {
            // Notify data changes
            mListChangeListener.onOrderChange(mItems, mItemsCopy);
            mListChangeListener.onDataChange();
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
        int position = RecyclerView.NO_POSITION;
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
        int position = RecyclerView.NO_POSITION;
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
}