// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceAddAdapter$DeviceViewHolder$$ViewBinder<T extends com.brioal.cman.adapter.DeviceAddAdapter.DeviceViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493058, "field 'mTvTitle'");
    target.mTvTitle = finder.castView(view, 2131493058, "field 'mTvTitle'");
    view = finder.findRequiredView(source, 2131493059, "field 'mTvState'");
    target.mTvState = finder.castView(view, 2131493059, "field 'mTvState'");
  }

  @Override public void unbind(T target) {
    target.mTvTitle = null;
    target.mTvState = null;
  }
}
