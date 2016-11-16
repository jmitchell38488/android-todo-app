package com.github.jmitchell38488.todo.app.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.fragment.EditItemFragment;

public class EditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putAll(getIntent().getExtras());

            EditItemFragment fragment = new EditItemFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_edit_item, fragment)
                    .commit();
        }
    }
}
