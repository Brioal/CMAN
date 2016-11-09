package com.brioal.cman.fragment;

import android.os.Bundle;

import com.brioal.cman.R;
import com.brioal.cman.base.CMBaseFragment;

/**
 * Mine Fragment For MainActivity
 * Created by Brioal on 2016/9/11.
 */
public class HomeFragment extends CMBaseFragment {
    private static HomeFragment sFragment;

    public static HomeFragment newInstacne() {
        if (sFragment == null) {
            sFragment = new HomeFragment();
        }
        return sFragment;
    }

    @Override
    public void initVar() {

    }

    @Override
    public void initView(Bundle saveInstanceState) {
        mRootView = inflater.inflate(R.layout.fra_home, container, false);
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
