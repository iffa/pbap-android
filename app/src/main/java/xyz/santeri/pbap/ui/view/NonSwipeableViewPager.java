package xyz.santeri.pbap.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ViewPager whose swipe abilities can be disabled.
 *
 * @author Santeri 'iffa'
 */
public class NonSwipeableViewPager extends ViewPager {
    private boolean isPagingEnabled = false;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    @SuppressWarnings("unused")
    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

}
