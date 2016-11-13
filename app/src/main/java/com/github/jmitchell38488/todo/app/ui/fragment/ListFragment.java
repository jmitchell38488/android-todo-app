package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoAdapter;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoItemSorter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @Inject TodoStorage todoStorage;
    private TodoAdapter mAdapter;
    @BindView(R.id.list_container) ListView mListView;
    @BindView(R.id.empty_list) TextView mEmptyView;

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
        TodoApp.getComponent(getActivity()).inject(this);

        List<TodoItem> items = todoStorage.getTodos();
        if (items == null) {
            items = new ArrayList<>();
        }

        // Sort the list so that pinned are showed at the top and completed at the bottom
        if (items.size() > 0) {
            TodoItemSorter.sort(items);
        }

        mAdapter = new TodoAdapter(getActivity(), getActivity().getApplicationContext(), todoStorage, items);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem item = (TodoItem) mListView.getAdapter().getItem(position);
                Bundle arguments = new Bundle();
                arguments.putCharSequence("title", item.getTitle());
                arguments.putCharSequence("description", item.getDescription());
                arguments.putInt("position", position);
                arguments.putBoolean("edit", true);
                arguments.putBoolean("pinned", item.isPinned());
                arguments.putBoolean("completed", item.isCompleted());

                mPosition = position;

                ((ListActivity) getActivity()).showEditDialog(arguments);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            mPosition = savedInstanceState.getInt(POSITION_KEY);
        }

        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public TodoAdapter getTodoAdapter() {
        return mAdapter;
    }

    public ListView getListView() {
        return mListView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            mPosition = getItemPositionFromYOffset();
        }

        outState.putInt(POSITION_KEY, mPosition);

        super.onSaveInstanceState(outState);
    }

    private int getItemPositionFromYOffset() {
        int[] heights = new int[mListView.getCount()];

        View v = mListView.getChildAt(0);
        if (v == null) {
            return 0;
        }

        int firstVisible = mListView.getFirstVisiblePosition();
        if (firstVisible < mListView.getCount() && heights[firstVisible + 1] == 0) {
            heights[firstVisible + 1] += heights[firstVisible] + v.getHeight();
        }

        return v.getTop() + heights[firstVisible];
    }

}
