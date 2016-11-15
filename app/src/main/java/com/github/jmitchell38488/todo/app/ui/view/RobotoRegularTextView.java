package com.github.jmitchell38488.todo.app.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoRegularTextView extends TextView {

    public RobotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto/Roboto-Regular.ttf"));
    }

}
