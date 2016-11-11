package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.view.View;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListItemViewHolder extends ViewHolder {

    @BindView(R.id.list_fragment_title) public TextView titleView;
    @BindView(R.id.list_fragment_description) public TextView dateView;

    public ListItemViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
