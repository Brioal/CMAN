package com.brioal.cman.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.brioal.cman.R;

/**
 * Created by brioal on 16-11-8.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public class LoadView extends ImageView {
    private Animation mAnimation;
    private boolean isRoating = false;

    public LoadView(Context context) {
        this(context, null);
    }

    public LoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        startRotate();
    }

    //停止旋转
    public void stopRotate() {
        if (mAnimation != null) {
            mAnimation.cancel();
        }
        isRoating = false;
    }

    public void startRotate() {
        isRoating = true;
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.cirlce_animation);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(2000);
        mAnimation.setRepeatMode(Animation.RESTART);
        startAnimation(mAnimation);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                if (isRoating) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startAnimation(animation);
                            animation.start();
                        }
                    }, 500);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimation.start();

    }
}
