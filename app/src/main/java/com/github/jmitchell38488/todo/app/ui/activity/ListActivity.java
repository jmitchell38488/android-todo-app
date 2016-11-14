package com.github.jmitchell38488.todo.app.ui.activity;


import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.adapter.TodoItemSorter;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.dialog.DeleteTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.TodoItemDialogListener;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {

    @Inject TodoStorage todoStorage;
    @Inject SharedPreferences prefs;
    @BindView(R.id.fab) FloatingActionButton fab;

    private ListFragment mFragment;

    private final static String COUNTER = "counter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setMenuBar();

        TodoApp.getComponent(this).inject(this);

        if (savedInstanceState == null) {
            mFragment = new ListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, mFragment)
                    .commit();
        } else {
            mFragment = (ListFragment) getFragmentManager().getFragment(savedInstanceState, "mFragment");
        }

        ButterKnife.bind(this);
    }

    private void setMenuBar() {
        Toolbar t = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

}
