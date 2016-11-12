package com.github.jmitchell38488.todo.app.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoAdapter;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.activity.ListActivity;
import com.github.jmitchell38488.todo.app.ui.dialog.TodoItemDialogListener;
import com.github.jmitchell38488.todo.app.util.ItemUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListFragment extends Fragment {

    @Inject TodoStorage todoStorage;
    private TodoAdapter mAdapter;
    @BindView(R.id.list_container) ListView mListView;
    @BindView(R.id.empty_list) TextView mEmptyView;

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
                arguments.putBoolean("edit", true);
                arguments.putInt("position", position);

                ((ListActivity) getActivity()).showEditDialog(arguments);
            }
        });
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

}
