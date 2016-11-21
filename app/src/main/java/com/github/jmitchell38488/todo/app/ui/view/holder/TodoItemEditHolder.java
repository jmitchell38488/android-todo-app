package com.github.jmitchell38488.todo.app.ui.view.holder;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

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

    @BindView(R.id.edit_dialog_title) public RobotoLightEditText titleView;
    @BindView(R.id.edit_dialog_description) public RobotoLightEditText descriptionView;
    @BindView(R.id.edit_dialog_pinned) public Switch pinnedSwitch;
    @BindView(R.id.edit_dialog_completed) public Switch completedSwitch;
    @BindView(R.id.edit_dialog_locked) public Switch lockedSwitch;
    @BindView(R.id.edit_dialog_pinned_label) public TextView pinnedLabel;

    public TodoItemEditHolder(View view, Context context,@Nullable TodoItem item) {
        mView = view;
        mContext = context;
        mItem = item;

        ButterKnife.bind(this, mView);

        updateView();
    }

    public void updateView() {
        pinnedSwitch.setChecked(mItem.isPinned());
        pinnedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setPinned(isChecked);

            if (isChecked) {
                pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_true));
            } else {
                pinnedLabel.setText(mContext.getString(R.string.dialog_edit_pin_false));
            }
        });

        completedSwitch.setChecked(mItem.isCompleted());
        completedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setCompleted(isChecked);
        });

        lockedSwitch.setChecked(mItem.isLocked());
        lockedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mItem.setLocked(isChecked);
        });

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
