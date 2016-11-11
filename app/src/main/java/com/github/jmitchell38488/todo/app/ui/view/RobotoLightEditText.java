package com.github.jmitchell38488.todo.app.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class RobotoLightEditText extends EditText {

    public RobotoLightEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto/Roboto-Light.ttf"));
    }

}
