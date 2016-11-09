package com.brioal.cman.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import com.brioal.cman.R;
import com.brioal.drawableview.render.LoadingRenderer;

/**
 * Created by brioal on 16-10-28.
 * Email : brioal@foxmail.com
 * Gihub : https://github.com/Brioal
 */

public class ErrorRenderer extends LoadingRenderer {
    private Context mContext;

    public ErrorRenderer(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void draw(Canvas canvas) {
        super.draw(canvas);
        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_error);
        drawable.draw(canvas);
    }

    @Override
    protected void computeRender(float v) {

    }

    @Override
    protected void setAlpha(int i) {

    }

    @Override
    protected void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    protected void reset() {

    }


}
