package com.github.jmitchell38488.todo.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.ui.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;

import javax.inject.Inject;

public class ListActivity extends AppCompatActivity {

    @Inject TodoStorage todoStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TodoApp.getComponent(this).inject(this);

        if (savedInstanceState == null) {
            ListFragment fragment = new ListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, fragment)
                    .commit();
        }
    }
}
