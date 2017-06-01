package com.fanwe.library.looper;

public interface SDTimeouter
{
    /**
     * 获得超时时间
     */
    long getTimeout();

    /**
     * 是否超时
     */
    boolean isTimeout();

    /**
     * 设置超时需要执行的Runnable
     */
    SDTimeouter timeout(Runnable timeoutRunnable);

    /**
     * 执行超时需要执行的Runnable
     */
    SDTimeouter runTimeoutRunnable();

    /**
     * 设置超时时间，并且开始计时
     */
    SDTimeouter startTimeout(long timeout);

    /**
     * 停止超时
     */
    SDTimeouter stopTimeout();
}