package com.fanwe.library.utils;

import com.fanwe.library.model.SDDelayRunnable;

/**
 * 实现某个时段内重复触发相同动作的拦截，当拦截次数超过最大拦截次数后则执行提交的Runnable，在界面销毁的时候需要调用onDestroy()
 */
public class SDRunnableIntercepter
{
    private int intercepteCount = -1;
    private int maxIntercepteCount;
    private Runnable runnable;

    public void setMaxIntercepteCount(int maxIntercepteCount)
    {
        this.maxIntercepteCount = maxIntercepteCount;
    }

    private void setIntercepteCount(int intercepteCount)
    {
        this.intercepteCount = intercepteCount;
        if (intercepteCount > 0)
        {
            LogUtil.i("intercepteCount:" + intercepteCount);
        }
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable
     * @param intercept 拦截间隔
     */
    public void post(Runnable runnable, long intercept)
    {
        this.runnable = runnable;
        if (runnable != null)
        {
            setIntercepteCount(intercepteCount + 1);
            if (maxIntercepteCount > 0 && intercepteCount >= maxIntercepteCount)
            {
                intercept = 0;
            }
            realRunnable.runDelay(intercept);
        } else
        {
            onDestroy();
        }
    }

    private SDDelayRunnable realRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            if (runnable != null)
            {
                setIntercepteCount(-1);
                runnable.run();
                LogUtil.i("runnable run");
            }
        }
    };

    public void onDestroy()
    {
        setIntercepteCount(-1);
        setMaxIntercepteCount(0);
        realRunnable.removeDelay();
    }

}
