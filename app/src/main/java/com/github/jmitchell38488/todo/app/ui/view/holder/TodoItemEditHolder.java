package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.model.TodoItem;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightTextView;
import com.github.jmitchell38488.todo.app.ui.view.SignPainterRegularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoItemEditHolder {

    View mView;
    Context mContext;
    TodoItem mItem;

    @BindView(R.id.edit_dialog_title) RobotoLightEditText titleView;
    @BindView(R.id.edit_dialog_description) RobotoLightEditText descriptionView;
    @BindView(R.id.edit_dialog_pinned) Switch pinnedSwitch;
    @BindView(R.id.edit_dialog_pinned_label) RobotoLightTextView pinnedLabel;

    public TodoItemEditHolder(View view, Context context,@Nullable TodoItem item) {
        mView = view;
        mContext = context;
        mItem = item;

        ButterKnife.bind(this, mView);

        updateView();
    }

    public void updateView() {
        pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_false));

        pinnedSwitch.setChecked(false);
        pinnedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mItem.isPinned()) {
                pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_true));
            } else {
                pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_false));
            }
        });

        // Item can be set to null if this is a new record
        if (mItem == null) {
            return;
        }

        if (mItem.isPinned()) {
            pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_true));
        }

        if (!TextUtils.isEmpty(mItem.getTitle())) {
            titleView.setText(mItem.getTitle());
        }

        if (!TextUtils.isEmpty(mItem.getDescription())) {
            descriptionView.setText(mItem.getDescription());
        }
    }

}
