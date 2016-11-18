package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.helper.TodoItemHelper;
import com.github.jmitchell38488.todo.app.ui.listener.ItemStateChangeListener;
import com.github.jmitchell38488.todo.app.ui.listener.ItemTouchListener;
import com.github.jmitchell38488.todo.app.ui.listener.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.listener.ItemTouchCallback;
import com.github.jmitchell38488.todo.app.ui.decoration.VerticalSpaceItemDecoration;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public abstract class ListFragment extends BaseFragment
        implements OnStartDragListener, RecyclerListAdapter.ListChangeListener {

    protected final static Object SEMAPHORE = new Object();
    protected final int INVALID_POSITION = RecyclerView.NO_POSITION;

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private static final String LOG_TAG = ListFragment.class.getSimpleName();

    private static final String STATE_LIST = "state_list";
    private static final String STATE_POSITION = "state_position";
    private static final String STATE_UNDO = "state_undo";

    protected ItemTouchHelper mItemTouchHelper;
    protected RecyclerListAdapter mAdapter;
    @BindView(R.id.list_container) RecyclerView mRecyclerView;
    @BindView(R.id.empty_list) View mEmptyListView;

    @Inject TodoStorage todoStorage;

    private int mPosition;

    protected TodoItemHelper mHelper;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected TodoItemRepository mItemRepository;
    protected CompositeSubscription mSubscriptions;

    protected List<TodoItem> mPendingRemoveList;
    protected List<TodoItem> mPendingCompleteList;

    protected boolean mUndoOn;
    protected Handler mRunnableHandler = new Handler();
    protected HashMap<TodoItem, Runnable> mPendingRunnables = new HashMap<>();

    private RecyclerListAdapter.ListClickListener mListClickListener = view -> {
        int position = mRecyclerView.getChildLayoutPosition(view);
        TodoItem item = mAdapter.getItem(position);

        Bundle arguments = new Bundle();
        arguments.putParcelable("todoitem", item);

        mPosition = position;

        /*Intent intent = new Intent(ListFragment.this.getActivity(), EditItemActivity.class);
        intent.putExtras(arguments);
        startActivity(intent);*/
        //return;

        ((ListActivity) getActivity()).showEditDialog(arguments);
    };

    private RecyclerListAdapter.BindViewHolderListener mBindViewHolderListener = (holder, position) -> {
        final TodoItemHolder itemHolder = (TodoItemHolder) holder;
        final TodoItem item = mAdapter.getItem(position);

        // Display the pending action view, remove or complete
        if (mAdapter.pendingActionListContains(item)) {
            final boolean actionRemove = mPendingRemoveList.contains(item);

            View actionView = (actionRemove) ?
                    itemHolder.getRemovePendingView() :
                    itemHolder.getCompletePendingView();

            if (actionRemove) {
                itemHolder.updateViewPendingRemoval();
            } else {
                itemHolder.updateViewPendingComplete();
            }

            ViewGroup.LayoutParams lp = itemHolder.mView.getLayoutParams();
            lp.height = item.height;
            itemHolder.mView.setLayoutParams(lp);

            //actionView.setMinimumHeight(item.height);

            actionView.setOnClickListener(view -> {
                if (itemHolder.isPendingComplete()) {
                    mPendingCompleteList.remove(item);
                } else {
                    mPendingRemoveList.remove(item);
                }

                mAdapter.removeItemFromPendingActionList(item);
                Runnable runner = mPendingRunnables.remove(item);

                if (runner != null) {
                    mRunnableHandler.removeCallbacks(runner);
                }

                mAdapter.notifyItemChanged(position);
            });

        // Display the default view
        } else {
            itemHolder.updateView(item);
            itemHolder.bindDragEvent(ListFragment.this);
            itemHolder.mView.setOnClickListener(view -> mListClickListener.onItemClick(view));
            itemHolder.getItemVisibleView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                item.height = itemHolder.getItemVisibleView().getHeight();
            });
        }
    };

    private ItemStateChangeListener mItemStateChangeListener = new ItemStateChangeListener() {

        @Override
        public void onItemComplete(int position) {
            Log.d(LOG_TAG, "Marking item at position (" + position + ") for completion");
            final TodoItem item = mAdapter.getItem(position);

            if (!mPendingCompleteList.contains(item)) {
                mPendingCompleteList.add(item);
                mAdapter.addItemToPendingActionList(item);
                mAdapter.notifyItemChanged(position);

                /*Runnable pending = () -> {
                    mAdapter.removeItemFromPendingActionList(item);
                    mHelper.setItemComplete(item.getId());
                };

                mRunnableHandler.postDelayed(pending, PENDING_REMOVAL_TIMEOUT);
                mPendingRunnables.put(item, pending);*/
            }
        }

        @Override
        public void onItemDismiss(int position) {
            Log.d(LOG_TAG, "Marking item at position (" + position + ") for removal");
            final TodoItem item = mAdapter.getItem(position);

            if (!mPendingRemoveList.contains(item)) {
                mPendingRemoveList.add(item);
                mAdapter.addItemToPendingActionList(item);
                mAdapter.notifyItemChanged(position);

                /*Runnable pending = () -> {
                    mAdapter.removeItemFromPendingActionList(item);
                    mHelper.setItemRemoved(item.getId());
                };

                mRunnableHandler.postDelayed(pending, PENDING_REMOVAL_TIMEOUT);
                mPendingRunnables.put(item, pending);*/
            }
        }
    };

    private ItemTouchListener mItemTouchListener = new ItemTouchListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            mAdapter.moveItem(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemSwipeRight(int position) {
            if (mUndoOn) {
                mItemStateChangeListener.onItemDismiss(position);
            } else {
                TodoItem item = mAdapter.getItem(position);
                mHelper.setItemRemoved(item.getId());
            }

            Log.d(LOG_TAG, "onItemSwipeRight(" + position + "), Undo: " + (mUndoOn ? "On" : "Off"));
        }

        @Override
        public void onItemSwipeLeft(int position) {
            if (mUndoOn) {
                mItemStateChangeListener.onItemComplete(position);
            } else {
                TodoItem item = mAdapter.getItem(position);
                mHelper.setItemComplete(item.getId());
            }

            Log.d(LOG_TAG, "onItemSwipeLeft(" + position + "), Undo: " + (mUndoOn ? "On" : "Off"));
        }
    };

    public int getFirstUnpinnedPosition() {
        int position = INVALID_POSITION;
        synchronized (SEMAPHORE) {
            for (int i = 0, k = mAdapter.getItemCount(); i < k; i++) {
                if (!mAdapter.getItem(i).isPinned()) {
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
            for (int i = 0, k = mAdapter.getItemCount(); i < k; i++) {
                if (mAdapter.getItem(i).isCompleted()) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    public ListFragment() {
        mPendingRemoveList = new ArrayList<>();
        mPendingCompleteList = new ArrayList<>();
        mUndoOn = true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mHelper = new TodoItemHelper(activity, mItemRepository);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TodoApp.getComponent(getActivity()).inject(this);

        mSubscriptions = new CompositeSubscription();

        mPosition = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_POSITION, -1)
                : -1;

        List<TodoItem> restoredList = savedInstanceState != null
                ? savedInstanceState.getParcelableArrayList(STATE_LIST)
                : todoStorage.getTodos();

        mUndoOn = savedInstanceState != null
                ? savedInstanceState.getBoolean(STATE_UNDO)
                : true;

        mAdapter = new RecyclerListAdapter(this, restoredList);
        mAdapter.setListChangeListener(this);
        mAdapter.setBindViewHolderListener(mBindViewHolderListener);

        initRecyclerView();
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != ListView.INVALID_POSITION) {
            mPosition = getItemPositionFromYOffset();
        }

        List<TodoItem> itemList = new ArrayList<>(mAdapter.getItems());
        outState.putParcelableArrayList(STATE_LIST, new ArrayList<>(mAdapter.getItems()));
        outState.putInt(STATE_POSITION, mPosition);
        outState.putBoolean(STATE_UNDO, mUndoOn);
    }

    @Override
    public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList) {
        // Set the new sorted list
        ((RecyclerListAdapter) mRecyclerView.getAdapter()).set(newList);

        // Notify data changed, invalidate and reset the layout manager
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.invalidate();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onItemChange(int position) {
        mRecyclerView.getAdapter().notifyItemChanged(position);
    }

    @Override
    public void onDataChange() {
        // Show alternative view
        if (mAdapter.getItemCount() == 0) {
            mEmptyListView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else if (mAdapter.getItemCount() > 0 && mRecyclerView.getVisibility() == View.GONE) {
            mEmptyListView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private int getItemPositionFromYOffset() {
        LinearLayoutManager lMan = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int lastVisible = lMan.findLastCompletelyVisibleItemPosition();

        return lastVisible;
    }

    public void scrollToTop(boolean smooth) {
        if (smooth)
            mRecyclerView.smoothScrollToPosition(0);
        else
            mRecyclerView.scrollToPosition(0);
    }

    public TodoItem getItemFromAdapter(int position) {
        return mAdapter.getItem(position);
    }

    public void saveUpdatedItem(int position, TodoItem item) {
        TodoItem mItem = mAdapter.getItem(position);

        // Flags haven't changed?
        if (mItem.isCompleted() == item.isCompleted() &&
                mItem.isPinned() == item.isPinned()) {
            replaceAdapterItem(position, item);
            return;
        }

        // Not so lucky, remove and add it!
        removeItem(position, false);
        addItem(item);
    }

    public void replaceAdapterItem(int position, TodoItem item) {
        mAdapter.replace(position, item);
    }

    public void addItem(TodoItem item) {
        int newPosition = RecyclerView.NO_POSITION;

        if (item.isCompleted()) {
            newPosition = getFirstCompletedPosition();
        } else {
            newPosition = item.isPinned() ? 0 : getFirstUnpinnedPosition();
        }

        if (newPosition == RecyclerView.NO_POSITION) {
            newPosition = 0;
        }

        mAdapter.addItem(newPosition, item);
        mRecyclerView.smoothScrollToPosition(newPosition);
    }

    public void removeItem(int position, boolean undo) {
        /*if (undo) {
            mAdapter.onItemDismiss(position);
        } else {
            mAdapter.remove(position);
        }*/
    }

    protected void initRecyclerView() {
        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(listDividerHeight));

        // Set touch helper
        ItemTouchHelper.Callback callback = new ItemTouchCallback(mItemTouchListener, getActivity());
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // Scroll to previous position
        if (mPosition != ListView.INVALID_POSITION) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        if (mAdapter.getItemCount() == 0) {
            mEmptyListView.setVisibility(View.VISIBLE);
        } else {
            mEmptyListView.setVisibility(View.GONE);
        }
    }

}
