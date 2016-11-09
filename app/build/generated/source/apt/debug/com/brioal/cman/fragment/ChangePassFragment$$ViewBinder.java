// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ChangePassFragment$$ViewBinder<T extends com.brioal.cman.fragment.ChangePassFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493025, "field 'mBtnClose'");
    target.mBtnClose = finder.castView(view, 2131493025, "field 'mBtnClose'");
    view = finder.findRequiredView(source, 2131493026, "field 'mBtnDone'");
    target.mBtnDone = finder.castView(view, 2131493026, "field 'mBtnDone'");
    view = finder.findRequiredView(source, 2131493027, "field 'mEtOld'");
    target.mEtOld = finder.castView(view, 2131493027, "field 'mEtOld'");
    view = finder.findRequiredView(source, 2131493028, "field 'mEtNew'");
    target.mEtNew = finder.castView(view, 2131493028, "field 'mEtNew'");
  }

  @Override public void unbind(T target) {
    target.mBtnClose = null;
    target.mBtnDone = null;
    target.mEtOld = null;
    target.mEtNew = null;
  }
}
