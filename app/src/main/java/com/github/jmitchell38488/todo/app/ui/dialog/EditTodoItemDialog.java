package com.github.jmitchell38488.todo.app.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;
import com.github.jmitchell38488.todo.app.ui.view.SignPainterRegularTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by justinmitchell on 11/11/2016.
 */

public class EditTodoItemDialog extends android.support.v4.app.DialogFragment {

    @BindView(R.id.edit_dialog_logo_main) SignPainterRegularTextView logoView;
    @BindView(R.id.edit_dialog_title)
    RobotoLightEditText titleView;
    @BindView(R.id.edit_dialog_description)
    RobotoLightEditText descriptionView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit, null);

        ButterKnife.bind(this, rootView);

        String title = "";
        String description = "";
        boolean edit = false;

        Bundle arguments = getArguments();
        if (arguments != null) {
            title = (String) arguments.getCharSequence("title");
            description = (String) arguments.getCharSequence("description");
            edit = arguments.getBoolean("edit", false);

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
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.dialog_edit_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        return builder.create();
    }
}
