package com.fanwe.library.holder;

/**
 * Created by Administrator on 2017/3/16.
 */
public class GenericHolder<T> implements IGenericHolder<T>
{
    private T generic;

    @Override
    public T get()
    {
        return generic;
    }

    @Override
    public void set(T generic)
    {
        this.generic = generic;
    }

    @Override
    public boolean isNotEmpty()
    {
        return generic != null;
    }
}
