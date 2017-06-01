package com.fanwe.library.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public class SDGestureHandler
{
    protected Context context;
    private ViewConfiguration viewConfiguration;
    private VelocityTracker velocityTracker;
    private GestureDetector gestureDetector;
    private SDScroller scroller;
    private boolean isScrollVertical = false;
    private boolean isScrollHorizontal = false;
    private SDGestureListener gestureListener;

    public SDGestureHandler(Context context)
    {
        this.context = context;
        this.viewConfiguration = ViewConfiguration.get(context);
        scroller = new SDScroller(context);
        gestureDetector = new GestureDetector(context, defaultGestureListener);
    }

    private GestureDetector.SimpleOnGestureListener defaultGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onDown(MotionEvent e)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onDown(e))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onSingleTapUp(e))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onSingleTapConfirmed(e))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onDoubleTap(e))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onDoubleTapEvent(e))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (gestureListener != null)
            {
                if (gestureListener.onFling(e1, e2, velocityX, velocityY))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {

            getVelocityTracker().addMovement(e2);
            getVelocityTracker().computeCurrentVelocity(1000);

            if (gestureListener != null)
            {
                if (gestureListener.onScroll(e1, e2, distanceX, distanceY))
                {
                    setScrollVertical(false);
                    setScrollHorizontal(false);
                    return true;
                }
            }

            if (isScrollVertical)
            {
                if (gestureListener != null)
                {
                    if (gestureListener.onScrollVertical(e1, e2, distanceX, distanceY))
                    {
                        return true;
                    }
                }
            } else if (isScrollHorizontal)
            {
                if (gestureListener != null)
                {
                    if (gestureListener.onScrollHorizontal(e1, e2, distanceX, distanceY))
                    {
                        return true;
                    }
                }
            } else
            {
                float dx = Math.abs(e1.getRawX() - e2.getRawX());
                float dy = Math.abs(e1.getRawY() - e2.getRawY());
                int touchSlop = getScaledTouchSlop();

                if (dx > dy)
                {
                    // 水平方向
                    if (dx > touchSlop)
                    {
                        setScrollHorizontal(true);
                    }
                } else
                {
                    // 竖直方向
                    if (dy > touchSlop)
                    {
                        setScrollVertical(true);
                    }
                }
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            if (gestureListener != null)
            {
                gestureListener.onLongPress(e);
            }
        }

        @Override
        public void onShowPress(MotionEvent e)
        {
            if (gestureListener != null)
            {
                gestureListener.onShowPress(e);
            }
        }
    };

    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (gestureListener != null)
            {
                float velocityX = getVelocityTracker().getXVelocity();
                float velocityY = getVelocityTracker().getYVelocity();
                gestureListener.onActionUp(event, velocityX, velocityY);
            }
            cancel();
        }

        return gestureDetector.onTouchEvent(event);
    }

    public SDScroller getScroller()
    {
        return scroller;
    }

    private void setScrollHorizontal(boolean isScrollHorizontal)
    {
        this.isScrollHorizontal = isScrollHorizontal;
        if (isScrollHorizontal)
        {
            isScrollVertical = false;
        }
    }

    private void setScrollVertical(boolean isScrollVertical)
    {
        this.isScrollVertical = isScrollVertical;
        if (isScrollVertical)
        {
            isScrollHorizontal = false;
        }
    }

    public void cancel()
    {
        isScrollVertical = false;
        isScrollHorizontal = false;
        recyleVelocityTracker();
    }

    public boolean isScrollHorizontal()
    {
        return isScrollHorizontal;
    }

    public boolean isScrollVertical()
    {
        return isScrollVertical;
    }

    public static int getDurationPercent(float distance, float maxDistance, long maxDuration)
    {
        int result = 0;

        float percent = Math.abs(distance) / Math.abs(maxDistance);
        float duration = percent * (float) maxDuration;

        result = (int) duration;
        return result;
    }

    public int getScaledTouchSlop()
    {
        return viewConfiguration.getScaledTouchSlop();
    }

    public int getScaledMinimumFlingVelocityCommon()
    {
        return viewConfiguration.getScaledMinimumFlingVelocity() * 21;
    }

    public VelocityTracker getVelocityTracker()
    {
        if (velocityTracker == null)
        {
            velocityTracker = VelocityTracker.obtain();
        }
        return velocityTracker;
    }

    public void recyleVelocityTracker()
    {
        if (velocityTracker != null)
        {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    public ViewConfiguration getViewConfiguration()
    {
        return viewConfiguration;
    }

    public Context getContext()
    {
        return context;
    }

    public void setGestureListener(SDGestureListener gestureListener)
    {
        this.gestureListener = gestureListener;
    }

    public static class SDGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        public boolean onScrollVertical(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return false;
        }

        public boolean onScrollHorizontal(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return false;
        }

        public void onActionUp(MotionEvent event, float velocityX, float velocityY)
        {

        }
    }

}
