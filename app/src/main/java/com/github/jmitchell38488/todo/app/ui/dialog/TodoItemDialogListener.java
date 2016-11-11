package com.github.jmitchell38488.todo.app.ui.dialog;

import android.support.v4.app.DialogFragment;

public interface TodoItemDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}