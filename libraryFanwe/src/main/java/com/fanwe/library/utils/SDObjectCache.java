package com.fanwe.library.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体内存缓存工具类
 */
public class SDObjectCache
{
    private static Map<String, Object> mapCache = new HashMap<>();

    public static <T> T get(Class<T> clazz)
    {
        synchronized (mapCache)
        {
            if (clazz == null)
            {
                return null;
            }

            String key = clazz.getName();
            Object object = mapCache.get(key);
            if (object != null)
            {
                return (T) object;
            } else
            {
                return null;
            }
        }
    }

    public static <T> void put(T model)
    {
        synchronized (mapCache)
        {
            if (model == null)
            {
                return;
            }

            Class<?> clazz = model.getClass();
            String key = clazz.getName();
            mapCache.put(key, model);
        }
    }

    public static <T> void remove(Class<T> clazz)
    {
        synchronized (mapCache)
        {
            if (clazz == null)
            {
                return;
            }

            String key = clazz.getName();
            if (mapCache.containsKey(key))
            {
                mapCache.remove(key);
            }
        }
    }

    public static void clear()
    {
        synchronized (mapCache)
        {
            mapCache.clear();
        }
    }
}
