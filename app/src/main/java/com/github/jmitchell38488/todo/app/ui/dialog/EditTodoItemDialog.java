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

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;
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

    EditTodoItemDialogListener mListener;
    public String title;
    public String description;
    public boolean edit;
    public int position;

    public interface EditTodoItemDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public EditTodoItemDialog() {
        title = "";
        description = "";
        edit = false;
        position = -1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit, null);

        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            title = (String) arguments.getCharSequence("title", "");
            description = (String) arguments.getCharSequence("description", "");
            edit = arguments.getBoolean("edit", false);
            position = arguments.getInt("position", -1);

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

        builder.setView(rootView)
                .setPositiveButton(R.string.dialog_edit_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(EditTodoItemDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_edit_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(EditTodoItemDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the EditTodoItemDialogListener so we can send events to the host
            mListener = (EditTodoItemDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
