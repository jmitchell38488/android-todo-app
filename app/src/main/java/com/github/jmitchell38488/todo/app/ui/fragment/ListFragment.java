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
import com.github.jmitchell38488.todo.app.data.adapter.RecyclerListAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.helper.OnStartDragListener;
import com.github.jmitchell38488.todo.app.ui.helper.SimpleItemTouchHelperCallback;

import javax.inject.Inject;

public class ListFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter mAdapter;

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

        RecyclerListAdapter adapter = new RecyclerListAdapter(getActivity(), todoStorage, this);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
