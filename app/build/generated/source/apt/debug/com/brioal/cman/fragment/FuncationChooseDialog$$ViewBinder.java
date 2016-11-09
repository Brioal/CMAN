// Generated code from Butter Knife. Do not modify!
package com.brioal.cman.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FuncationChooseDialog$$ViewBinder<T extends com.brioal.cman.fragment.FuncationChooseDialog> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493036, "field 'mBtnClose'");
    target.mBtnClose = finder.castView(view, 2131493036, "field 'mBtnClose'");
    view = finder.findRequiredView(source, 2131493046, "field 'mSWChushi'");
    target.mSWChushi = finder.castView(view, 2131493046, "field 'mSWChushi'");
    view = finder.findRequiredView(source, 2131493047, "field 'mSWChuchou'");
    target.mSWChuchou = finder.castView(view, 2131493047, "field 'mSWChuchou'");
    view = finder.findRequiredView(source, 2131493048, "field 'mSWJiare'");
    target.mSWJiare = finder.castView(view, 2131493048, "field 'mSWJiare'");
  }

  @Override public void unbind(T target) {
    target.mBtnClose = null;
    target.mSWChushi = null;
    target.mSWChuchou = null;
    target.mSWJiare = null;
  }
}
