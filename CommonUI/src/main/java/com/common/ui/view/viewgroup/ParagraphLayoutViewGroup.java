package com.common.ui.view.viewgroup;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;


/**
 * 将child view 整块显示，如果一行显示不下，则整块换行
 */
public class ParagraphLayoutViewGroup extends ViewGroup {
    private static final String sTag = ParagraphLayoutViewGroup.class.getSimpleName();
    private boolean mIsDebug = false;

    private static final String sNot_Enough_Space_Width_Excess = "Not_Enough_Space_Width_Excess";
    private static final String sNot_Enough_Space_Height_Excess = "Not_Enough_Space_Height_Excess";

    private class Size {
        public int width;
        public int height;
    }

    public interface AlignType {
        int bottom = 0;
        int verticalCenter = 1;
        int top = 2;
    }

    public interface HorizonAlignType {
        int left = 0;
        int right = 1;
    }

    private int mMaxWidth = 500;
    private int mMaxHeight = 100;
    private int mHorizionMargin = 10;
    private int mVerticalMargin = 10;
    private final SparseArray<ArrayList<View>> mToDrawMap = new SparseArray<>();
    private final SparseArray<Integer> mRowHeightList = new SparseArray<Integer>();
    private final ArrayList<View> mOutDisplayRectViewList = new ArrayList<>();
    private int mAlignType = AlignType.bottom;
    private int mHorizonAlignType = HorizonAlignType.left;
    private int mMaxLine = Integer.MAX_VALUE;


    public ParagraphLayoutViewGroup(Context context) {
        super(context);
        init(context);
    }

    public ParagraphLayoutViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParagraphLayoutViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setAlignType(int alignType) {
        this.mAlignType = alignType;
    }

    public void setHorizonAlignType(int alignType) {
        this.mHorizonAlignType = alignType;
    }

    public void setHorizionMargin(int horizionMargin) {
        this.mHorizionMargin = horizionMargin;
    }

    public void setVerticalMargin(int verticalMargin) {
        this.mVerticalMargin = verticalMargin;
    }

    public void setMaxLine(int maxLine) {
        if (maxLine <= 0) {
            return;
        }

        this.mMaxLine = maxLine;
    }

    private void init(Context context) {
        if (context == null ||
                context.getResources() == null ||
                context.getResources().getDisplayMetrics() == null) {
            return;
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mMaxWidth = displayMetrics.widthPixels;
        mMaxHeight = displayMetrics.heightPixels;
    }

    /**
     * 在4.4以下版本，relative layout measure有系统bug，此处保护一下
     */
    private void measureViewSafely(final View view, final int widthMeasureSpec, final int heightMeasureSpec) {
        if (view == null) {
            return;
        }

        try {
            if (view instanceof RelativeLayout &&
                    view.getLayoutParams() == null) {
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
            }

            view.measure(widthMeasureSpec, heightMeasureSpec);
        } catch (Exception e) {
            Log("measureViewSafely Exception");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthLimited = getDefaultSize(mMaxWidth, widthMeasureSpec);
        int heightLimited = getDefaultSize(mMaxHeight, heightMeasureSpec);

        if (widthLimited > mMaxWidth) {
            widthLimited = mMaxWidth;
        }

        if (heightLimited > mMaxHeight) {
            heightLimited = mMaxHeight;
        }

        mToDrawMap.clear();
        mRowHeightList.clear();
        resetOutDisplayRectViewVisibility();

        int mRow = 0;
        int horizionLeftUsable = widthLimited;
        int verticalLeftUsable = heightLimited;
        int maxHeightInRow = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child == null ||
                    child.getVisibility() != View.VISIBLE) {
                continue;
            }

            final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST);
            final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
            measureViewSafely(child, childWidthMeasureSpec, childHeightMeasureSpec);

            final int childMeasureWidth = child.getMeasuredWidth();
            final int childMeasureHeight = child.getMeasuredHeight();

            if (childMeasureWidth > widthLimited) {
                Log(sNot_Enough_Space_Width_Excess);
                break;
            }

            if (childMeasureHeight > verticalLeftUsable) {
                Log(sNot_Enough_Space_Height_Excess);
                break;
            }

            if (mToDrawMap.get(mRow) != null &&
                    mToDrawMap.get(mRow).size() > 0) {
                horizionLeftUsable -= mHorizionMargin;
            }

            if (horizionLeftUsable >= childMeasureWidth) {
                createToDrawMapChildList(mRow);
                mToDrawMap.get(mRow).add(child);
                horizionLeftUsable -= childMeasureWidth;

                if (childMeasureHeight > maxHeightInRow) {
                    maxHeightInRow = childMeasureHeight;
                }
            } else {
                verticalLeftUsable -= mVerticalMargin + maxHeightInRow;
                if (childMeasureHeight > verticalLeftUsable) {
                    Log(sNot_Enough_Space_Height_Excess);
                    break;
                }

                mRow++;

                if (mRow > mMaxLine - 1) {
                    setOutDisplayRectViewListInvisible(i);
                    break;
                }

                maxHeightInRow = 0;
                horizionLeftUsable = widthLimited;

                createToDrawMapChildList(mRow);
                mToDrawMap.get(mRow).add(child);
                horizionLeftUsable -= childMeasureWidth;

                if (childMeasureHeight > maxHeightInRow) {
                    maxHeightInRow = childMeasureHeight;
                }
            }

            mRowHeightList.put(mRow, maxHeightInRow);
        }

