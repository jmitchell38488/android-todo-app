package com.github.jmitchell38488.todo.app.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by justinmitchell on 10/11/2016.
 */

public class RobotoMediumTextView extends TextView {

    public RobotoMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/roboto/Roboto-Medium.ttf"));
    }

}
