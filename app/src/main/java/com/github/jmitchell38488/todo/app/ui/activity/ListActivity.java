package com.github.jmitchell38488.todo.app.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.TodoStorage;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;

import javax.inject.Inject;

public class ListActivity extends ActionBarActivity {

    private ActionMenuView amvMenu;

    @Inject TodoStorage todoStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setMenuBar();

        TodoApp.getComponent(this).inject(this);

        if (savedInstanceState == null) {
            ListFragment fragment = new ListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, fragment)
                    .commit();
        }
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
}
