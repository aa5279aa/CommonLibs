package com.common.ui.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class MyEditText extends androidx.appcompat.widget.AppCompatEditText {

    private boolean isSelected2 = false;

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelected2(boolean selected) {
        if (isSelected2 == selected) {
            return;
        }
        isSelected2 = selected;
        setSelected(isSelected2);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected != isSelected2) {
            return;
        }
        super.setSelected(selected);
    }


}
