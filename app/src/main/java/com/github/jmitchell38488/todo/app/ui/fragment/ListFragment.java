package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.helper.SimpleItemTouchHelperCallback;

import java.util.List;

import javax.inject.Inject;

public class ListFragment extends Fragment implements OnStartDragListener,
        RecyclerListAdapter.ListChangeListener {

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Inject TodoStorage todoStorage;

    private static String POSITION_KEY = "position";
    private int mPosition;


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
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TodoApp.getComponent(getActivity()).inject(this);

        // Set adapter
        mAdapter = new RecyclerListAdapter(getActivity(), todoStorage, this, this);

        // Set view
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set touch helper
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onOrderChange(List<TodoItem> oldList, List<TodoItem> newList) {
        // Set the new sorted list
        ((RecyclerListAdapter) mRecyclerView.getAdapter()).setListData(newList);

        // Notify data changed, invalidate and reset the layout manager
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.invalidate();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
