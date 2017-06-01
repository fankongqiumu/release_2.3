package com.fanwe.library.listener;

/**
 * Created by Administrator on 2016/8/16.
 */
public interface SDResultListener<T>
{
    void onSuccess(T result);

    void onError(int code, String msg);
}
