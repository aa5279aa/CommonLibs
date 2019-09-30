package com.common.ui.bar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.common.util.DeviceUtil;


/**
 *  @author lxl
 */
public class CommonSettingSwitchBar extends CommonInfoBar {

    private CompoundButton mSwitchBar;

    public CommonSettingSwitchBar(Context context) {
        this(context, null);
    }

    public CommonSettingSwitchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setViewAttr();
    }

    public void setViewAttr() {
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        mValueText.setTypeface(Typeface.DEFAULT_BOLD);
        mValueText.setLayoutParams(labelParams);
        mValueText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        mValueText.setLineSpacing(3.4f, 1.0f);
        mValueText.setPadding(0, 0, DeviceUtil.getPixelFromDip(getContext(),10.0f), 0);

        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        valueParams.gravity = Gravity.CENTER_VERTICAL;
        mSwitchBar = new CommonSimpleSwitch(getContext());
        addView(mSwitchBar, valueParams);

        setClickable(false);
        setFocusable(false);
    }


    /**
     * @param listener 通知对象
     * @see CompoundButton.OnCheckedChangeListener
     */
    public void setOnCheckdChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mSwitchBar.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置switcher的有效化
     *
     * @param enable
     */
    public void setSwitchEnable(boolean enable) {
        if (mSwitchBar != null) {
            mSwitchBar.setFocusable(enable);
            mSwitchBar.setClickable(enable);
            mSwitchBar.setEnabled(enable);
        }
    }

    /**
     * 设置SwitchBar的On/Off状态
     *
     * @param checked true - On状态， false - Off状态
     */
    public void setSwitchChecked(boolean checked) {
        if (mSwitchBar != null) {
            mSwitchBar.setChecked(checked);
        }
    }

    /**
     * 获取SwitchBar的On/Off状态
     *
     * @return true - On状态， false - Off状态
     */
    public boolean isSwitchChecked() {
        if (mSwitchBar != null) {
            return mSwitchBar.isChecked();
        }
        return false;
    }

    /**
     * 设置Switch的On文本
     *
     * @param textOn On显示的文本
     */
    @Deprecated
    public void setSwitchTextOn(CharSequence textOn) {
        if (mSwitchBar != null) {
            // mSwitchBar.setTextOn(textOn);
        }
    }

    /**
     * 设置Switch的Off文本
     */
    @Deprecated
    public void setSwitchTextOff(CharSequence textOff) {
        if (mSwitchBar != null) {
            // mSwitchBar.setTextOff(textOff);
        }
    }


    public CompoundButton getSwitchBar() {
        return mSwitchBar;
    }
}
