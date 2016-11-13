package com.github.jmitchell38488.todo.app.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightTextView;
import com.github.jmitchell38488.todo.app.ui.view.SignPainterRegularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by justinmitchell on 11/11/2016.
 */

public class EditTodoItemDialog extends DialogFragment {

    @BindView(R.id.edit_dialog_logo_main) SignPainterRegularTextView logoView;
    @BindView(R.id.edit_dialog_title) public RobotoLightEditText titleView;
    @BindView(R.id.edit_dialog_description) public RobotoLightEditText descriptionView;
    @BindView(R.id.edit_dialog_pinned) public Switch pinnedSwitch;
    @BindView(R.id.edit_dialog_pinned_label) public RobotoLightTextView pinnedLabel;

    TodoItemDialogListener mListener;
    public String title;
    public String description;
    public int position;
    public boolean edit;
    public boolean pinned;

    public EditTodoItemDialog() {
        title = "";
        description = "";
        position = -1;
        edit = false;
        pinned = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit, null);

        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();

        pinnedLabel.setText(getContext().getString(R.string.dialog_edit_pin_false));

        if (arguments != null) {
            title = (String) arguments.getCharSequence("title", "");
            description = (String) arguments.getCharSequence("description", "");
            position = arguments.getInt("position", -1);
            edit = arguments.getBoolean("edit", false);
            pinned = arguments.getBoolean("pinned", false);

            if (pinned) {
                pinnedLabel.setText(getContext().getString(R.string.dialog_edit_pin_true));
            }

            pinnedSwitch.setChecked(pinned);

            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
            }

            if (!TextUtils.isEmpty(description)) {
                descriptionView.setText(description);
            }

            if (!edit) {
                logoView.setText(getActivity().getString(R.string.action_create));
            }
        }

        pinnedSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pinned = isChecked;
                if (pinned) {
                    pinnedLabel.setText(getContext().getString(R.string.dialog_edit_pin_true));
                } else {
                    pinnedLabel.setText(getContext().getString(R.string.dialog_edit_pin_false));
                }
            }
        });

        builder.setView(rootView)
                .setPositiveButton(R.string.dialog_edit_button_positive, null)
                .setNegativeButton(R.string.dialog_edit_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(EditTodoItemDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.onDialogPositiveClick(EditTodoItemDialog.this);
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the EditTodoItemDialogListener so we can send events to the host
            mListener = (TodoItemDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
