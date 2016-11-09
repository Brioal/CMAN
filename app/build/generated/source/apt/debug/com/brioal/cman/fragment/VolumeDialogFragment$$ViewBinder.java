// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class VolumeDialogFragment$$ViewBinder<T extends com.brioal.cman.fragment.VolumeDialogFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493049, "field 'mBtnClose'");
    target.mBtnClose = finder.castView(view, 2131493049, "field 'mBtnClose'");
    view = finder.findRequiredView(source, 2131493050, "field 'mBtnDone'");
    target.mBtnDone = finder.castView(view, 2131493050, "field 'mBtnDone'");
    view = finder.findRequiredView(source, 2131493051, "field 'mTvCurrent'");
    target.mTvCurrent = finder.castView(view, 2131493051, "field 'mTvCurrent'");
    view = finder.findRequiredView(source, 2131493052, "field 'mSeek'");
    target.mSeek = finder.castView(view, 2131493052, "field 'mSeek'");
  }

  @Override public void unbind(T target) {
    target.mBtnClose = null;
    target.mBtnDone = null;
    target.mTvCurrent = null;
    target.mSeek = null;
  }
}
