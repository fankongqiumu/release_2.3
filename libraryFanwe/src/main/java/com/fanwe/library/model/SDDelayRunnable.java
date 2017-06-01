package com.fanwe.library.model;

import com.fanwe.library.common.SDHandlerManager;

/**
 * 延迟执行Runnable
 */
public abstract class SDDelayRunnable implements Runnable
{

    public final void runDelay(long delay)
    {
        removeDelay();
        SDHandlerManager.getMainHandler().postDelayed(this, delay);
    }

    public final void removeDelay()
    {
        SDHandlerManager.getMainHandler().removeCallbacks(this);
        SDHandlerManager.getBackgroundHandler().removeCallbacks(this);
    }
}
