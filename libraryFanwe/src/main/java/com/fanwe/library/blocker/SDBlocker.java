package com.fanwe.library.blocker;

public class SDBlocker
{

    public static final long DEFAULT_BLOCK_DURATION = 500;

    private long blockDuration;
    private long lastTime;
    private boolean autoSaveLastTime = true;

    public SDBlocker()
    {
        this(DEFAULT_BLOCK_DURATION);
    }

    public SDBlocker(long blockDuration)
    {
        super();
        setBlockDuration(blockDuration);
    }

    public long getLastTime()
    {
        return lastTime;
    }

    public void setAutoSaveLastTime(boolean autoSaveLastTime)
    {
        this.autoSaveLastTime = autoSaveLastTime;
    }

    public void setBlockDuration(long blockDuration)
    {
        if (blockDuration < 0)
        {
            blockDuration = 0;
        }
        this.blockDuration = blockDuration;
    }

    public boolean block()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < blockDuration)
        {
            // 拦截掉
            return true;
        } else
        {
            if (autoSaveLastTime)
            {
                saveLastTime();
            }
            return false;
        }
    }

    public void saveLastTime()
    {
        lastTime = System.currentTimeMillis();
    }

}
