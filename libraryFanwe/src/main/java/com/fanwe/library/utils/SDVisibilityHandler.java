package com.fanwe.library.utils;

import android.animation.Animator;
import android.view.View;

import com.fanwe.library.listener.SDIterateListener;
import com.fanwe.library.listener.SDVisibilityStateListener;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/8/4.
 */
public class SDVisibilityHandler
{

    private View view;

    private Animator showAnimator;
    private Animator hideAnimator;

    private boolean isInShowAnimator;
    private boolean isInHideAnimator;

    private boolean hideMode = true;

    private SDListenerManager<SDVisibilityStateListener> listenerManager;

    public SDVisibilityHandler()
    {
        listenerManager = new SDListenerManager<>();
    }

    public void setView(View view)
    {
        this.view = view;
    }

    public void addVisibilityStateListener(SDVisibilityStateListener listener)
    {
        listenerManager.add(listener);
    }

    public void removeVisibilityStateListener(SDVisibilityStateListener listener)
    {
        listenerManager.remove(listener);
    }

    public void clearVisibilityStateListener()
    {
        listenerManager.clear();
    }

    public void setShowAnimator(Animator showAnimator)
    {
        this.showAnimator = showAnimator;
        if (showAnimator != null)
        {
            showAnimator.addListener(showListener);
        }
    }

    public void setHideAnimator(Animator hideAnimator)
    {
        this.hideAnimator = hideAnimator;
        if (hideAnimator != null)
        {
            hideAnimator.addListener(hideListener);
        }
    }

    //show
    public void show(boolean anim)
    {
        if (view.getVisibility() == View.VISIBLE)
        {
            return;
        }

        if (anim)
        {
            startShowAnimator();
        } else
        {
            showViewInside();
        }
    }

    private void showViewInside()
    {
        SDViewUtil.show(view);
        notifyVisibleStateListener();
    }

    private void startShowAnimator()
    {
        if (isInShowAnimator)
        {
            return;
        }

        if (showAnimator != null)
        {
            isInShowAnimator = true;
            showAnimator.start();
        } else
        {
            showViewInside();
        }
    }

    // hide
    public void hide(boolean anim)
    {
        if (view.getVisibility() == View.GONE)
        {
            return;
        }

        if (anim)
        {
            startHideAnimator(true);
        } else
        {
            hideViewInside();
        }
    }

    private void hideViewInside()
    {
        SDViewUtil.hide(view);
        notifyVisibleStateListener();
    }

    private void startHideAnimator(boolean hideMode)
    {
        if (isInHideAnimator)
        {
            return;
        }

        this.hideMode = hideMode;
        if (hideAnimator != null)
        {
            isInHideAnimator = true;
            hideAnimator.start();
        } else
        {
            if (hideMode)
            {
                hideViewInside();
            } else
            {
                invisibleViewInside();
            }
        }
    }

    // invisible
    public void invisible(boolean anim)
    {
        if (view.getVisibility() == View.INVISIBLE)
        {
            return;
        }

        if (anim)
        {
            startHideAnimator(false);
        } else
        {
            invisibleViewInside();
        }
    }

    private void invisibleViewInside()
    {
        SDViewUtil.invisible(view);
        notifyVisibleStateListener();
    }

    /**
     * 显示监听
     */
    private Animator.AnimatorListener showListener = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animation)
        {
            isInShowAnimator = true;
            showViewInside();
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
            isInShowAnimator = false;
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {
            isInShowAnimator = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {
        }
    };

    /**
     * 隐藏监听
     */
    private Animator.AnimatorListener hideListener = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animation)
        {
            isInHideAnimator = true;
        }

        @Override
        public void onAnimationEnd(Animator animation)
        {
            isInHideAnimator = false;
            if (hideMode)
            {
                hideViewInside();
            } else
            {
                invisibleViewInside();
            }
            SDViewUtil.resetView(view);
        }

        @Override
        public void onAnimationCancel(Animator animation)
        {
            isInHideAnimator = false;
            if (hideMode)
            {
                hideViewInside();
            } else
            {
                invisibleViewInside();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {
        }
    };

    public final void notifyVisibleStateListener()
    {
        if (view != null)
        {
            listenerManager.foreach(new SDIterateListener<SDVisibilityStateListener>()
            {
                @Override
                public boolean next(int i, SDVisibilityStateListener item, Iterator<SDVisibilityStateListener> it)
                {
                    switch (view.getVisibility())
                    {
                        case View.GONE:
                            item.onGone(view);
                            break;
                        case View.VISIBLE:
                            item.onVisible(view);
                            break;
                        case View.INVISIBLE:
                            item.onInvisible(view);
                            break;

                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }


    public Animator getShowAnimator()
    {
        return showAnimator;
    }


    public Animator getHideAnimator()
    {
        return hideAnimator;
    }

}
