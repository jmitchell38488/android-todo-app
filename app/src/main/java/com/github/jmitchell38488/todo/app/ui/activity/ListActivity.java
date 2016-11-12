package com.github.jmitchell38488.todo.app.ui.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.dialog.DeleteTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.TodoItemDialogListener;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.github.jmitchell38488.todo.app.util.ItemUtility;

import java.util.ArrayList;

import javax.inject.Inject;

public class ListActivity extends AppCompatActivity implements TodoItemDialogListener {

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
                Bundle arguments = new Bundle();
                arguments.putBoolean("edit", false);
                arguments.putInt("position", -1);

                showEditDialog(arguments);
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
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) todoStorage.getTodos();
        int position = ListView.INVALID_POSITION;

        if (dialog instanceof DeleteTodoItemDialog) {
            position = ((DeleteTodoItemDialog) dialog).position;

            int id = mFragment.getTodoAdapter().getItem(position).getId();
            ArrayList<TodoItem> newlist = new ArrayList<>();

            for (TodoItem ditem : list) {
                if (ditem.getId() != id) {
                    newlist.add(ditem);
                }
            }

            list = newlist;
        } else if (dialog instanceof EditTodoItemDialog) {
            String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
            String description = ((EditTodoItemDialog) dialog).descriptionView.getText().toString();
            boolean edit = ((EditTodoItemDialog) dialog).edit;
            position = ((EditTodoItemDialog) dialog).position;

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
        }

        // Make sure that we reorder, put completed last
        ItemUtility.reorderTodoItemList(list);
        todoStorage.saveTodos(list);

        // Refresh the adaptor
        mFragment.getTodoAdapter().clear();
        mFragment.getTodoAdapter().addAll(list);
        mFragment.getTodoAdapter().notifyDataSetChanged();

        if (position != ListView.INVALID_POSITION && position <= mFragment.getTodoAdapter().getCount()) {
            mFragment.getListView().smoothScrollToPosition(position);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

}
