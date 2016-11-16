package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Created by justinmitchell on 16/11/2016.
 */

public class TodoItemEditHolder {

    View mView;
    Context mContext;

    TextView titleView;
    TextView descView;

    public TodoItemEditHolder(View view, Context context) {
        mView = view;
        mContext = context;
    }
}
