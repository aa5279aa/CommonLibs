package com.common.ui.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.common.ui.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 波形图
 */
public class CommonWaveView extends View {

    private String tag = "CircleView";

    /**
     * 输入值
     */
    private float mMinValue = 0;
    private float mMaxValue = 0;
    private List<Integer> mListValue = new ArrayList<>();

    /**
     *
     */
    private int mPointNum = 0;
    private int mBgColor = 0;
    private int mLineColor = 0;
    private float mLineWidth = 0;

    private Paint mWavePaint = null;
    private Paint mBgPaint = null;

    /**
     * 中间变量
     */
    private float mItemWidth = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private List<Float[]> showPointList = new ArrayList<>();

    public CommonWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initPaint();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CommonWaveView);
        mPointNum = typedArray.getInteger(R.styleable.CommonWaveView_wave_pointnum, 10);
        mBgColor = typedArray.getColor(R.styleable.CommonWaveView_wave_bg_color, Color.parseColor("#26ff715b"));
        mLineColor = typedArray.getColor(R.styleable.CommonWaveView_wave_line_color, Color.parseColor("#ff715b"));
        mLineWidth = typedArray.getDimension(R.styleable.CommonWaveView_wave_line_width, context.getResources().getDimension(R.dimen.dip3));

        typedArray.recycle();
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(false);
        mWavePaint.setStrokeWidth(mLineWidth);
        mWavePaint.setColor(mLineColor);
        mWavePaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(false);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);
    }

    public void setValue(int minValue, int maxValue, List<Integer> valueList) {
        mMaxValue = maxValue;
        mMinValue = minValue;
        if (valueList.size() > mPointNum) {
            mListValue = valueList.subList(valueList.size() - 1 - mPointNum, valueList.size() - 1);
        } else if (valueList.size() < mPointNum) {
            for (int i = 0; i < mPointNum; i++) {
                int fillSize = mPointNum - valueList.size();
                if (i < fillSize) {
                    mListValue.add(i, null);
                } else {
                    mListValue.add(i, valueList.get(i - fillSize));
                }
            }
        } else {
            mListValue = valueList;
        }

        //高度计算
        mItemWidth = mWidth / (float) (mPointNum - 1);
        float itemHeight = mHeight / (float) (mMaxValue - mMinValue);

        List<Float[]> list = new ArrayList<>();
        for (int i = 0; i < mPointNum; i++) {
            Float[] ints = new Float[2];
            if (i >= mListValue.size()) {
                continue;
            }
            Integer getValue = mListValue.get(i);
            if (getValue == null) {
                continue;
            }
            float value = getValue - mMinValue;
            if (value < 0) {
                value = 0F;
            }
            ints[0] = mItemWidth * i;
            ints[1] = mHeight - (itemHeight * value);
            list.add(ints);
        }
        showPointList = list;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    protected void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
//        float[] pts = new float[]{0, 0, 10, 10, 10, 10, 20, 20, 90, 90, 100, 100};
        if (showPointList.size() <= 1) {
            return;
        }
        canvas.save();
        float[] pts = new float[showPointList.size() * 4 - 4];
        Path path = new Path();
        path.moveTo(0 + mItemWidth * (mPointNum - showPointList.size()), mHeight);
        for (int i = 0; i < showPointList.size(); i++) {
            Float[] floats = showPointList.get(i);
            path.lineTo(floats[0], floats[1]);
            if (i == 0) {
                continue;
            }
            int last = i - 1;
            Float[] floats1 = showPointList.get(last);
            pts[i * 4 - 4] = floats1[0];
            pts[i * 4 - 3] = floats1[1];

            pts[i * 4 - 2] = floats[0];
            pts[i * 4 - 1] = floats[1];
        }
        path.lineTo(mWidth, mHeight);
        path.close();
        canvas.drawLines(pts, mWavePaint);
        canvas.drawPath(path, mBgPaint);
        canvas.restore();
    }


}
