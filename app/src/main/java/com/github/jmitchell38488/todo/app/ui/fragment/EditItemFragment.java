package com.github.jmitchell38488.todo.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.view.holder.TodoItemEditHolder;

import javax.inject.Inject;

public class EditItemFragment extends Fragment {

    @Inject TodoStorage todoStorage;

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
        Bundle arguments = getArguments();

        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);
        TodoItemEditHolder viewHolder = new TodoItemEditHolder(view, getActivity());
        view.setTag(viewHolder);

        return view;
    }

}
