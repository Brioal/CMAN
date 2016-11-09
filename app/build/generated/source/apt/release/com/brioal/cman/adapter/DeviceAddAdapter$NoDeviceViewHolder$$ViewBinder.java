// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceAddAdapter$NoDeviceViewHolder$$ViewBinder<T extends com.brioal.cman.adapter.DeviceAddAdapter.NoDeviceViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493060, "field 'mNoDataTv'");
    target.mNoDataTv = finder.castView(view, 2131493060, "field 'mNoDataTv'");
  }

  @Override public void unbind(T target) {
    target.mNoDataTv = null;
  }
}
