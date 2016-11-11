package com.github.jmitchell38488.todo.app.ui.activity;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoItem;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog;
import com.github.jmitchell38488.todo.app.ui.dialog.EditTodoItemDialog.EditTodoItemDialogListener;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;

import javax.inject.Inject;

public class ListActivity extends AppCompatActivity implements EditTodoItemDialogListener {

    @Inject TodoStorage todoStorage;
    private ListFragment mFragment;

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

        if (position < 0 || !edit) {
            TodoItem item = new TodoItem(title, description, 0, false);
            mFragment.getTodoAdapter().add(item);
        } else if (edit) {
            TodoItem item = mFragment.getTodoAdapter().getItem(position);
            item.setTitle(title);
            item.setDescription(description);
        }

        mFragment.getTodoAdapter().notifyDataSetChanged();


    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

}
