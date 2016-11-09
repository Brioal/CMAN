// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SettingDialogFragment$$ViewBinder<T extends com.brioal.cman.fragment.SettingDialogFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493032, "field 'mTablayout'");
    target.mTablayout = finder.castView(view, 2131493032, "field 'mTablayout'");
    view = finder.findRequiredView(source, 2131493034, "field 'mBaseRecycler'");
    target.mBaseRecycler = finder.castView(view, 2131493034, "field 'mBaseRecycler'");
    view = finder.findRequiredView(source, 2131493035, "field 'mAdvanceRecycler'");
    target.mAdvanceRecycler = finder.castView(view, 2131493035, "field 'mAdvanceRecycler'");
    view = finder.findRequiredView(source, 2131493033, "field 'mScrollview'");
    target.mScrollview = finder.castView(view, 2131493033, "field 'mScrollview'");
  }

  @Override public void unbind(T target) {
    target.mTablayout = null;
    target.mBaseRecycler = null;
    target.mAdvanceRecycler = null;
    target.mScrollview = null;
  }
}
