package com.github.jmitchell38488.todo.app.ui.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog.EditTodoItemDialogListener;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ListActivity extends AppCompatActivity implements EditTodoItemDialogListener {

    @Inject TodoStorage todoStorage;
    @Inject SharedPreferences prefs;

    private ListFragment mFragment;

    private final static String COUNTER = "counter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TodoApp.getComponent(this).inject(this);

        if (savedInstanceState == null) {
            mFragment = new ListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, mFragment)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(null);
            }
        });
    }

    public void showEditDialog(Bundle arguments) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditTodoItemDialog dialogFragment = new EditTodoItemDialog();
        dialogFragment.setArguments(arguments);
        dialogFragment.show(fragmentManager, "edit_dialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
        String description = ((EditTodoItemDialog) dialog).descriptionView.getText().toString();
        boolean edit = ((EditTodoItemDialog) dialog).edit;
        int position = ((EditTodoItemDialog) dialog).position;

        ArrayList<TodoItem> list = (ArrayList<TodoItem>) todoStorage.getTodos();
        int counter = prefs.getInt(COUNTER, 0);
        counter++;
        prefs.edit().putInt(COUNTER, counter).commit();

        if (position < 0 || !edit) {
            // Store the new TodoItem
            TodoItem item = new TodoItem(counter, title, description, 0, false);

            if (list.isEmpty()) {
                list.add(item);
            } else {
                list.add(0, item);
            }
        } else if (edit) {
            int id = mFragment.getTodoAdapter().getItem(position).getId();

            for (TodoItem ditem : list) {
                if (ditem.getId() == id) {
                    ditem.setTitle(title);
                    ditem.setDescription(description);
                }
            }
        }

        todoStorage.saveTodos(list);

        // Refresh the adaptor
        mFragment.getTodoAdapter().clear();
        mFragment.getTodoAdapter().addAll(list);
        mFragment.getTodoAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

}
