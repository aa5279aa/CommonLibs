package com.common.ui.view.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.common.ui.model.TabModel;

import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * 实现ViewGroup的高效绘制
 */
public class TabShowContainer extends LinearLayout implements View.OnClickListener {

    private TabShowContainerListener mListener;
    private List<TabModel> mList;
    private ShowImageAction showImageAction;

    private int count = 4;//数量


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TabShowContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TabShowContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long currentTimeMillis = System.currentTimeMillis();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getWidth();
//        int width = 1080;
//        int itemWidth = width / count;
//        int suggestedMinimumWidth = getSuggestedMinimumWidth();
//        if (getChildCount() > 0) {
//            View child0 = getChildAt(0);
//            child0.setRight(0);
//            child0.setLeft(270);
//            child0.setTop(53);
//            child0.setBottom(253);
//        }
//        int childLeft = 0;
//        int childTop = 53;
//        int itemWidth = width / count;
//        int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View childAt = getChildAt(i);
//            childAt.measure(widthMeasureSpec, heightMeasureSpec);
//            childAt.setRight(childLeft + itemWidth);
//            childAt.setLeft(childLeft);
//            childAt.setTop(childTop);
//            childAt.setBottom(childTop + childAt.getMeasuredHeight());
//            childLeft = childLeft + itemWidth;
//        }
//        setMeasuredDimension(1080, 249);

        Log.i("lxltest", "onMeasure:" + (System.currentTimeMillis() - currentTimeMillis));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        long currentTimeMillis = System.currentTimeMillis();
//        layoutListChild(changed, l, t, r, b);
        super.onLayout(changed, l, t, r, b);
        Log.i("lxltest", "onLayout:" + (System.currentTimeMillis() - currentTimeMillis));
    }

    //
//    /**
//     * 均分布局
//     */
    private void layoutListChild(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        int itemWidth = width / count;

        int childLeft = 0;
        int childTop = top;
        int currentRowHeight;
        int childCount = getChildCount();
        if (childCount > 0) {
//            Log.i("lxltest", "size:" + childCount);
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }
//            Log.i("lxltest", "index:" + i + ",left:" + child.getLeft() + ",right" + child.getRight() + ",top:" + child.getTop() + ",bottom:" + child.getBottom());
            child.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
//            childLeft = childLeft + itemWidth;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        if (getChildCount() > 0) {
//            View childAt = getChildAt(0);
//            Log.i("lxltest", "childAt:" + childAt.getBottom());
//        }
        long currentTimeMillis = System.currentTimeMillis();
        super.dispatchDraw(canvas);
        Log.i("lxltest", "dispatchDraw:" + (System.currentTimeMillis() - currentTimeMillis));
    }

    /**
     * 设置内容
     */
    public void bindData(List<TabModel> list) {
        mList = list;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(TabShowContainerListener listener) {
        this.mListener = listener;
    }


    /**
     * 设置监听
     *
     * @param action
     */
    public void setShowImageAction(ShowImageAction action) {
        this.showImageAction = action;
    }

    /**
     * 通知刷新
     */
    public void notifycation() {

        //查看原来的数量，如果有则不需要重新生成
        int childCount = getChildCount();
        if (childCount < mList.size()) {
            //代码实现，效果更高
            for (int i = 0; i < mList.size() - childCount; i++) {
                View itemView = createItemView();
//                View itemView = new View(getContext());
                addView(itemView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        //绑定内容
        for (int i = 0; i < mList.size(); i++) {
            ViewGroup childAt = (ViewGroup) getChildAt(i);
            ImageView img = (ImageView) childAt.getChildAt(0);
            TextView textView = (TextView) childAt.getChildAt(1);

            TabModel model = mList.get(i);
            //imageloader的实现，回调实现
            if (showImageAction != null) {
                showImageAction.showImage(i, img, model.iconUrl);
            }
            textView.setText(model.iconName);
            childAt.setTag(i);
            childAt.setOnClickListener(this);
        }
    }


    private View createItemView() {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(getContext());
        TextView textView = new TextView(getContext());
        textView.setText("fegweg给");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        container.addView(imageView, lp);
        container.addView(textView, lp);
        return container;
    }


    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.clickTab((Integer) v.getTag());
        }
    }

    public interface TabShowContainerListener {

        void clickTab(int index);

    }

    public interface ShowImageAction {

        void showImage(int index, ImageView img, String url);

    }
}


