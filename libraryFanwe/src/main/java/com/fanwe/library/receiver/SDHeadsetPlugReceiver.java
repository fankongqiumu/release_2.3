package com.fanwe.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.fanwe.library.listener.SDIterateListener;
import com.fanwe.library.utils.SDListenerManager;

import java.util.Iterator;

/**
 * 耳机插拔监听
 */
public class SDHeadsetPlugReceiver extends BroadcastReceiver
{

    private static BroadcastReceiver receiver;
    private static SDListenerManager<HeadsetPlugListener> listenerManager = new SDListenerManager();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        receiver = this;

        if (intent.hasExtra("state"))
        {
            int state = intent.getIntExtra("state", 0);

            final boolean isHeadsetPlug = state == 1;

            listenerManager.foreach(new SDIterateListener<HeadsetPlugListener>()
            {
                @Override
                public boolean next(int i, HeadsetPlugListener item, Iterator<HeadsetPlugListener> it)
                {
                    item.onHeadsetPlugChange(isHeadsetPlug);
                    return false;
                }
            });
        }
    }

    public static BroadcastReceiver getReceiver()
    {
        if (receiver == null)
        {
            receiver = new SDHeadsetPlugReceiver();
        }
        return receiver;
    }

    public static void registerReceiver(Context context)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        context.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    public static void unregisterReceiver(Context context)
    {
        if (receiver != null)
        {
            try
            {
                context.getApplicationContext().unregisterReceiver(receiver);
            } catch (Exception e)
            {
            }
        }
    }

    public static void addListener(HeadsetPlugListener listener)
    {
        listenerManager.add(listener);
    }

    public static void removeListener(HeadsetPlugListener listener)
    {
        listenerManager.remove(listener);
    }

    public interface HeadsetPlugListener
    {
        void onHeadsetPlugChange(boolean plug);
    }

}
