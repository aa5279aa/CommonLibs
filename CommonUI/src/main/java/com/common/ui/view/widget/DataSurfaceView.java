package com.common.ui.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.common.ui.R;
import com.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * N*N控件
 * 1.左侧添加描述
 * 2.右侧添加当前值
 */
public class DataSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static final String TAG = "DataSurfaceView";
    Thread drawThread;
    //线条绘制
    private Paint mWavePaint;//绘制方形图和波形图
    private Paint mWaveShadowPaint;//绘制描述的字体
    private Paint mCoordPaint;//绘制坐标系
    private Paint mBgPaint;
    private Paint mBgClickPaint;
    private Paint mFontPaint;//绘制最大值以及底部坐标轴
    private Paint mEffectPaint;//绘制坐标系虚线
    private Paint mDescPaint;//绘制描述的字体
    private Paint mCurrentPaint;//绘制描述的字体

    private final ShowAttr mAttr = new ShowAttr();

    //宽和高
    private float mWidth = 0;
    private float mHeight = 0;

    private int mCount = 16;
    private final float mMargin = getResources().getDimension(R.dimen.dip2);//外边距
    private int mItemTopHeight = 50;//发动机转速这一行的高度
    private int mItemBottomHeight = 30;//坐标系这一行的高度
    private final float mItemPadding = getResources().getDimension(R.dimen.dip4);
    private final float mItemRound = getResources().getDimension(R.dimen.dip4);

    private float itemViewWidth;
    private float itemViewHeight;
    private float itemCoordWidth;
    private float itemCoordHeight;

    //是否
    private boolean isIng = true;
    //是否初始化
    private boolean isInit = false;
    /**
     * 选中的是第几个卡片
     */
    private int mIndex = -1;
    /**
     * 顶部的偏移量
     */
    private float mOffset = 0;
    private float mMaxOffset = 0;
    private PointF mStartPoint = new PointF();

    public static int refreshInterval = 20;

    private ShowPointSetting[] mSettings = new ShowPointSetting[16];//配置项
    private List<Integer>[] mShowPointList;
    private String[] mCurrents;

    public DataSurfaceView(Context context) {
        super(context);
        init(null);
    }

    public DataSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DataSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DataSurfaceView);
            mAttr.widthCount = a.getInt(R.styleable.DataSurfaceView_datasurface_widthnum, 4);
            mAttr.heightCount = a.getInt(R.styleable.DataSurfaceView_datasurface_heightnum, 4);
            mAttr.showHeightCount = a.getFloat(R.styleable.DataSurfaceView_datasurface_showheightnum, 4);

            mAttr.waveColor = a.getColor(R.styleable.DataSurfaceView_datasurface_waveColor, Color.parseColor("#2870F6"));
            mAttr.coordColor = a.getColor(R.styleable.DataSurfaceView_datasurface_coordColor, Color.parseColor("#aeb4be"));
            mAttr.clickBg = a.getColor(R.styleable.DataSurfaceView_datasurface_clickbg, Color.parseColor("#F5F5F5"));
            mAttr.fontColor = a.getColor(R.styleable.DataSurfaceView_datasurface_fontcolor, Color.parseColor("#999999"));

            mAttr.waveLineWidth = a.getDimension(R.styleable.DataSurfaceView_datasurface_waveWidth, getResources().getDimension(R.dimen.dip1));
            mAttr.coordLineWidth = a.getDimension(R.styleable.DataSurfaceView_datasurface_coordWidth, getResources().getDimension(R.dimen.dip0_5));
        }

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        getHolder().addCallback(this);

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(false);
        mWavePaint.setStrokeWidth(mAttr.waveLineWidth);
        mWavePaint.setColor(mAttr.waveColor);
        mWavePaint.setAntiAlias(true);

        mCoordPaint = new Paint();
        mCoordPaint.setAntiAlias(true);
        mCoordPaint.setStrokeWidth(mAttr.coordLineWidth);
        mCoordPaint.setColor(mAttr.coordColor);
        mCoordPaint.setAntiAlias(true);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setAntiAlias(true);

        mBgClickPaint = new Paint();
        mBgClickPaint.setAntiAlias(true);
        mBgClickPaint.setColor(mAttr.clickBg);
        mBgClickPaint.setStyle(Paint.Style.FILL);
        mBgClickPaint.setAntiAlias(true);

        mFontPaint = new Paint();
        mFontPaint.setAntiAlias(true);
        mFontPaint.setColor(mAttr.fontColor);
        mFontPaint.setStyle(Paint.Style.FILL);
        mFontPaint.setTextSize(15);

        mDescPaint = new Paint();
        mDescPaint.setAntiAlias(true);
        mDescPaint.setColor(Color.parseColor("#333333"));
        mDescPaint.setStyle(Paint.Style.FILL);
        mDescPaint.setTextSize(getResources().getDimension(R.dimen.sp8));
        Typeface normalTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);
        mDescPaint.setTypeface(normalTypeface);

        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setColor(mAttr.fontColor);
        mCurrentPaint.setStyle(Paint.Style.FILL);
        mCurrentPaint.setTextSize(getResources().getDimension(R.dimen.sp8));
        mCurrentPaint.setTypeface(normalTypeface);

        mEffectPaint = new Paint();
        mEffectPaint.setAntiAlias(true);
        mEffectPaint.setStrokeWidth(mAttr.coordLineWidth);
        mEffectPaint.setColor(Color.GRAY);
        PathEffect effects = new DashPathEffect(new float[]{5, 5}, 1);
        mEffectPaint.setPathEffect(effects);

