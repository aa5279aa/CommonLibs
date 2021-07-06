package com.common.ui.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.common.ui.R;
import com.common.ui.dialog.InputDialog;
import com.common.util.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.表格，N*N的模式，点击某一项，弹出dialog框提示编辑
 * 2.attr中标记是否支持编辑
 */
public class TableView extends View implements InputDialog.ClickCallBack {
    static final String TAG = "TableView";
    //线条绘制
    private final Paint mTitlePaint = new Paint();
    private final Paint mTitleBgPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private final Paint mDividerPaint = new Paint();
    private final Paint mSelectPaint = new Paint();

    private TableValueModel mValue = new TableValueModel();
    private AttrValue mAttr = new AttrValue();
    private final ShowValue mShowValue = new ShowValue();

    private boolean isInit;

    private final PointF mStartPoint = new PointF();//选中记录
    private int mType = 0;//0默认，1选中，2滑动

    private final LinkedHashMap<String, Integer[]> mSelectMap = new LinkedHashMap<>();//选中的
    public List<List<String>> mDataList = new ArrayList<>();
    private final InputDialog dialog = new InputDialog(getContext());

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TableView);
            mAttr.isTitleEdit = a.getBoolean(R.styleable.TableView_tableview_is_title_edit, false);
            mAttr.isContentEdit = a.getBoolean(R.styleable.TableView_tableview_is_content_edit, false);
            mAttr.titleColor = a.getColor(R.styleable.TableView_tableview_textcolor, Color.parseColor("#333333"));
            mAttr.titleBgColor = a.getColor(R.styleable.TableView_tableview_titlebgcolor, Color.parseColor("#f8f8f8"));
            mAttr.textColor = a.getColor(R.styleable.TableView_tableview_textcolor, Color.parseColor("#333333"));
            mAttr.dividerColor = a.getColor(R.styleable.TableView_tableview_dividercolor, Color.parseColor("#e8e8e8"));
            mAttr.selectColor = a.getColor(R.styleable.TableView_tableview_textcolor, Color.parseColor("#2870f6"));
            mAttr.dividerWidth = a.getDimension(R.styleable.TableView_tableview_dividerwidth, 1);
            mAttr.padding = getResources().getDimension(R.dimen.dip2);
            a.recycle();
        }
        InputDialog.DialogModel model = new InputDialog.DialogModel();
        model.setHint("编辑内容");
        model.setButton("确定");
        model.setTitle("请输入编辑内容");
        dialog.bindData(model);
        dialog.setButtonClickBack(this);
        setShowAttr(mAttr);

        Rect rect = new Rect();
        mTextPaint.getTextBounds("123", 0, 2, rect);
        mShowValue.textHeight = rect.height();
    }

    public void setShowAttr(AttrValue showAttr) {
        mAttr = showAttr;
        Typeface normalTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);

        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setColor(showAttr.titleColor);
        mTitlePaint.setStyle(Paint.Style.FILL);
        mTitlePaint.setTextSize(getResources().getDimension(R.dimen.sp8));
        mTitlePaint.setTypeface(normalTypeface);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(showAttr.textColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.sp8));
        mTextPaint.setTypeface(normalTypeface);

        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mAttr.dividerWidth == 0 ? 1 : mAttr.dividerWidth);
        mDividerPaint.setColor(mAttr.dividerColor == 0 ? Color.parseColor("#666666") : mAttr.dividerColor);
