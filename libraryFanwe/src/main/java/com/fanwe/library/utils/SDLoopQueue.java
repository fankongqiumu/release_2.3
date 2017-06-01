package com.fanwe.library.utils;

import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/1/4.
 */

public class SDLoopQueue<T>
{
    private SDLooper looper = new SDSimpleLooper();
    private LinkedList<T> queue = new LinkedList<>();
    private SDLoopQueueListener<T> listener;

    public void setListener(SDLoopQueueListener<T> listener)
    {
        this.listener = listener;
    }

    public int size()
    {
        return queue.size();
    }

    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public void offer(T model)
    {
        if (model == null)
        {
            return;
        }
        queue.offer(model);
    }

    public T poll()
    {
        return queue.poll();
    }

    public void startLoop(long period)
    {
        if (looper.isRunning())
        {
            looper.setPeriod(period);
        } else
        {
            looper.start(period, new Runnable()
            {
                @Override
                public void run()
                {
                    if (listener != null)
                    {
                        listener.onLoop(queue);
                    } else
                    {
                        stopLoop();
                    }
                }
            });
        }
    }

    public void stopLoop()
    {
        looper.stop();
    }

    public void stopLoopIfEmpty()
    {
        if (isEmpty())
        {
            stopLoop();
        }
    }

    public interface SDLoopQueueListener<T>
    {
        void onLoop(LinkedList<T> queue);
    }
}
