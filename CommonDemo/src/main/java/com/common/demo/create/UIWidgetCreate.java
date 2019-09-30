package com.common.demo.create;

import android.content.Context;
import android.content.res.ColorStateList;

import com.common.demo.R;
import com.common.ui.widget.CommonButton;

/**
 * UI生成通用类
 */
public class UIWidgetCreate {


    public static CommonButton createCommonButton(Context context) {
        CommonButton button = new CommonButton(context);
        CommonButton.Model model = new CommonButton.Model();
        model.resourceBG = R.drawable.selector_common_button;

        int[] enalbe = new int[]{android.R.attr.state_enabled};
        int[] colors = new int[]{context.getResources().getColor(R.color.color_white1), context.getResources().getColor(R.color.color_bbbbb)};

        model.stateList = new ColorStateList(new int[][]{enalbe, new int[]{}}, colors);
        button.bindData(model);
        return button;
    }


    public static CommonButton createCommonButton2(Context context) {
        CommonButton button = new CommonButton(context);
        CommonButton.Model model = new CommonButton.Model();
        model.resourceBG = R.drawable.selector_common_button2;
        model.color = context.getResources().getColor(R.color.color_558efb);
        button.bindData(model);
        return button;
    }
}
