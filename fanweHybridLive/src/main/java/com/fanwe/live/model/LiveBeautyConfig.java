package com.fanwe.live.model;

import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.utils.SDCache;

/**
 * 美颜相关参数配置
 */
public class LiveBeautyConfig
{
    /**
     * 美颜百分比
     */
    private int beautyProgress = -1;
    /**
     * 美白百分比
     */
    private int whiteProgress;
    /**
     * 美颜滤镜
     */
    private int filterResId;

    private LiveBeautyConfig()
    {

    }

    public static LiveBeautyConfig get()
    {
        LiveBeautyConfig config = SDCache.getObjectJson(LiveBeautyConfig.class);
        if (config == null)
        {
            config = new LiveBeautyConfig();

            //检查是否需要设置初始化返回的美颜百分比
            int beauty = config.getBeautyProgress();
            if (beauty < 0)
            {
                InitActModel initActModel = InitActModelDao.query();
                if (initActModel != null)
                {
                    beauty = initActModel.getBeauty_android();
                }
            }
            if (beauty < 0)
            {
                beauty = 0;
            }
            config.setBeautyProgress(beauty);

            config.save();
        }
        return config;
    }

    public void save()
    {
        SDCache.setObjectJson(this);
    }

    public int getBeautyProgress()
    {
        return beautyProgress;
    }

    public LiveBeautyConfig setBeautyProgress(int beautyProgress)
    {
        this.beautyProgress = beautyProgress;
        return this;
    }

    public int getWhiteProgress()
    {
        return whiteProgress;
    }

    public LiveBeautyConfig setWhiteProgress(int whiteProgress)
    {
        this.whiteProgress = whiteProgress;
        return this;
    }

    public int getFilterResId()
    {
        return filterResId;
    }

    public LiveBeautyConfig setFilterResId(int filterResId)
    {
        this.filterResId = filterResId;
        return this;
    }
}
