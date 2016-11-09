// Generated code from Butter Knife. Do not modify!
package com.brioal.cman;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.brioal.cman.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493009, "field 'mBottomLayout'");
    target.mBottomLayout = finder.castView(view, 2131493009, "field 'mBottomLayout'");
  }

  @Override public void unbind(T target) {
    target.mBottomLayout = null;
  }
}