//        mWaveShadowPaint = new Paint();
//        LinearGradient linearGradient = new LinearGradient(0, 0, 0, 100, new int[]{Color.RED, Color.BLUE}, new float[]{0, 1}, Shader.TileMode.MIRROR);
//        mWaveShadowPaint.setShader(linearGradient);
    }

    public void initSettings(ShowPointSetting[] settings) {
        mCount = mAttr.widthCount * mAttr.heightCount;
        mShowPointList = new ArrayList[mCount];
        mCurrents = new String[mCount];
        if (settings.length == mCount) {
            this.mSettings = settings;
            return;
        }
        this.mSettings = new ShowPointSetting[mCount];
        if (settings.length > mCount) {
            System.arraycopy(settings, 0, mSettings, 0, mCount);
            return;
        }
        System.arraycopy(settings, 0, mSettings, 0, settings.length);
    }

    //设置16个
    public void setData(int position, List<Integer> integers, String current) {
        if (mShowPointList == null || mCurrents == null) {
            return;
        }
        if (position >= mCount) {
            return;
        }
        mShowPointList[position] = new ArrayList<>(integers);
        this.mCurrents[position] = current;
        isInit = true;
    }

    //初始化坐标系的计算
    protected void initCoord() {
        //每一个的宽度
        itemViewWidth = mWidth / mAttr.widthCount;
        //除了下面的padding，还要有上面的padding
        itemViewHeight = mHeight / mAttr.showHeightCount;
        //1723/4=430
        itemCoordWidth = itemViewWidth - 2 * mItemPadding - 2 * mMargin;
        itemCoordHeight = itemViewHeight - mItemTopHeight - mItemBottomHeight - 2 * mItemPadding - 2 * mMargin;
        if (mAttr.heightCount > mAttr.showHeightCount) {
            mMaxOffset = itemViewHeight * (mAttr.heightCount - mAttr.showHeightCount);
        }
    }


    protected float[] getDrawCoord() {
        float[] pts = new float[mAttr.widthCount * mAttr.heightCount * 8];
        float startX = mMargin + mItemPadding;
        float startY = mMargin + mItemPadding + mItemTopHeight - mOffset;
        for (int i = 0; i < mSettings.length; i++) {
            pts[i * 8 + 0] = startX;
            pts[i * 8 + 1] = startY;
            pts[i * 8 + 2] = startX;
            pts[i * 8 + 3] = startY + itemCoordHeight;
            pts[i * 8 + 4] = startX;
            pts[i * 8 + 5] = startY + itemCoordHeight;
            pts[i * 8 + 6] = startX + itemCoordWidth;
            pts[i * 8 + 7] = startY + itemCoordHeight;
            if (i % mAttr.widthCount == (mAttr.widthCount - 1)) {
                startX = mMargin + mItemPadding;
                startY += itemViewHeight;
            } else {
                startX += itemViewWidth;
            }
        }
        return pts;
    }

    /**
     * 绘制方波图
     */
    protected void drawArc(Canvas canvas) {
        //长度不对等或者没有初始化，则不绘制
         if (mShowPointList.length != mSettings.length || !isInit) {
            return;
        }
//        canvas.save();
        float startX = mMargin;
        float startY = mMargin - mOffset;
        RectF rectF;
        for (int index = 0; index < mShowPointList.length; index++) {
            List<Integer> integers = mShowPointList[index];
            ShowPointSetting setting = mSettings[index];
            int count = integers.size();
            if (setting.showType == ShowConstant.ShowTypeWave) {
                count--;
            }
            float itemWidth = itemCoordWidth / count;//每一个的宽度
            //绘制背景  mBgPaint
            rectF = new RectF(startX, startY, startX + itemViewWidth - mMargin * 2, startY + itemViewHeight - mMargin * 2);
            if (mIndex == index) {
                canvas.drawRoundRect(rectF, mItemRound, mItemRound, mBgClickPaint);
            } else {
                canvas.drawRoundRect(rectF, mItemRound, mItemRound, mBgPaint);
            }

            float itemX = startX + mItemPadding;
            float itemY = startY + mItemPadding;
            float nextY = 0;
            float[] pts = new float[integers.size() * 8 - 4];
            for (int innerIndex = 0; innerIndex < count; innerIndex++) {
                Integer value = integers.get(innerIndex);
                if (value != null) {
                    value = value > setting.showMaxValue ? setting.showMaxValue : value;
                    itemY = startY + mItemPadding + mItemTopHeight + itemCoordHeight - itemCoordHeight * value / setting.showMaxValue;
                    if (setting.showType == ShowConstant.ShowTypeSquare) {
                        pts[innerIndex * 8 + 0] = itemX;
                        pts[innerIndex * 8 + 1] = itemY;
                        pts[innerIndex * 8 + 2] = itemX + itemWidth;
                        pts[innerIndex * 8 + 3] = itemY;
                    }
                }
                itemX = itemX + itemWidth;
                //方形图逻辑
                if (setting.showType == ShowConstant.ShowTypeSquare) {
                    if (innerIndex != count - 1) {
                        Integer nextValue = integers.get(innerIndex + 1);
                        if (value != null && nextValue != null) {
                            nextValue = nextValue > setting.showMaxValue ? setting.showMaxValue : nextValue;
                            nextY = startY + mItemPadding + mItemTopHeight + itemCoordHeight - itemCoordHeight * nextValue / setting.showMaxValue;
                            pts[innerIndex * 8 + 4] = itemX;
                            pts[innerIndex * 8 + 5] = itemY;
                            pts[innerIndex * 8 + 6] = itemX;
                            pts[innerIndex * 8 + 7] = nextY;
                        }
                    } else {
                        //绘制坐标
                        canvas.drawText(String.valueOf(innerIndex + 2), itemX - 5, startY + mItemPadding + mItemTopHeight + itemCoordHeight + 20, mFontPaint);
                    }
                } else if ((setting.showType == ShowConstant.ShowTypeWave)) {
                    if (value != null && integers.get(innerIndex + 1) != null) {
                        nextY = startY + mItemPadding + mItemTopHeight + itemCoordHeight - itemCoordHeight * integers.get(innerIndex + 1) / setting.showMaxValue;
                        pts[innerIndex * 8 + 4] = itemX - itemWidth;
                        pts[innerIndex * 8 + 5] = itemY;
                        pts[innerIndex * 8 + 6] = itemX;
                        pts[innerIndex * 8 + 7] = nextY;
                    }
                    if (innerIndex == count - 1) {
                        //绘制坐标
                        canvas.drawText(String.valueOf(innerIndex + 2), itemX - 5, startY + mItemPadding + mItemTopHeight + itemCoordHeight + 20, mFontPaint);
                    }
                }
                //绘制坐标
                canvas.drawText(String.valueOf(innerIndex + 1), itemX - itemWidth - 5, startY + mItemPadding + mItemTopHeight + itemCoordHeight + 20, mFontPaint);

//                mWaveShadowPaint.set
                //渐变色
//                canvas.drawRect(itemX - itemWidth, itemY, itemX, startY + mItemPadding + mItemTopHeight + itemCoordHeight, mWaveShadowPaint);

                //绘制虚线
                canvas.drawLine(itemX, startY + mItemPadding + mItemTopHeight, itemX, startY + mItemPadding + mItemTopHeight + itemCoordHeight, mEffectPaint);
            }
            //绘制最大值
            if (!StringUtil.emptyOrNull(setting.showMaxValueShow)) {
                canvas.drawText(setting.showMaxValueShow, startX + mItemPadding + 5, startY + mItemPadding + mItemTopHeight + 30, mFontPaint);//ok
            }

            //todo 绘制描述
            canvas.drawText(setting.showDesc, startX + mItemPadding, startY + mItemPadding + 30, mDescPaint);

            //todo 描述当前值
            String currentStr = String.valueOf(mCurrents[index]);
            float v = mCurrentPaint.measureText(currentStr);
            canvas.drawText(currentStr, startX + mItemPadding + itemCoordWidth - v, startY + mItemPadding + 30, mCurrentPaint);

            //绘制方形图的线
            canvas.drawLines(pts, mWavePaint);

            if (index % mAttr.widthCount == (mAttr.widthCount - 1)) {
                startX = mMargin;
                startY += itemViewHeight;
            } else {
                startX += itemViewWidth;
            }
        }
//        canvas.restore();
    }

    public void release() {
        isIng = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        drawThread = new Thread(this::drawTask);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mWidth = getWidth();
        this.mHeight = getHeight();
        Log.i(TAG, "surfaceChanged,width:" + mWidth + ",height:" + this.mHeight);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawTask() {
        try {
            //休眠一定时间，让surfaceView彻底创建完成
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //绘制固定的坐标系
        Canvas canvas;
        float[] drawCoord;
        while (isIng) {
            canvas = getHolder().lockCanvas();
            //清空画布
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                initCoord();
                drawArc(canvas);
                drawCoord = getDrawCoord();
                canvas.drawLines(drawCoord, mCoordPaint);
                getHolder().unlockCanvasAndPost(canvas);
            }
//            Log.i("lxltest", "time:" + (System.currentTimeMillis() - currentTimeMillis));
            try {
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            int xIndex = (int) (x / itemViewWidth);
            int yIndex = (int) (y / itemViewHeight);
            int index = yIndex * mAttr.widthCount + xIndex;
            mIndex = index;
            Log.i(TAG, "action:ACTION_DOWN" + ",y:" + event.getY());
            mStartPoint.y = y;
        } else if (action == MotionEvent.ACTION_UP) {
//            mIndex = -1;
            Log.i(TAG, "action:ACTION_UP" + ",y:" + event.getY());
        } else if (action == MotionEvent.ACTION_MOVE) {
            mIndex = -1;
            mOffset += mStartPoint.y - event.getY();
            if (mOffset < 0) {
                mOffset = 0;
            } else if (mOffset > mMaxOffset) {
                mOffset = mMaxOffset;
            }
            mStartPoint.y = event.getY();
            Log.i(TAG, "action:ACTION_MOVE" + ",y:" + event.getY() + ",mOffset:" + mOffset);
        }
        return super.dispatchTouchEvent(event);
    }

    public static class ShowPointSetting {
        public int showType;
        public String showMaxValueShow;
        public int showMaxValue;
        public String showDesc;
    }

    public static class ShowAttr {
        /**
         * 横向数量和纵向数量
         */
        private int widthCount = 4;
        private int heightCount = 4;
        /**
         * 高度范围内可展示的数量，可以为小数
         */
        private float showHeightCount = 4;
        /**
         * 各种颜色配置
         */
        public int waveColor;
        public int coordColor;
        private int clickBg;
        private int fontColor;
        /**
         * 绘制的线条宽度
         */
        private float waveLineWidth;
        private float coordLineWidth;
    }

    interface ShowConstant {
        int ShowTypeWave = 0;
        int ShowTypeSquare = 1;
    }
}

