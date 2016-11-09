package com.brioal.cman;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.brioal.baselib.util.ToastUtils;
import com.brioal.bottomtab.entity.TabEntity;
import com.brioal.bottomtab.interfaces.OnTabSelectedListener;
import com.brioal.bottomtab.view.BottomLayout;
import com.brioal.cman.base.CMBaseActivity;
import com.brioal.cman.fragment.DeviceFragment;
import com.brioal.cman.fragment.HomeFragment;
import com.brioal.cman.fragment.HudongFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends CMBaseActivity {
    @Bind(R.id.main_bottom_layout)
    BottomLayout mBottomLayout;

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private long mLasClickTime = 0;


    @Override
    public void initVar() {
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setTheme(R.style.CMTheme_NoActionBar);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        initBottomLayout();//初始化底部菜单
        initContainer(); //初始化默认容器内容
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //更换Fragment
    public void changeFragment(Fragment fragment) {
        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commit();
        }
        if (fragment.isAdded()) {
            mFragmentManager.beginTransaction().show(fragment).commit();
        } else {
            mFragmentManager.beginTransaction().add(R.id.main_container, fragment).commit();
        }
        mCurrentFragment = fragment;
    }

    private void initContainer() {
        mFragmentManager = getSupportFragmentManager();
        changeFragment(HomeFragment.newInstacne());
    }

    private void initBottomLayout() {
        List<TabEntity> list = new ArrayList<>();
        list.add(new TabEntity(R.mipmap.ic_home, "首页"));
        list.add(new TabEntity(R.mipmap.ic_devices, "我的"));
        list.add(new TabEntity(R.mipmap.ic_inter, "互动"));

        mBottomLayout.setInCircleColor(getResources().getColor(R.color.colorPrimary));
        mBottomLayout.setExCircleColor(getResources().getColor(R.color.colorTransPrimary));
        mBottomLayout.setList(list);
        mBottomLayout.setSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case 0:
                        fragment = HomeFragment.newInstacne();
                        break;
                    case 1:
                        fragment = DeviceFragment.newInstance();
                        break;
                    case 2:
                        fragment = HudongFragment.newInstance();
                        break;
                }
                changeFragment(fragment);
            }
        });
    }

    @Override
    public void initTheme() {

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

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLasClickTime < 2000) {
            super.onBackPressed();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(mContext, "再按一次退出键退出");
                }
            });
            mLasClickTime = currentTime;
        }

    }
}
