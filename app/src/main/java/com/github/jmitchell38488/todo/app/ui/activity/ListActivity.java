package com.github.jmitchell38488.todo.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.util.SortedList;
import android.view.Menu;
import android.view.MenuItem;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.TodoApp;
import com.github.jmitchell38488.todo.app.data.Filter;
import com.github.jmitchell38488.todo.app.data.Sort;
import com.github.jmitchell38488.todo.app.ui.fragment.ListFragment;
import com.github.jmitchell38488.todo.app.ui.fragment.SortedListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends BaseActivity implements ListFragment.ActivityListClickListener {

    private static final String STATE_MODE = "state_mode";
    private static final String ITEMS_FRAGMENT_TAG = "fragment_items";

    @BindView(R.id.fab) FloatingActionButton fab;
    private SortedListFragment mFragment;
    private String mMode = null;
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        TodoApp.getComponent(this).inject(this);

        if (savedInstanceState == null) {
            mFragment = SortedListFragment.newInstance(Sort.DEFAULT, Filter.DEFAULT);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, mFragment)
                    .commit();
        } else {
            mFragment = (SortedListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
            mFragment.setActivityListClickListener(this);
        }

        ButterKnife.bind(this);

        fab.setOnClickListener(view -> {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ListFragment.ActivityListClickListener.ARG_TODOITEM, null);

            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtras(arguments);
            startActivity(intent);
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_MODE, mMode);
        getSupportFragmentManager().putFragment(outState, "mFragment", mFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Bundle arguments) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtras(arguments);
        startActivity(intent);
    }
}
