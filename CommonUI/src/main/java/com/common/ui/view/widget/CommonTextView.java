package com.common.ui.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.common.ui.R;

/**
 * Created by xiangleiliu on 2016/7/4.
 * 公共类未实现setCompoundDrawablePadding方法，补上
 */
public class CommonTextView extends TextView {

    Drawable compoundDrawable;
    Drawable selectCompoundDrawable;
    int direction;
    int width;
    int height;
    int padding;
    String text1 = "";
    int color1 = 0;
    int color2 = 0;

    public CommonTextView(Context context) {
        this(context, null);
    }

    public CommonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonTextView);

        compoundDrawable = a.getDrawable(R.styleable.CommonTextView_common_text_drawable_src);
        if (compoundDrawable != null) {
            direction = a.getInt(R.styleable.CommonTextView_common_text_drawable_direction, 0);
            if (direction < 0 || direction > 3) {
                direction = 0;
            }
            selectCompoundDrawable = a.getDrawable(R.styleable.CommonTextView_common_text_drawable_src_select);
            width = a.getDimensionPixelSize(R.styleable.CommonTextView_common_text_drawable_width, 0);
            height = a.getDimensionPixelSize(R.styleable.CommonTextView_common_text_drawable_height, 0);
            padding = a.getDimensionPixelSize(R.styleable.CommonTextView_common_text_drawable_padding, 0);
            setCompoundDrawablePadding(padding);
            setCompoundDrawable(compoundDrawable, direction, width, height);
        }
        text1 = a.getString(R.styleable.CommonTextView_common_text_text1);
        color1 = a.getColor(R.styleable.CommonTextView_common_text_color1, getResources().getColor(R.color.color_333333));
        color2 = a.getColor(R.styleable.CommonTextView_common_text_color2, getResources().getColor(R.color.color_333333));
        a.recycle();
    }

    /**
     * 设置TextView的CompoundDrawable于默认方向（左）
     *
     * @param drawable CompoundDrawable对象
     */
    public void setCompoundDrawable(Drawable drawable) {
        setCompoundDrawable(drawable, 0, 0, 0);
    }

    /**
     * 设置TextView的CompoundDrawable
     *
     * @param drawable  CompoundDrawable对象
     * @param direction 显示方向
     * @param width     显示宽度, 等于0则按drawable实际宽度显示
     * @param height    显示高度, 等于0则按drawable实际高度显示
     */
    public void setCompoundDrawable(Drawable drawable, int direction, int width, int height) {
        if (drawable != null) {
            drawable.setBounds(0, 0,
                    width == 0 ? drawable.getIntrinsicWidth() : width,
                    height == 0 ? drawable.getIntrinsicHeight() : height);
        }

        switch (direction) {
            case 0:
                setCompoundDrawables(drawable, null, null, null);
                break;
            case 1:
                setCompoundDrawables(null, drawable, null, null);
                break;
            case 2:
                setCompoundDrawables(null, null, drawable, null);
                break;
            case 3:
                setCompoundDrawables(null, null, null, drawable);
                break;
            default:
                break;
        }
    }

    @Override
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        //重新设置CompoundDrawables
        if (selectCompoundDrawable != null) {
            setCompoundDrawable(isSelected ? selectCompoundDrawable : compoundDrawable, direction, width, height);
        }
    }

    public void setSecondText(String str) {
        String[] strs = new String[2];
        strs[0] = text1;
        strs[1] = str;

        Integer[] colors = new Integer[2];
        colors[0] = color1;
        colors[1] = color2;
        setManyText(strs, colors, null);
    }

    public void setManyText(String[] strs, Integer[] colors, Integer[] sizes) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < strs.length; i++) {
            int length = builder.length();
            builder.append(strs[i]);
            builder.setSpan(new ForegroundColorSpan(colors[i]), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (sizes != null && sizes.length != 0) {
                builder.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(sizes[i])), length, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        setText(builder);
    }
}
