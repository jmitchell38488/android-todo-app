package com.github.jmitchell38488.todo.app.ui.fragment;

import android.content.res.Configuration;
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
import android.widget.ListView;

import com.github.jmitchell38488.todo.app.R;
import com.github.jmitchell38488.todo.app.data.Parcelable;
import com.github.jmitchell38488.todo.app.ui.view.RobotoLightEditText;

import java.util.ArrayList;

import butterknife.BindView;

public class EditDescriptionFragment extends BaseFragment implements TextWatcher {

    private static final String LOG_TAG = EditDescriptionFragment.class.getSimpleName();
    private static final String STATE_TEXT = "state_text";

    private boolean canConvertFontSize = false;
    private String descriptionText;
    private View mView;
    private int textLength = 0;
    private boolean mFocused = false;

    private float descSmall = 0f;
    private float descLarge = 0f;

    @BindView(R.id.item_edit_description)
    RobotoLightEditText descriptionView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        if (savedInstanceState != null) {
            descriptionText = savedInstanceState.getString(STATE_TEXT);
        } else {
            descriptionText = getArguments().getString(Parcelable.KEY_DESCRIPTION_TEXT);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            canConvertFontSize = true;

            TypedValue descSmallValue = new TypedValue();
            TypedValue descLargeValue = new TypedValue();

            getResources().getValue(R.dimen.EditThemeContainer_Input_textSize_float, descSmallValue, true);
            getResources().getValue(R.dimen.EditThemeContainer_Input_textSizeLarge_float, descLargeValue, true);

            descSmall = descSmallValue.getFloat();
            descLarge = descLargeValue.getFloat();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_TEXT, descriptionView.getText().toString());
        super.onSaveInstanceState(outState);
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

        if (descriptionText != null) {
            textLength = getDescriptionTextLength(descriptionText.toString());

            if (canConvertFontSize) {
                descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textLength < 85 ? descLarge : descSmall);
            }

            descriptionView.setText(descriptionText);
            descriptionView.setSelection(descriptionText.length());
        } else {
            descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descLarge);
        }

        descriptionView.addTextChangedListener(this);
        descriptionView.setOnEditTextImeBackListener((ctrl, text) -> {
            mFocused = false;
            descriptionView.setLines(textLength < 85 ? 18 : 20);
        });

        descriptionView.setOnLongClickListener(v -> {
            mFocused = true;

            if (mFocused) {
                descriptionView.setLines(textLength < 85 ? 8 : 10);
            }

            return true;
        });

        descriptionView.setOnClickListener(v -> {
            mFocused = true;

            if (mFocused) {
                descriptionView.setLines(textLength < 85 ? 8 : 10);
            }
        });

        descriptionView.setOnFocusChangeListener((v, focused) -> {
            if (descriptionView == null) {
                return;
            }

            mFocused = focused;

            if (mFocused) {
                descriptionView.setLines(textLength < 85 ? 8 : 10);
            } else {
                descriptionView.setLines(textLength < 85 ? 18 : 20);
            }
        });
    }

    public String getDescriptionText() {
        return descriptionView.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    private int getDescriptionTextLength(String s) {
        String seq = s.replace(" ", "").replace("\n","").replace("\r","");

        return seq.length();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!canConvertFontSize) {
            return;
        }

        // Dynamically resize the font size based on text entry, like Facebook
        textLength = getDescriptionTextLength(s.toString());

        if (textLength >= 85) {
            if (descriptionView.getTextSize() != descSmall) {
                descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descSmall);
            }
        } else {
            if (descriptionView.getTextSize() != descLarge) {
                descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, descLarge);
            }
        }
    }

}
