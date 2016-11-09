// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceFragment$$ViewBinder<T extends com.brioal.cman.fragment.DeviceFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493030, "field 'mRefreshLayout'");
    target.mRefreshLayout = finder.castView(view, 2131493030, "field 'mRefreshLayout'");
    view = finder.findRequiredView(source, 2131493031, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131493031, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131493029, "field 'mBtnAdd'");
    target.mBtnAdd = finder.castView(view, 2131493029, "field 'mBtnAdd'");
  }

  @Override public void unbind(T target) {
    target.mRefreshLayout = null;
    target.mRecyclerView = null;
    target.mBtnAdd = null;
  }
}
