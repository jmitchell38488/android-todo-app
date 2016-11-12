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
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements TodoItemDialogListener {

    @Inject TodoStorage todoStorage;
    @Inject SharedPreferences prefs;
    @BindView(R.id.fab) FloatingActionButton fab;

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
        } else {
            mFragment = (ListFragment) getFragmentManager().getFragment(savedInstanceState, "mFragment");
        }

        ButterKnife.bind(this);

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

        // Delete a TodoItem
        if (dialog instanceof DeleteTodoItemDialog) {
            int position = ((DeleteTodoItemDialog) dialog).position;

            TodoItem item = mFragment.getTodoAdapter().getItem(position);
            ArrayList<TodoItem> newlist = new ArrayList<>();

            for (TodoItem ditem : list) {
                if (ditem.getId() != item.getId()) {
                    newlist.add(ditem);
                }
            }

            list = newlist;
            mFragment.getTodoAdapter().remove(item);

        // Create a new TodoItem or edit an existing TodoItem
        } else if (dialog instanceof EditTodoItemDialog) {
            String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
            String description = ((EditTodoItemDialog) dialog).descriptionView.getText().toString();
            boolean edit = ((EditTodoItemDialog) dialog).edit;
            int position = ((EditTodoItemDialog) dialog).position;

            int counter = prefs.getInt(COUNTER, 0);
            counter++;
            prefs.edit().putInt(COUNTER, counter).commit();

            final boolean newItem = false;
            final int newItemId = (position < 0 || !edit) ? counter : -1;

            if (position < 0 || !edit) {
                // Store the new TodoItem
                TodoItem item = new TodoItem(counter, title, description, 0, false);

                if (list.isEmpty()) {
                    list.add(item);
                } else {
                    list.add(0, item);
                }

                mFragment.getTodoAdapter().add(item);

                mFragment.getTodoAdapter().sort(new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem lhs, TodoItem rhs) {
                        return (lhs.getId() == newItemId ? -1 : newItemId > lhs.getId() ? 1 : 0);
                    }
                });

            } else if (edit) {
                TodoItem item = mFragment.getTodoAdapter().getItem(position);
                item.setTitle(title);
                item.setDescription(description);


                for (TodoItem ditem : list) {
                    if (ditem.getId() == item.getId()) {
                        ditem.setTitle(title);
                        ditem.setDescription(description);
                    }
                }
            }
        }

        // Make sure that we reorder, put completed last
        ItemUtility.reorderTodoItemList(list);
        todoStorage.saveTodos(list);

        mFragment.getTodoAdapter().notifyDataSetChanged();
        mFragment.getTodoAdapter().sort(new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem lhs, TodoItem rhs) {
                return (lhs.isCompleted() == rhs.isCompleted() ? 0 : rhs.isCompleted() ? -1 : 1 );
            }
        });

        mFragment.getTodoAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

}
