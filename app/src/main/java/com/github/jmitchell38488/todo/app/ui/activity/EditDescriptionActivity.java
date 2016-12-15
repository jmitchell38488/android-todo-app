package com.github.jmitchell38488.todo.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.ui.fragment.EditDescriptionFragment;

public class EditDescriptionActivity extends BaseActivity {

    private static final String TAG_FRAGMENT = "editDescriptionActivityFragment";

    private EditDescriptionFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_item);

        if (savedInstanceState == null) {
            String desc = getIntent().getExtras().getString(Parcelable.KEY_DESCRIPTION_TEXT);

            Bundle args = new Bundle();
            args.putString(Parcelable.KEY_DESCRIPTION_TEXT, desc);

            mFragment = new EditDescriptionFragment();
            mFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_edit_container, mFragment, TAG_FRAGMENT)
                    .commit();
        } else {
            mFragment = (EditDescriptionFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        }

        // Enable buttons
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0.1f);

            // Change text
            ((TextView) mToolbar.findViewById(R.id.logo_main)).setText(getString(R.string.action_edit_description));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();

                return true;
            }

            case R.id.action_save: {
                Bundle args = new Bundle();
                args.putString(Parcelable.KEY_DESCRIPTION_TEXT, mFragment.getDescriptionText());

                Intent intent = new Intent();
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();

                return false;
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
