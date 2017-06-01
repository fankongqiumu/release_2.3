package com.fanwe.library.gesture;

import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/3/24.
 */

public abstract class SDViewDragCallback extends Callback
{
    @Override
    public boolean tryCaptureView(View child, int pointerId)
    {
        if (hasCapturedView())
        {
            return true;
        } else
        {
            if (View.VISIBLE == child.getVisibility())
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx)
    {
        int leftBound = getParentView().getPaddingLeft();
        int rightBound = getParentView().getWidth() - child.getWidth() - leftBound;

        int newLeft = Math.min(Math.max(left, leftBound), rightBound);
        return newLeft;
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy)
    {
        int topBound = getParentView().getPaddingTop();
        int botBound = getParentView().getHeight() - child.getHeight() - topBound;

        int newTop = Math.min(Math.max(top, topBound), botBound);
        return newTop;
    }

    @Override
    public int getViewHorizontalDragRange(View child)
    {
        return getParentView().getMeasuredWidth() - child.getMeasuredWidth();
    }

    @Override
    public int getViewVerticalDragRange(View child)
    {
        return getParentView().getMeasuredHeight() - child.getMeasuredHeight();
    }


    public abstract ViewGroup getParentView();

    public abstract ViewDragHelper getViewDragHelper();

    public boolean hasCapturedView()
    {
        return getViewDragHelper().getCapturedView() != null && View.VISIBLE == getViewDragHelper().getCapturedView().getVisibility();
    }

}
