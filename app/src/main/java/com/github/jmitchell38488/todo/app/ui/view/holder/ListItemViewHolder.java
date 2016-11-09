package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.view.View;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by justinmitchell on 10/11/2016.
 */

public class ListItemViewHolder {

    @BindView(R.id.list_fragment_title) TextView titleView;

    public ListItemViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
