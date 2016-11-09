// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceAddActivity$$ViewBinder<T extends com.brioal.cman.activity.DeviceAddActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492993, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131492993, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131492992, "field 'mRefreshLayout'");
    target.mRefreshLayout = finder.castView(view, 2131492992, "field 'mRefreshLayout'");
    view = finder.findRequiredView(source, 2131492991, "field 'mBtnBack'");
    target.mBtnBack = finder.castView(view, 2131492991, "field 'mBtnBack'");
  }

  @Override public void unbind(T target) {
    target.mRecyclerView = null;
    target.mRefreshLayout = null;
    target.mBtnBack = null;
  }
}
