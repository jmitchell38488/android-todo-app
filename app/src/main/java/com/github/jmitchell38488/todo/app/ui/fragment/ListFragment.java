package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.data.repository.TodoItemRepository;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.activity.EditItemActivity;
import com.github.jmitchell38488.todo.app.ui.helper.TodoItemHelper;
import com.github.jmitchell38488.todo.app.ui.listener.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.listener.SimpleItemTouchHelperCallback;
import com.github.jmitchell38488.todo.app.ui.decoration.VerticalSpaceItemDecoration;

 import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public abstract class ListFragment extends BaseFragment
        implements OnStartDragListener, RecyclerListAdapter.ListChangeListener {

    private static final String STATE_LIST = "state_list";
    private static final String STATE_POSITION = "state_position";

    protected ItemTouchHelper mItemTouchHelper;
    protected RecyclerListAdapter mAdapter;
    @BindView(R.id.list_container) RecyclerView mRecyclerView;
    @BindView(R.id.empty_list) View mEmptyListView;

    @Inject TodoStorage todoStorage;

    private int mPosition;

    protected TodoItemHelper mHelper;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected CompositeSubscription mSubscriptions;
    protected TodoItemRepository mItemRepository;

    private RecyclerListAdapter.ListClickListener onClick =
            new RecyclerListAdapter.ListClickListener() {

                @Override
                public void onItemClick(View view) {
                    int position = mRecyclerView.getChildLayoutPosition(view);
                    TodoItem item = mAdapter.getItem(position);

                    Bundle arguments = new Bundle();
                    arguments.putCharSequence("title", item.getTitle());
                    arguments.putCharSequence("description", item.getDescription());
                    arguments.putInt("position", position);
                    arguments.putBoolean("edit", true);
                    arguments.putBoolean("pinned", item.isPinned());
                    arguments.putBoolean("completed", item.isCompleted());

                    mPosition = position;

                    /*Intent intent = new Intent(ListFragment.this.getActivity(), EditItemActivity.class);
                    intent.putExtras(arguments);
                    startActivity(intent);*/
                    //return;

                    ((ListActivity) getActivity()).showEditDialog(arguments);
                }

            };

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

        mAdapter = new RecyclerListAdapter(this, restoredList);
        mAdapter.setUndoOn(true);
        mAdapter.setStartDragListener(this);
        mAdapter.setListChangeListener(this);
        mAdapter.setListClickListener(onClick);

        initRecyclerView();
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mAdapter.setListClickListener(RecyclerListAdapter.ListClickListener.Placeholder);
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
        todoStorage.saveTodos(mAdapter.getItems());

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
            newPosition = mAdapter.getFirstCompletedPosition();
        } else {
            newPosition = item.isPinned() ? 0 : mAdapter.getFirstUnpinnedPosition();
        }

        if (newPosition == RecyclerView.NO_POSITION) {
            newPosition = 0;
        }

        mAdapter.addItem(newPosition, item);
        mRecyclerView.smoothScrollToPosition(newPosition);
    }

    public void removeItem(int position, boolean undo) {
        if (undo) {
            mAdapter.onItemDismiss(position);
        } else {
            mAdapter.remove(position);
        }
    }

    protected void initRecyclerView() {
        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(listDividerHeight));

        // Set touch helper
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, getActivity());
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
