package com.common.ui.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import com.common.ui.R;
import com.common.util.DeviceUtil;

import java.util.List;

public class CarKeyboardView extends KeyboardView {

    private int xPadding;
    private int yPadding;
    private Drawable shiftBig;
    private Drawable shiftSmall;

    public CarKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        xPadding = (int) context.getResources().getDimension(R.dimen.dip16);
        yPadding = (int) context.getResources().getDimension(R.dimen.dip8);
        shiftBig = context.getResources().getDrawable(R.drawable.common_keyboard_shift_big);
        shiftSmall = context.getResources().getDrawable(R.drawable.common_keyboard_shift_small);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Keyboard keyboard = getKeyboard();
        if (keyboard == null) return;
        List<Keyboard.Key> keys = keyboard.getKeys();
        if (keys != null && keys.size() > 15) {
            boolean isUpper = keys.get(15).codes[0] < 95;
            @SuppressLint("DrawAllocation")
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paint.setTypeface(font);
            paint.setAntiAlias(true);
            paint.setColor(getResources().getColor(R.color.color_666666));
            paint.setTextSize(getResources().getDimension(R.dimen.sp11));
            for (Keyboard.Key key : keys) {
                int resourceId = 0;
                int code = key.codes[0];
                if (code > 0) {
                    if (key.pressed) {
                        resourceId = R.drawable.common_rect_r2_f0f0f0_shadow;
                    } else {
                        resourceId = R.drawable.common_rect_r2_ffffff_shadow;
                    }
                } else if (code == -1) {
                    //大小写切换
                    key.icon = isUpper ? shiftBig : shiftSmall;
                    if (key.pressed) {
                        resourceId = R.drawable.common_rect_r2_8f939a_shadow;
                    } else {
                        resourceId = R.drawable.common_rect_r2_b3b8c1_shadow;
                    }
                } else if (code == -4) {
                    paint.setColor(getResources().getColor(R.color.color_ffffff));
                    //确认
                    if (key.pressed) {
                        resourceId = R.drawable.common_rect_r2_2364dc_shadow;
                    } else {
                        resourceId = R.drawable.common_rect_r2_2870f6_shadow;
                    }
                } else if (code == -5) {
                    if (key.pressed) {
                        resourceId = R.drawable.common_rect_r2_8f939a_shadow;
                    } else {
                        resourceId = R.drawable.common_rect_r2_b3b8c1_shadow;
                    }
                }

                if (resourceId != 0) {
                    Drawable dr = getContext().getResources().getDrawable(resourceId);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                }
                if (key.label != null) {
                    Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                    //计算字体的baseline
                    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                    int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                    // 下面这行是实现水平居中
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(key.label.toString(), rect.centerX(), baseline, paint);
                    continue;
                }
                if (key.icon != null) {
                    key.icon.setBounds(key.x + xPadding, key.y + yPadding, key.x + key.width - xPadding, key.y + key.height - yPadding);
                    key.icon.draw(canvas);
                }
            }
        }
    }
}