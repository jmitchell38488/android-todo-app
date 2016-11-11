package com.github.jmitchell38488.todo.app.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

public class DeleteTodoItemDialog extends DialogFragment {

    TodoItemDialogListener mListener;
    public String title;
    public boolean delete;
    public int position;

    public DeleteTodoItemDialog() {
        title = "";
        delete = false;
        position = -1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit, null);

        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        StringBuilder strbuilder = new StringBuilder();
        if (arguments != null) {
            title = (String) arguments.getCharSequence("title", "");
            delete = arguments.getBoolean("delete", false);
            position = arguments.getInt("position", -1);

            if (!delete) {
                return null;
            }

            strbuilder.append("Are you sure you want to delete");

            if (!TextUtils.isEmpty(title)) {
                strbuilder.append(" " + title);
            } else {
                strbuilder.append(" this item");
            }

            strbuilder.append("?");
        }

        builder.setMessage(strbuilder.toString())
                .setPositiveButton(R.string.dialog_delete_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DeleteTodoItemDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_delete_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DeleteTodoItemDialog.this);
                    }
                });

        return builder.create();
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
