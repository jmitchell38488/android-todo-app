package com.github.jmitchell38488.todo.app.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;

import butterknife.BindView;

public class EditDescriptionFragment extends BaseFragment {

    private static final String LOG_TAG = EditDescriptionFragment.class.getSimpleName();

    private boolean canConvertFontSize = false;
    private View mView;

    @BindView(R.id.item_edit_description) EditText descriptionView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_edit_description, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TypedValue descSmallValue = new TypedValue();
        TypedValue descLargeValue = new TypedValue();

        getResources().getValue(R.dimen.EditThemeContainer_Input_textSize_float, descSmallValue, true);
        getResources().getValue(R.dimen.EditThemeContainer_Input_textSizeLarge_float, descLargeValue, true);

        float descSmall = descSmallValue.getFloat();
        float descLarge = descLargeValue.getFloat();

        Bundle arguments = getArguments();
        String desc = arguments.getString(Parcelable.KEY_DESCRIPTION_TEXT);
        int length = desc.replace(" ", "").replace("\n","").replace("\r","").length();

        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, length < 85 ? descLarge : descSmall);
        descriptionView.setText(desc);

        // Setting the cursor to the end of the text
        if (desc != null && desc.length() > 0) {
            descriptionView.setSelection(desc.length());
        }

        descriptionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Dynamically resize the font size based on text entry, like Facebook
                s.toString();
                String seq = s.toString().replace(" ", "").replace("\n","").replace("\r","");

                if (seq.length() >= 85) {
                    if (descriptionView.getTextSize() != descSmall) {
                        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descSmall);
                    }
                } else {
                    if (descriptionView.getTextSize() != descLarge) {
                        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descLarge);
                    }
                }
            }
        });
    }

    public String getDescriptionText() {
        return descriptionView.getText().toString();
    }

}
