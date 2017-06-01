package com.fanwe.library.utils;

/**
 * Created by Administrator on 2016/11/16.
 */

public abstract class SDThread extends Thread
{

    private boolean isPause = false;
    private boolean isStop = false;

    private boolean isLoop = false;

    public final boolean isLoop()
    {
        return isLoop;
    }

    public final boolean isPause()
    {
        return isPause;
    }

    public final boolean isStop()
    {
        return isStop || isInterrupted();
    }

    /**
     * 暂停线程
     */
    public final void pauseThread()
    {
        synchronized (this)
        {
            isPause = true;
        }
    }

    /**
     * 恢复线程
     */
    public final void resumeThread()
    {
        synchronized (this)
        {
            isPause = false;
            notify();
        }
    }

    /**
     * 恢复线程（所有持有当前锁的线程）
     */
    public final void resumeThreadAll()
    {
        synchronized (this)
        {
            isPause = false;
            notifyAll();
        }
    }

    /**
     * 暂停线程
     */
    protected final void waitThread()
    {
        synchronized (this)
        {
            try
            {
                wait();
            } catch (Exception e)
            {
                onException(e);
            }
        }
    }

    /**
     * 停止线程
     */
    public final void stopThread()
    {
        synchronized (this)
        {
            isLoop = false;
            isPause = false;
            isStop = true;
            interrupt();
            notifyAll();
        }
    }

    /**
     * 开始线程循环（会不断触发runBackground()方法）
     */
    public synchronized void startLoop()
    {
        synchronized (this)
        {
            isLoop = true;
        }
        start();
    }

    @Override
    public final void run()
    {
        try
        {
            do
            {
                if (isPause)
                {
                    onPause();
                }

                // 此处下方的流程不放在else里面，onPause()里面应该让线程暂停不能执行下方的代码
                super.run();
                runBackground();

            } while (isLoop && !isStop());
        } catch (Exception e)
        {
            onException(e);
        }
    }

    protected void onPause()
    {
        waitThread();
    }

    protected abstract void runBackground() throws Exception;

    protected void onException(Exception e)
    {

    }
}
