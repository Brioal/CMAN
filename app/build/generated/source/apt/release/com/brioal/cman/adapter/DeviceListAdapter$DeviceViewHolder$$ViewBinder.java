// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.adapter;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DeviceListAdapter$DeviceViewHolder$$ViewBinder<T extends com.brioal.cman.adapter.DeviceListAdapter.DeviceViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493053, "field 'mIvHead'");
    target.mIvHead = finder.castView(view, 2131493053, "field 'mIvHead'");
    view = finder.findRequiredView(source, 2131493054, "field 'mTvTitle'");
    target.mTvTitle = finder.castView(view, 2131493054, "field 'mTvTitle'");
    view = finder.findRequiredView(source, 2131493055, "field 'mTvState'");
    target.mTvState = finder.castView(view, 2131493055, "field 'mTvState'");
    view = finder.findRequiredView(source, 2131493056, "field 'mBtnChange'");
    target.mBtnChange = finder.castView(view, 2131493056, "field 'mBtnChange'");
  }

  @Override public void unbind(T target) {
    target.mIvHead = null;
    target.mTvTitle = null;
    target.mTvState = null;
    target.mBtnChange = null;
  }
}