//        mTextPaint.setTypeface(normalTypeface);

        mTitleBgPaint.setAntiAlias(true);
        mTitleBgPaint.setColor(showAttr.titleBgColor);
        mTitleBgPaint.setStyle(Paint.Style.FILL);

        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mAttr.dividerWidth == 0 ? 1 : mAttr.dividerWidth);
        mDividerPaint.setColor(mAttr.dividerColor == 0 ? Color.parseColor("#e8e8e8") : mAttr.dividerColor);
        mDividerPaint.setAntiAlias(true);

        mSelectPaint.setAntiAlias(true);
        mSelectPaint.setStrokeWidth(mAttr.dividerWidth == 0 ? 1 : mAttr.dividerWidth);
        mSelectPaint.setColor(mAttr.selectColor == 0 ? Color.parseColor("#2870f6") : mAttr.selectColor);
        mSelectPaint.setTextSize(getResources().getDimension(R.dimen.sp8));
        mSelectPaint.setAntiAlias(true);

        mAttr.padding = getResources().getDimension(R.dimen.dip2);
    }

    /**
     * 设置数据表头
     *
     * @param value
     */
    public void notifyDataChange(TableValueModel value) {
        this.mDataList.clear();
        mValue = value;
        //构建二维数组
        this.mShowValue.hasRowTitle = mValue.lineTopList.size() != 0;
        this.mShowValue.hasColumnTitle = mValue.linesTopList.size() != 0;
        this.mShowValue.rowNum = this.mShowValue.hasRowTitle ? mValue.lineTopList.size() : mValue.dataList.get(0).size();
        this.mShowValue.columnNum = this.mShowValue.hasColumnTitle ? mValue.linesTopList.size() : mValue.dataList.size();
        for (int i = -1; i < this.mShowValue.columnNum; ) {
            List<String> line = new ArrayList<>();
            this.mDataList.add(line);
            if (i == -1) {
                if (this.mShowValue.hasRowTitle) {
                    line.addAll(mValue.lineTopList);
                }
                i++;
                continue;
            }
            if (this.mShowValue.hasColumnTitle) {
                line.add(mValue.linesTopList.get(i));
            }
            line.addAll(mValue.dataList.get(i));
            i++;
        }
        if (this.mShowValue.hasRowTitle) {
            this.mShowValue.columnNum++;
        }
        mShowValue.initCoord(mAttr);
        isInit = true;
        this.requestLayout();
    }

    /**
     * 通知单个刷新
     *
     * @param column 所在行
     * @param row    所在列
     * @param value  值
     */
    @SuppressLint("")
    public void notifyDataChangeByIndex(int column, int row, String value) {
        this.mDataList.get(row).set(column, value);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInit) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            //计算高度
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算高度
        int height = (int) (mShowValue.columnNum * getResources().getDimension(R.dimen.dip28));
        setMeasuredDimension(getMeasuredWidth(), height);
        mShowValue.viewWidth = getMeasuredWidth();
        mShowValue.viewHeight = height;
        mShowValue.initCoord(mAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowValue.viewHeight == 0 || mShowValue.viewWidth == 0) {
            super.onDraw(canvas);
            return;
        }
        drawData(canvas);
    }

    /**
     * 绘制方波图
     */
    protected void drawData(Canvas canvas) {
        //长度不对等或者没有初始化，则不绘制
        if (!isInit) {
            return;
        }
        //清理之前的。
        float startX;
        float startY = mAttr.padding;
        float itemViewHeight = mShowValue.itemViewHeight;
        float itemViewWidth = mShowValue.itemViewWidth;
        boolean isTitle;
        //绘制普通分割线
        for (int column = 0; column <= this.mShowValue.columnNum; column++) {
            float[] pts = new float[4];
            pts[0] = mAttr.padding;
            pts[1] = mAttr.padding + column * itemViewHeight;
            pts[2] = mShowValue.viewWidth - mAttr.padding;
            pts[3] = mAttr.padding + column * itemViewHeight;
            canvas.drawLines(pts, mDividerPaint);
        }
        for (int row = 0; row <= this.mShowValue.rowNum; row++) {
            float[] pts = new float[4];
            pts[0] = mAttr.padding + row * itemViewWidth;
            pts[1] = mAttr.padding;
            pts[2] = mAttr.padding + row * itemViewWidth;
            pts[3] = mShowValue.viewHeight - mAttr.padding;
            canvas.drawLines(pts, mDividerPaint);
        }
        for (int column = 0; column < this.mShowValue.columnNum; column++) {
            startX = mAttr.padding;
            List<String> line = mDataList.get(column);
            for (int row = 0; row < this.mShowValue.rowNum; row++) {
                isTitle = (mShowValue.hasRowTitle && column == 0) || (mShowValue.hasColumnTitle && row == 0);
                String value = line.get(row);
                float[] pts = new float[16];
                pts[0] = startX;
                pts[1] = startY;
                pts[2] = startX + itemViewWidth;
                pts[3] = startY;

                pts[4] = startX + itemViewWidth;
                pts[5] = startY;
                pts[6] = startX + itemViewWidth;
                pts[7] = startY + itemViewHeight;

                pts[8] = startX + itemViewWidth;
                pts[9] = startY + itemViewHeight;
                pts[10] = startX;
                pts[11] = startY + itemViewHeight;

                pts[12] = startX;
                pts[13] = startY + itemViewHeight;
                pts[14] = startX;
                pts[15] = startY;

                //文字绘制在中心，x,y是左上角的距离
                float textWidth = mTextPaint.measureText(value);
                if (isTitle) {
                    RectF rectF = new RectF(startX + 1, startY + 1, startX + itemViewWidth - 1, startY + itemViewHeight - 1);
                    canvas.drawRect(rectF, mTitleBgPaint);
                    canvas.drawText(value, startX + (itemViewWidth - textWidth) / 2, startY + (itemViewHeight + mShowValue.textHeight) / 2, mTitlePaint);
                } else {
                    canvas.drawText(value, startX + (itemViewWidth - textWidth) / 2, startY + (itemViewHeight + mShowValue.textHeight) / 2, mSelectMap.get(column + "_" + row) != null ? mSelectPaint : mTextPaint);
                }
                Map.Entry<String, Integer[]> lastEntry = getLastEntry();
                if (lastEntry != null && (column + "_" + row).equals(lastEntry.getKey())) {
                    canvas.drawLines(pts, mSelectPaint);
                }
                startX += mShowValue.itemViewWidth;
            }
            startY += mShowValue.itemViewHeight;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mAttr.isContentEdit && !mAttr.isTitleEdit) {
            return super.dispatchTouchEvent(event);
        }
        int action = event.getAction();
        //判断坐标变化，超过10则认为滑动
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            Log.i(TAG, "action:ACTION_DOWN" + ",x:" + event.getX() + ",y:" + event.getY());
            mStartPoint.x = x;
            mStartPoint.y = y;
            mType = 1;
        } else if (action == MotionEvent.ACTION_UP) {
            if (mType == 1) {
                actionSelect(mStartPoint);
            }
            mType = 0;
            Log.i(TAG, "action:ACTION_UP" + ",x:" + event.getX() + ",y:" + event.getY());
        } else if (action == MotionEvent.ACTION_MOVE) {
            //判断距离移动是否超过10
            double sqrt = Math.sqrt(Math.pow((double) (mStartPoint.x - x), 2.0) + Math.pow((double) (mStartPoint.y - y), 2.0));
            if (sqrt > 10.0) {
                mType = 2;
            }
            Log.i(TAG, "action:ACTION_MOVE" + ",x:" + event.getX() + ",y:" + event.getY());
        }
        return super.dispatchTouchEvent(event);
    }

    private void actionSelect(PointF startPoint) {
        int row = (int) ((startPoint.x - 2 * mAttr.padding) / mShowValue.itemViewWidth);
        int column = (int) ((startPoint.y - 2 * mAttr.padding) / mShowValue.itemViewHeight);
        if (!mAttr.isTitleEdit && (mShowValue.hasRowTitle && row == 0 || mShowValue.hasColumnTitle && column == 0)) {
            return;
        }
        mSelectMap.put(column + "_" + row, new Integer[]{column, row});
        invalidate();
        dialog.clearInputText(mDataList.get(column).get(row));
        dialog.show();
    }

    @Override
    public void clickButton(String str) {
        Map.Entry<String, Integer[]> last = getLastEntry();
        if (last == null) {
            return;
        }
        if (StringUtil.emptyOrNull(str)) {
            Toast.makeText(getContext(), "输入值不能为空，请重新编辑", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer[] value = last.getValue();
        mDataList.get(value[0]).set(value[1], str);
        int column = value[0];
        int row = value[1];
        if (mShowValue.hasRowTitle) {
            column--;
        }
        if (mShowValue.hasColumnTitle) {
            row--;
        }
        if (column == -1) {
            if (row == -1) {
                row = 0;
            }
            mValue.lineTopList.set(row, str);
        } else if (row == -1) {
            mValue.linesTopList.set(column, str);
        } else {
            mValue.dataList.get(column).set(row, str);
        }
        invalidate();
    }

    private Map.Entry<String, Integer[]> getLastEntry() {
        Map.Entry<String, Integer[]> last = null;
        for (Map.Entry<String, Integer[]> stringEntry : mSelectMap.entrySet()) {
            last = stringEntry;
        }
        return last;
    }

    public boolean isInit() {
        return isInit;
    }

    public static class AttrValue {
        public boolean isTitleEdit;
        public boolean isContentEdit;
        /**
         * 各种颜色配置
         */
        public int titleColor;
        public int titleBgColor;
        public int textColor;
        public int dividerColor;
        public int selectColor;
        /**
         * 绘制的边框的宽度
         */
        public float dividerWidth;
        /**
         * 内边距
         */
        public float padding = 0;
    }

    public static class ShowValue {
        /**
         * 横向数量和纵向数量
         */
        public int columnNum = 4;
        public int rowNum = 4;
        private boolean hasColumnTitle = true;
        private boolean hasRowTitle = true;
        private float viewWidth = 100;
        private float textHeight = 15;
        private float viewHeight = 100;
        private float itemViewWidth;//每个宽度
        private float itemViewHeight;

        public void initCoord(AttrValue attrValue) {
            itemViewWidth = (viewWidth - 2 * attrValue.padding) / rowNum;
            itemViewHeight = (viewHeight - 2 * attrValue.padding) / columnNum;
        }
    }

    public static class TableValueModel {
        public List<String> lineTopList;
        public List<String> linesTopList;
        public List<List<String>> dataList;
    }
}

