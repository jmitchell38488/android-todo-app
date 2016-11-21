package com.github.jmitchell38488.todo.app.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.fragment.EditItemFragment;

public class EditItemActivity extends BaseActivity {

    EditItemFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putAll(getIntent().getExtras());

            mFragment = new EditItemFragment();
            mFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_list_container, mFragment)
                    .commit();
        }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
