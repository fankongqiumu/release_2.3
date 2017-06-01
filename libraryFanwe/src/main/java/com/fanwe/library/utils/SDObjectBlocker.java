package com.fanwe.library.utils;

import com.fanwe.library.blocker.SDBlocker;

/**
 * Created by Administrator on 2017/1/12.
 */

public class SDObjectBlocker extends SDBlocker
{
    private Object lastObject = new Object();
    private long blockObjectDuration;
    private int maxEqualsCount;
    private int equalsCount;

    public SDObjectBlocker()
    {
        setBlockObjectDuration(5 * 1000);
        setMaxEqualsCount(1);
        setAutoSaveLastTime(false);
    }

    public void setBlockObjectDuration(long blockObjectDuration)
    {
        this.blockObjectDuration = blockObjectDuration;
    }

    public void setMaxEqualsCount(int maxEqualsCount)
    {
        this.maxEqualsCount = maxEqualsCount;
    }

    public void setLastObject(Object lastObject)
    {
        this.lastObject = lastObject;
    }

    public boolean blockObject(Object object)
    {
        if (lastObject.equals(object))
        {
            equalsCount++;
            if (equalsCount > maxEqualsCount)
            {
                if (System.currentTimeMillis() - getLastTime() < blockObjectDuration)
                {
                    return true;
                } else
                {
                    equalsCount = maxEqualsCount;
                }
            }
        } else
        {
            equalsCount = 0;
        }
        LogUtil.i("equalsCount:"+equalsCount);
        setLastObject(object);
        return false;
    }

}
