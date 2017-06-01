package com.fanwe.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fanwe.library.listener.SDNetStateListener;

/**
 * 网络工具类
 */
public class SDNetWorkUtil
{

    public static enum NetworkType
    {
        Wifi, Mobile, None
    }

    public static ConnectivityManager getConnectivityManager(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context)
    {
        ConnectivityManager manager = getConnectivityManager(context);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null)
        {
            return info.isConnected();
        } else
        {
            return false;
        }
    }

    /**
     * 获得网络类型
     *
     * @param context
     * @return
     */
    public static NetworkType getNetworkType(Context context)
    {
        ConnectivityManager manager = getConnectivityManager(context);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null)
        {
            return NetworkType.None;
        } else
        {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_MOBILE)
            {
                return NetworkType.Mobile;
            } else if (type == ConnectivityManager.TYPE_WIFI)
            {
                return NetworkType.Wifi;
            } else
            {
                return NetworkType.None;
            }
        }
    }

    /**
     * 检查网络
     *
     * @param context
     * @param listener
     */
    public static void checkNet(Context context, SDNetStateListener listener)
    {
        if (listener != null)
        {
            NetworkType type = getNetworkType(context);
            switch (type)
            {
                case Mobile:
                    listener.onMobile();
                    break;
                case None:
                    listener.onNone();
                    break;
                case Wifi:
                    listener.onWifi();
                    break;
                default:
                    break;
            }
        }
    }

}
