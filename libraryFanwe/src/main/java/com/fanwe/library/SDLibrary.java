package com.fanwe.library;

import android.app.Application;

import com.fanwe.library.common.SDCookieManager;
import com.fanwe.library.config.SDConfig;
import com.fanwe.library.config.SDLibraryConfig;
import com.fanwe.library.utils.SDCache;

public class SDLibrary
{

    private static SDLibrary instance;
    private Application application;

    private SDLibraryConfig config;

    public SDLibraryConfig getConfig()
    {
        return config;
    }

    public void setConfig(SDLibraryConfig config)
    {
        this.config = config;
    }

    private SDLibrary()
    {
        config = new SDLibraryConfig();
    }

    public Application getApplication()
    {
        return application;
    }

    public void init(Application application)
    {
        this.application = application;
        initDefaultConfig();
        SDConfig.getInstance().init(application);
        SDCookieManager.getInstance().init(application);
        SDCache.init(application);
    }

    private void initDefaultConfig()
    {
        int main_color = application.getResources().getColor(R.color.main_color);
        int main_color_press = application.getResources().getColor(R.color.main_color_press);

        int bg_title_bar = application.getResources().getColor(R.color.bg_title_bar);
        int bg_title_bar_pressed = application.getResources().getColor(R.color.bg_title_bar_pressed);

        int stroke = application.getResources().getColor(R.color.stroke);
        int gray_press = application.getResources().getColor(R.color.gray_press);

        int text_title_bar = application.getResources().getColor(R.color.text_title_bar);

        int height_title_bar = application.getResources().getDimensionPixelOffset(R.dimen.height_title_bar);
        int corner = application.getResources().getDimensionPixelOffset(R.dimen.corner);
        int width_stroke = application.getResources().getDimensionPixelOffset(R.dimen.width_stroke);

        config.setMainColor(main_color);
        config.setMainColorPress(main_color_press);
        config.setGrayPressColor(gray_press);
        config.setTitleColor(bg_title_bar);
        config.setTitleColorPressed(bg_title_bar_pressed);
        config.setTitleTextColor(text_title_bar);
        config.setTitleHeight(height_title_bar);
        config.setStrokeColor(stroke);
        config.setStrokeWidth(width_stroke);
        config.setCornerRadius(corner);
    }

    public static SDLibrary getInstance()
    {
        if (instance == null)
        {
            instance = new SDLibrary();
        }
        return instance;
    }

}
