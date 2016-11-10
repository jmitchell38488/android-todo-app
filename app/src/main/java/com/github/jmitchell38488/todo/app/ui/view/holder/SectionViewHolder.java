package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.view.View;
import android.widget.TextView;

import com.github.jmitchell38488.todo.app.R;
import com.twotoasters.sectioncursoradapter.adapter.viewholder.ViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SectionViewHolder extends ViewHolder {

    @BindView(R.id.list_fragment_section_title) public TextView titleView;

    public SectionViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

}
