package com.brioal.cman.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by Brioal on 2016/10/9.
 */

public class HorizontalScroll extends ViewGroup {
    private View mView1;
    private View mView2;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private int mCurrentX = 0;
    private int mCurrentIndex = 0;

    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;

    public HorizontalScroll(Context context) {
        this(context, null);
    }

    public HorizontalScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        if (mListener != null) {
            mListener.changed(0);
        }
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }


    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mView1 = getChildAt(0);
        mView2 = getChildAt(1);
    }

    public void prev() {
        if (mCurrentIndex != 0) {
            changeView(0);
        }
    }

    public void next() {
        if (mCurrentIndex != 1) {
            changeView(1);
        }
    }

    public void changeView(int index) {
        mScroller.startScroll(mCurrentX, 0, index * mScreenWidth - mCurrentX, 0, 1000);
        invalidate();
        mCurrentX = index * mScreenWidth;
        mCurrentIndex = index;
        if (mListener != null) {
            mListener.changed(index);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChild(mView1, widthMeasureSpec, heightMeasureSpec);
        measureChild(mView2, widthMeasureSpec, heightMeasureSpec);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mScreenWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mView1.layout(0, 0, mScreenWidth, mScreenHeight);
        mView2.layout(mScreenWidth, 0, mScreenWidth * 2, mScreenHeight);
    }

    private OnIndexChangedListener mListener;

    public interface OnIndexChangedListener {
        void changed(int index);
    }

    public void setListener(OnIndexChangedListener listener) {
        mListener = listener;
    }
}
