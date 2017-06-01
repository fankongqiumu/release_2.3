package com.fanwe.library.utils;

import com.fanwe.library.model.SDDelayRunnable;

/**
 * Created by Administrator on 2016/8/19.
 */
public class SDRunnableTryer
{
    private static final int DEFAULT_TRY_COUNT = 3;

    private int maxTryCount = DEFAULT_TRY_COUNT;
    private int tryCount = 0;
    private Runnable runnable;

    /**
     * 设置重试次数
     *
     * @param maxTryCount
     * @return
     */
    public SDRunnableTryer setMaxTryCount(int maxTryCount)
    {
        this.maxTryCount = maxTryCount;
        return this;
    }

    public int getMaxTryCount()
    {
        return maxTryCount;
    }

    public int getTryCount()
    {
        return tryCount;
    }

    public boolean tryRun(Runnable runnable)
    {
        return tryRunDelayed(runnable, 0);
    }

    /**
     * 返回false：超过重试次数
     *
     * @param delay 延迟多久执行
     * @return
     */
    public boolean tryRunDelayed(Runnable runnable, long delay)
    {
        if (runnable == null)
        {
            return false;
        }
        this.runnable = runnable;
        tryCount++;
        if (tryCount > maxTryCount)
        {
            //超过最大重试次数，不处理
            return false;
        } else
        {
            realRunnable.runDelay(delay);
            return true;
        }
    }

    private SDDelayRunnable realRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            if (runnable != null)
            {
                runnable.run();
            }
        }
    };

    public void reset()
    {
        tryCount = 0;
    }

    public void onDestroy()
    {
        realRunnable.removeDelay();
        runnable = null;
    }

}
