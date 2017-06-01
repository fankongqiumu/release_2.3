package com.fanwe.library.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fanwe.library.gesture.SDViewDragCallback;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SDDragRelativeLayout extends RelativeLayout
{
    private ViewDragHelper dragHelper;

    public SDDragRelativeLayout(Context context)
    {
        super(context);
        init();
    }

    public SDDragRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public SDDragRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        dragHelper = ViewDragHelper.create(this, 1.0f, dragCallback);
    }

    private SDViewDragCallback dragCallback = new SDViewDragCallback()
    {
        @Override
        public ViewGroup getParentView()
        {
            return SDDragRelativeLayout.this;
        }

        @Override
        public ViewDragHelper getViewDragHelper()
        {
            return dragHelper;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean result = super.onTouchEvent(event);

        dragHelper.processTouchEvent(event);
        if (dragCallback.hasCapturedView())
        {
            result = true;
        }
        return result;
    }
}
