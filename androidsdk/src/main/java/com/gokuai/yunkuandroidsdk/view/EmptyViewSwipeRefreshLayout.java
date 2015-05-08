package com.gokuai.yunkuandroidsdk.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class EmptyViewSwipeRefreshLayout extends SwipeRefreshLayout {

    public EmptyViewSwipeRefreshLayout(Context context) {
        super(context);
    }

    public EmptyViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        // find content view
        ViewGroup target = null;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView) {
                continue;
            }
            target = (ViewGroup) child;
        }

        if (target == null) {
            return false;
        }

        // check if adapter view is visible
        View scrollableView = target.getChildAt(1);
        if (scrollableView.getVisibility() == GONE) {
            // use empty view layout instead
            scrollableView = target.getChildAt(0);
        }

        return ViewCompat.canScrollVertically(scrollableView, -1);
    }

}
