package com.fanwe.library.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by Administrator on 2016/7/14.
 */
public class SDHandlerManager
{

    private final static Object lock = new Object();

    private final static HandlerThread thread = new HandlerThread("handler thread");

    private final static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Handler backgroundHandler;

    private static boolean isBackgroundHandlerStart = false;

    public final static Handler getMainHandler()
    {
        return mainHandler;
    }

    public final static Handler getBackgroundHandler()
    {
        startBackgroundHandler();
        return backgroundHandler;
    }

    /**
     * 开始后台线程
     */
    public final static void startBackgroundHandler()
    {
        synchronized (lock)
        {
            if (!isBackgroundHandlerStart)
            {
                thread.start();
                backgroundHandler = new Handler(thread.getLooper());
                isBackgroundHandlerStart = true;
            }
        }
    }

    /**
     * 结束后台线程
     */
    public final static void stopBackgroundHandler()
    {
        synchronized (lock)
        {
            if (isBackgroundHandlerStart)
            {
                thread.quit();
                isBackgroundHandlerStart = false;
            }
        }
    }

    /**
     * 主线程执行
     *
     * @param r
     */
    public final static void postMain(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        if (Looper.getMainLooper() == Looper.myLooper())
        {
            r.run();
        } else
        {
            getMainHandler().post(r);
        }
    }

    /**
     * 主线程执行
     *
     * @param r
     * @param delayMillis 延迟毫秒
     */
    public final static void postMainDelayed(Runnable r, long delayMillis)
    {
        if (r == null)
        {
            return;
        }
        getMainHandler().postDelayed(r, delayMillis);
    }

    /**
     * 移除主线程任务
     *
     * @param r
     */
    public final static void removeMain(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        getMainHandler().removeCallbacks(r);
    }

    /**
     * 后台线程执行
     *
     * @param r
     */
    public final static void postBack(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        getBackgroundHandler().post(r);
    }

    /**
     * 后台线程执行
     *
     * @param r
     * @param delayMillis 延迟毫秒
     */
    public final static void postBackDelayed(Runnable r, long delayMillis)
    {
        if (r == null)
        {
            return;
        }
        getBackgroundHandler().postDelayed(r, delayMillis);
    }

    /**
     * 移除后台线程任务
     *
     * @param r
     */
    public final static void removeBack(Runnable r)
    {
        if (r == null)
        {
            return;
        }
        getBackgroundHandler().removeCallbacks(r);
    }

}
