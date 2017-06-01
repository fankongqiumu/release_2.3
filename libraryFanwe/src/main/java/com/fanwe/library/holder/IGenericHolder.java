package com.fanwe.library.holder;

/**
 * Created by Administrator on 2017/3/16.
 */

public interface IGenericHolder<T>
{
    T get();

    void set(T generic);

    boolean isNotEmpty();
}
