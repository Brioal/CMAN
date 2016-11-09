package com.brioal.cman.fragment;

import android.os.Bundle;

import com.brioal.cman.R;
import com.brioal.cman.base.CMBaseFragment;

/**
 * 互动 Fragment For MainActivity
 * Created by Brioal on 2016/9/11.
 */
public class HudongFragment extends CMBaseFragment {
    private static HudongFragment sFragment;

    public static HudongFragment newInstance() {
        if (sFragment == null) {
            sFragment = new HudongFragment();
        }
        return sFragment;
    }

    @Override
    public void initVar() {

    }

    @Override
    public void initView(Bundle saveInstanceState) {
        mRootView = inflater.inflate(R.layout.fra_hudong, container, false);
    }

    @Override
    public void loadDataLocal() {

    }

    @Override
    public void loadDataNet() {

    }

    @Override
    public void setView() {

    }

    @Override
    public void updateView() {

    }

    @Override
    public void saveDataLocal() {

    }
}
