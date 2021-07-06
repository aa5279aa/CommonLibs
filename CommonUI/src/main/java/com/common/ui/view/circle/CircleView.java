package com.common.ui.view.circle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.common.ui.R;

import androidx.annotation.Nullable;

public class CircleView extends View {

    private String tag = "CircleView";
    private Point mCenterPoint = null;
    private float mRadius = 0f;
    private Paint mArcPaint = null;
    private Paint mBgPaint = null;
    private float mArcWidth = 0f;
    private float mShowType = 0;
    private int mArcColor = 0;
//    private int[] mArcColors = new int[0];
    private int mBgColor = 0;
    private float mStartAngle = 0f;
    private float mSweepAngle = 0f;
    private RectF mRectF = null;
    private float mPercent = 0f;
    private Long mAnimTime = 500L;
    private ValueAnimator mAnimator = null;
    private float mValue = 0f;
    private boolean mReverse = false;
    private boolean mValueReverse = false;

    private float mMaxValue = 0f;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initPaint();
        setValue(mValue);
    }

    private void init(Context context, AttributeSet attrs) {
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mRadius = typedArray.getDimension(R.styleable.CircleView_circle_radius, 0f);
        mArcWidth = typedArray.getDimension(R.styleable.CircleView_circle_arcWidth, 0f);
        mShowType = typedArray.getInt(R.styleable.CircleView_circle_showType, 0);
        mArcColor = typedArray.getColor(R.styleable.CircleView_circle_arcColor, 0);
        mBgColor = typedArray.getColor(R.styleable.CircleView_circle_bgColor, 0);
        mStartAngle = typedArray.getFloat(R.styleable.CircleView_circle_startAngle, 0f);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleView_circle_sweepAngle, 0f);
        mMaxValue = typedArray.getFloat(R.styleable.CircleView_circle_maxValue, 0f);
        mValue = typedArray.getFloat(R.styleable.CircleView_circle_value, 0f);
        mReverse = typedArray.getBoolean(R.styleable.CircleView_circle_reverse, false);
        mValueReverse = typedArray.getBoolean(R.styleable.CircleView_circle_valuereverse, false);

        typedArray.recycle();
    }

    private void initPaint() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        if (mShowType == 0) {
            mArcPaint.setStyle(Paint.Style.FILL);
            mArcPaint.setStrokeCap(Paint.Cap.BUTT);
        } else if (mShowType == 1) {
            mArcPaint.setStyle(Paint.Style.STROKE);
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        }
        // 设置画笔粗细
        mArcPaint.setStrokeWidth(mArcWidth);
        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
        // Cap.ROUND,或方形样式 Cap.SQUARE
        if (mArcColor != 0) {
            mArcPaint.setColor(mArcColor);
        }

        if (mBgColor == 0) {
            return;
        }
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(false);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(mArcWidth);
        mBgPaint.setColor(mBgColor);
    }

    public void setArcColorsValue(int[] colors) {
//        mArcColors = colors;
//        int[] colors2 = {Color.parseColor("#20DDBA"), Color.parseColor("#FBE5A5"), Color.parseColor("#F25065")};
//        float[] positions = {0.5f, 0.75f, 1f};
        SweepGradient sweepGradient = new SweepGradient(mRadius, mRadius, colors, null);
        mArcPaint.setShader(sweepGradient);
    }

    public void setValue(Float value) {
        float inValue = value;
        if (inValue > mMaxValue) {
            inValue = mMaxValue;
        }
        float start = mPercent;
        float end = inValue / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(
            float start,
            float end,
            long animTime
    ) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                invalidate();
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取圆的相关参数
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //绘制圆弧的边界
        mRectF.left = mCenterPoint.x - mRadius;
        mRectF.top = mCenterPoint.y - mRadius;
        mRectF.right = mCenterPoint.x + mRadius;
        mRectF.bottom = mCenterPoint.y + mRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    protected void drawArc(Canvas canvas) {
        // 绘制背景圆弧
        // 从进度圆弧结束的地方开始重新绘制，优化性能
        canvas.save();
        if (mBgPaint != null) {
            canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mBgPaint);
        }
        float currentAngle = mSweepAngle * mPercent;
        if (mValueReverse) {
            currentAngle = mSweepAngle * (1 - mPercent);
        }

        // 第一个参数 oval 为 RectF 类型，即圆弧显示区域
        // startAngle 和 sweepAngle  均为 float 类型，分别表示圆弧起始角度和圆弧度数
        // 3点钟方向为0度，顺时针递增
        // 如果 startAngle < 0 或者 > 360,则相当于 startAngle % 360
        // useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        if (mReverse) {
            float value = 180 - mStartAngle - currentAngle;
            canvas.rotate(value < 0 ? value + 360 : value, (float) mCenterPoint.x, (float) mCenterPoint.y);
        } else {
            canvas.rotate(mStartAngle, (float) mCenterPoint.x, (float) mCenterPoint.y);
        }
        canvas.drawArc(mRectF, 0f, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    void reset() {
        startAnimator(mPercent, 0.0f, 500L);
    }
}
