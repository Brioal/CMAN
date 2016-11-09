package com.brioal.cman.view.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.brioal.cman.R;


/**
 * 中心大圆点
 * Created by Brioal on 2016/8/13.
 */

public class CenterDot extends View {
    private Paint mPaint;
    private int mWidth; //组件宽度
    private String mStatue = "正在连接"; //当前状态
    private int mMaxTextSize = 70;
    private int mMinTextSize = 50;
    private float mProgress = 0;

    public void setStatue(String statue) {
        mStatue = statue;
        invalidate();
    }


    public CenterDot(Context context, int width) {
        this(context, null, width);
    }

    public CenterDot(Context context, AttributeSet attrs, int width) {
        super(context, attrs);
        mWidth = width;
        init();
    }

    //设置动画
    public void setAnimationPogress(float progress) {
        setScaleX((float) (1 - progress * 0.2));
        setScaleY((float) (1 - progress * 0.2));
        this.mProgress = progress;
        invalidate();
    }
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        setBackgroundResource(R.drawable.ic_center_dot);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect textBound = new Rect();
        canvas.save();
        canvas.translate(mWidth / 2, mWidth / 2);
        //绘制中心文字
        mPaint.setTextSize(mMaxTextSize-mProgress*(mMaxTextSize-mMinTextSize));
        mPaint.setColor(Color.WHITE);
        mPaint.getTextBounds(mStatue, 0, mStatue.length(), textBound);
        canvas.drawText(mStatue, -(textBound.right + textBound.left) / 2, -(textBound.top + textBound.bottom) / 2 , mPaint);
        canvas.restore();
    }
}