        final Size size = getRealSize();
        final int realWidthMeasureSpec = MeasureSpec.makeMeasureSpec(size.width, MeasureSpec.EXACTLY);
        final int realHeightMeasureSpec = MeasureSpec.makeMeasureSpec(size.height, MeasureSpec.EXACTLY);

        super.onMeasure(realWidthMeasureSpec, realHeightMeasureSpec);
    }

    /**
     * 设置已经超过最大行数的view,我们将不展示它们
     */
    private void setOutDisplayRectViewListInvisible(final int start) {
        for (int j = start; j < getChildCount(); j++) {
            final View child = getChildAt(j);

            if (child == null ||
                    child.getVisibility() != View.VISIBLE) {
                continue;
            }
            child.setVisibility(View.GONE);
            mOutDisplayRectViewList.add(child);
        }
    }

    private void resetOutDisplayRectViewVisibility() {
        for (View child : mOutDisplayRectViewList) {
            if (child == null ||
                    child.getParent() != this) {
                continue;
            }
            child.setVisibility(View.VISIBLE);
        }

        mOutDisplayRectViewList.clear();
    }

    private void createToDrawMapChildList(final int row) {
        if (row < 0 ||
                mToDrawMap.get(row) != null) {
            return;
        }

        final ArrayList<View> list = new ArrayList<View>();
        mToDrawMap.put(row, list);
    }

    @Override
    public void onViewRemoved(View child) {
        if (child == null) {
            return;
        }

        if (mOutDisplayRectViewList.contains(child)) {
            child.setVisibility(View.VISIBLE);
            mOutDisplayRectViewList.remove(child);
        }
    }

    private Size getRealSize() {
        Size ret = new Size();
        int realWidth = 0;
        int realHeight = 0;
        for (int i = 0; i < mToDrawMap.size(); i++) {
            if (mToDrawMap.get(i) == null ||
                    mRowHeightList.get(i) == null) {
                continue;
            }

            ArrayList<View> childList = mToDrawMap.get(i);

            int tmp = 0;
            int childIndex = 0;
            for (View child : childList) {
                if (child == null) {
                    continue;
                }

                if (childIndex > 0) {
                    tmp += mHorizionMargin;
                }
                tmp += child.getMeasuredWidth();
                childIndex++;
            }

            if (tmp > realWidth) {
                realWidth = tmp;
            }

            realHeight += mRowHeightList.get(i);
            if (i > 0) {
                realHeight += mVerticalMargin;
            }
        }

        ret.width = realWidth;
        ret.height = realHeight;

        return ret;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mHorizonAlignType == HorizonAlignType.right) {
            innerLayoutAsHorizonRightAlign(changed, left, top, right, bottom);
        } else {
            innerLayoutAsHorizonLeftAlign(changed, left, top, right, bottom);
        }
    }

    /**
     * 水平左对齐布局
     */
    private void innerLayoutAsHorizonLeftAlign(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;
        int childTop = 0;
        int currentRowHeight;
        for (int i = 0; i < mToDrawMap.size(); i++) {
            if (mToDrawMap.get(i) == null ||
                    mRowHeightList.get(i) == null) {
                continue;
            }

            currentRowHeight = mRowHeightList.get(i);
            final ArrayList<View> childList = mToDrawMap.get(i);
            int childIndex = 0;

            for (View child : childList) {
                if (child == null) {
                    continue;
                }


                if (childIndex > 0) {
                    childLeft += mHorizionMargin;
                }

                int realTop = getChildViewLayoutTop(childTop, currentRowHeight, child.getMeasuredHeight());
                child.layout(childLeft, realTop, childLeft + child.getMeasuredWidth(), realTop + child.getMeasuredHeight());

                childLeft += child.getMeasuredWidth();
                childIndex++;
            }

            childTop += currentRowHeight + mVerticalMargin;
            childLeft = 0;
        }
    }

    /**
     * 水平右对齐布局
     */
    private void innerLayoutAsHorizonRightAlign(boolean changed, int left, int top, int right, int bottom) {
        final int width = right - left;
        int childRight = width;
        int childTop = 0;
        int currentRowHeight;

        for (int i = 0; i < mToDrawMap.size(); i++) {
            if (mToDrawMap.get(i) == null ||
                    mRowHeightList.get(i) == null) {
                continue;
            }

            currentRowHeight = mRowHeightList.get(i);
            final ArrayList<View> childList = mToDrawMap.get(i);

            View child;
            for (int childIndex = childList.size() - 1; childIndex >= 0; childIndex--) {
                child = childList.get(childIndex);
                if (child == null) {
                    continue;
                }


                if (childIndex != childList.size() - 1) {
                    childRight -= mHorizionMargin;
                }

                int realTop = getChildViewLayoutTop(childTop, currentRowHeight, child.getMeasuredHeight());

                child.layout(childRight - child.getMeasuredWidth(), realTop, childRight, realTop + child.getMeasuredHeight());

                childRight -= child.getMeasuredWidth();
            }

            childTop += currentRowHeight + mVerticalMargin;
            childRight = width;
        }
    }

    private int getChildViewLayoutTop(int rowTop, int rowHeight, int childHeight) {
        int realTop;
        if (mAlignType == AlignType.verticalCenter) {
            realTop = rowTop + (rowHeight - childHeight) / 2;
        } else if (mAlignType == AlignType.top) {
            realTop = rowTop;
        } else {
            realTop = rowTop + (rowHeight - childHeight);
        }

        return realTop;
    }

    private void Log(String message) {
        if (TextUtils.isEmpty(message) ||
                !mIsDebug) {
            return;
        }

        Log.e(sTag, message);
    }
}