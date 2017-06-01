package com.fanwe.library.looper.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fanwe.library.looper.SDLooper;

public class SDSimpleLooper implements SDLooper
{
    private static final int MSG_WHAT = 1990;

    private Runnable runnable;
    private long period;
    private boolean isRunning = false;
    private boolean isCancelled = false;
    private Handler handler;

    public SDSimpleLooper()
    {
        this(Looper.getMainLooper());
    }

    public SDSimpleLooper(Looper looper)
    {
        handler = new Handler(looper)
        {
            public void handleMessage(Message msg)
            {
                synchronized (SDSimpleLooper.this)
                {
                    if (isCancelled)
                    {
                        return;
                    }

                    if (runnable != null)
                    {
                        runnable.run();
                        SDSimpleLooper.this.sendMessageDelayed(period);
                    } else
                    {
                        stop();
                    }
                }
            }
        };
    }

    @Override
    public boolean isRunning()
    {
        return isRunning;
    }

    @Override
    public synchronized void setPeriod(long period)
    {
        this.period = period;
        if (this.period <= 0)
        {
            this.period = DEFAULT_PERIOD;
        }
    }

    @Override
    public SDLooper start(Runnable runnable)
    {
        start(0, DEFAULT_PERIOD, runnable);
        return this;
    }

    @Override
    public SDLooper start(long period, Runnable runnable)
    {
        start(0, period, runnable);
        return this;
    }

    @Override
    public synchronized SDLooper start(long delay, long period, Runnable runnable)
    {
        stop();

        this.isRunning = true;
        this.isCancelled = false;
        this.runnable = runnable;

        setPeriod(period);

        sendMessageDelayed(delay);
        return this;
    }

    private void sendMessageDelayed(long delay)
    {
        if (delay < 0)
        {
            delay = 0;
        }
        Message msg = handler.obtainMessage(MSG_WHAT);
        handler.sendMessageDelayed(msg, delay);
    }

    @Override
    public synchronized SDLooper stop()
    {
        handler.removeMessages(MSG_WHAT);
        isRunning = false;
        isCancelled = true;
        return this;
    }
}
