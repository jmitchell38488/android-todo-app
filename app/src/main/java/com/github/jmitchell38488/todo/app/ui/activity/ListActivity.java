package com.github.jmitchell38488.todo.app.ui.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

public class ListActivity extends AppCompatActivity implements TodoItemDialogListener {

    @Inject TodoStorage todoStorage;
    @Inject SharedPreferences prefs;
    @BindView(R.id.fab) FloatingActionButton fab;

    private ActionMenuView amvMenu;

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle arguments = new Bundle();
                arguments.putInt("position", -1);
                arguments.putBoolean("edit", false);
                arguments.putBoolean("pinned", false);

                showEditDialog(arguments);
            }
        });
    }

    private void setMenuBar() {
        Toolbar t = (Toolbar) findViewById(R.id.list_toolbar);
        amvMenu = (ActionMenuView) t.findViewById(R.id.amvMenu);

        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // use amvMenu here
        inflater.inflate(R.menu.list, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Do your actions here
        return true;
    }

    public void showEditDialog(Bundle arguments) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EditTodoItemDialog dialogFragment = new EditTodoItemDialog();
        dialogFragment.setArguments(arguments);
        dialogFragment.show(fragmentManager, "edit_dialog");
    }

    private void deleteTodoItem(DialogFragment dialog) {
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) todoStorage.getTodos();
        int position = ((DeleteTodoItemDialog) dialog).position;

        TodoItem item = mFragment.getTodoAdapter().getItem(position);
        ArrayList<TodoItem> newlist = new ArrayList<>();

        for (TodoItem ditem : list) {
            if (ditem.getId() != item.getId()) {
                newlist.add(ditem);
            }
        }

        mFragment.getTodoAdapter().remove(item);

        reorderAndSaveList(newlist);
    }

    private void saveNewTodoItem(DialogFragment dialog) {
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) todoStorage.getTodos();
        String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
        String description = ((EditTodoItemDialog) dialog).descriptionView.getText().toString();
        boolean pinned = ((EditTodoItemDialog) dialog).pinned;

        int counter = prefs.getInt(COUNTER, 0);
        counter++;
        prefs.edit().putInt(COUNTER, counter).commit();

        // Store the new TodoItem
        TodoItem item = new TodoItem(counter, title, description, 0, false, pinned);

        if (list.isEmpty()) {
            list.add(item);
        } else {
            list.add(0, item);
        }

        mFragment.getTodoAdapter().add(item);
        reorderAndSaveList(list);
    }

    private void saveEditedTodoItem(DialogFragment dialog) {
        ArrayList<TodoItem> list = (ArrayList<TodoItem>) todoStorage.getTodos();
        String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
        String description = ((EditTodoItemDialog) dialog).descriptionView.getText().toString();
        int position = ((EditTodoItemDialog) dialog).position;
        boolean pinned = ((EditTodoItemDialog) dialog).pinned;

        TodoItem item = mFragment.getTodoAdapter().getItem(position);
        item.setTitle(title);
        item.setDescription(description);
        item.setPinned(pinned);

        for (TodoItem ditem : list) {
            if (ditem.getId() == item.getId()) {
                ditem.setTitle(title);
                ditem.setDescription(description);
                ditem.setPinned(pinned);
            }
        }

        reorderAndSaveList(list);
    }

    private void reorderAndSaveList(ArrayList<TodoItem> list) {
        // Make sure that we reorder
        TodoItemSorter.sort(list);
        TodoItemSorter.sortAdapter(mFragment.getTodoAdapter());

        todoStorage.saveTodos(list);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Delete a TodoItem
        if (dialog instanceof DeleteTodoItemDialog) {
            deleteTodoItem(dialog);

        // Create a new TodoItem or edit an existing TodoItem
        } else if (dialog instanceof EditTodoItemDialog) {
            String title = ((EditTodoItemDialog) dialog).titleView.getText().toString();
            int position = ((EditTodoItemDialog) dialog).position;
            boolean edit = ((EditTodoItemDialog) dialog).edit;

            // Do nothing if the user didn't enter a title
            if (TextUtils.isEmpty(title)) {
                Toast toast = Toast.makeText(this, getString(R.string.empty_title), Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if (position < 0 || !edit) {
                saveNewTodoItem(dialog);
            } else if (edit) {
                saveEditedTodoItem(dialog);
            }
        }

        dialog.dismiss();
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
