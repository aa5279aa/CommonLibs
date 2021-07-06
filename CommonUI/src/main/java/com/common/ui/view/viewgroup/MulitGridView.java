package com.common.ui.view.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.common.ui.R;

public class MulitGridView extends GridView {
    private boolean isShowDivider = false;
    private int color = getResources().getColor(R.color.color_divider);

    public MulitGridView(Context context) {
        super(context);
    }

    public MulitGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MulitGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MulitGridView);
        isShowDivider = typedArray.getBoolean(R.styleable.MulitGridView_gridview_showdivider, false);
        color = typedArray.getColor(R.styleable.MulitGridView_gridview_dividercolor, getResources().getColor(R.color.color_divider));
        typedArray.recycle();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //选中时不覆盖/覆盖
        View localView1 = getChildAt(0);
        if (localView1 == null || !isShowDivider) {
            return;
        }
        int column = getWidth() / localView1.getWidth();//计算出一共有多少列，假设有3列
        int childCount = getChildCount();//子view的总数
//        System.out.println("子view的总数childCount==" + childCount);
        Paint localPaint;//画笔
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setColor(color);//设置画笔的颜色
        for (int i = 0; i < childCount; i++) {//遍历子view
            View cellView = getChildAt(i);//获取子view
//            if (i < 3) {//第一行
            canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getRight(), cellView.getTop(), localPaint);
//            }
            if (i % column == 0) {//第一列
                canvas.drawLine(cellView.getLeft(), cellView.getTop(), cellView.getLeft(), cellView.getBottom(), localPaint);
            }
            if ((i + 1) % column == 0) {//第三列
                //画子view底部横线
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
            } else if ((i + 1) > (childCount - (childCount % column))) {//如果view是最后一行
                //画子view的右边竖线
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
            } else {//如果view不是最后一行
                //画子view的右边竖线
                canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), localPaint);
                //画子view的底部横线
                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
            }
        }
        canvas.drawLine(getLeft(), getHeight() - 1, getRight(), getHeight() - 1, localPaint);
    }
}
