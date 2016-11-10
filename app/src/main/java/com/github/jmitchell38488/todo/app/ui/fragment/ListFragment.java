package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoAdapter;
import com.github.jmitchell38488.todo.app.data.TodoExpandableAdapter;
import com.github.jmitchell38488.todo.app.data.TodoSectionAdapter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.view.holder.ListItemViewHolder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @Inject TodoStorage todoStorage;
    //@Inject TodoAdapter mAdapter;
    //@Inject TodoExpandableAdapter mAdapter;
    @Inject TodoSectionAdapter mAdapter;
    //@BindView(R.id.list_container) ExpandableListView mListView;
    @BindView(R.id.list_container) ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TodoApp.getComponent(getActivity()).inject(this);

        mListView.setAdapter(mAdapter);
        /*mListView.setGroupIndicator(null);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Doing nothing
                return true;
            }
        });*/
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
}
