// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class EnvirSettingFragment$$ViewBinder<T extends com.brioal.cman.fragment.EnvirSettingFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493036, "field 'mBtnClose'");
    target.mBtnClose = finder.castView(view, 2131493036, "field 'mBtnClose'");
    view = finder.findRequiredView(source, 2131493037, "field 'mBtnDone'");
    target.mBtnDone = finder.castView(view, 2131493037, "field 'mBtnDone'");
    view = finder.findRequiredView(source, 2131493038, "field 'mBarTemp'");
    target.mBarTemp = finder.castView(view, 2131493038, "field 'mBarTemp'");
    view = finder.findRequiredView(source, 2131493039, "field 'mBarHumi'");
    target.mBarHumi = finder.castView(view, 2131493039, "field 'mBarHumi'");
    view = finder.findRequiredView(source, 2131493040, "field 'mRg'");
    target.mRg = finder.castView(view, 2131493040, "field 'mRg'");
    view = finder.findRequiredView(source, 2131493044, "field 'mEnvirAuto'");
    target.mEnvirAuto = finder.castView(view, 2131493044, "field 'mEnvirAuto'");
    view = finder.findRequiredView(source, 2131493045, "field 'mSwitchClience'");
    target.mSwitchClience = finder.castView(view, 2131493045, "field 'mSwitchClience'");
  }

  @Override public void unbind(T target) {
    target.mBtnClose = null;
    target.mBtnDone = null;
    target.mBarTemp = null;
    target.mBarHumi = null;
    target.mRg = null;
    target.mEnvirAuto = null;
    target.mSwitchClience = null;
  }
}
