package com.fanwe.live.appview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.fanwe.live.model.MenuConfig;

/**
 * 菜单view
 */
public abstract class AMenuView extends BaseAppView
{

    private MenuConfig config;

    public AMenuView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public AMenuView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AMenuView(Context context)
    {
        super(context);
    }

    public final MenuConfig getMenuConfig()
    {
        if (config == null)
        {
            config = new MenuConfig();
        }
        return config;
    }

    public final void setMenuConfig(MenuConfig config)
    {
        if (config != null)
        {
            this.config = config;
            bindData();
        }
    }

    /**
     * 根据MenuConfig绑定数据
     */
    public final void bindData()
    {
        if (config == null)
        {
            hide();
        }
        show();

        if (config.isVisible())
        {
            show();
        } else
        {
            hide();
        }

        setEnabled(config.isEnable());

        if (config.isSelected())
        {
            bindDataByState(config.getImageUrlSelected(),
                    config.getImageResIdSelected(),
                    config.getTextColorResIdSelected(),
                    config.getTextSelected());
        } else
        {
            bindDataByState(config.getImageUrlNormal(),
                    config.getImageResIdNormal(),
                    config.getTextColorResIdNormal(),
                    config.getTextNormal());
        }
    }

    private void bindDataByState(String imageUrl, int imageResId, int textColorResId, String text)
    {
        if (!TextUtils.isEmpty(imageUrl))
        {
            setImageUrl(imageUrl);
        } else
        {
            if (imageResId != 0)
            {
                setImageResId(imageResId);
            }
        }

        if (textColorResId != 0)
        {
            setTextColorResId(textColorResId);
        }

        setText(text);
    }

    /**
     * 设置图片
     *
     * @param url 图片url链接
     */
    public abstract void setImageUrl(String url);

    /**
     * 设置图片
     *
     * @param resId 图片资源id
     */
    public abstract void setImageResId(int resId);

    /**
     * 设置文字描述
     *
     * @param text
     */
    public abstract void setText(String text);

    /**
     * 设置未读
     *
     * @param text null-隐藏未读；空字符串-显示小圆点；其他的原样显示
     */
    public abstract void setTextUnread(String text);

    /**
     * 设置文字颜色
     *
     * @param color 颜色值
     */
    public abstract void setTextColor(int color);

    /**
     * 设置文字颜色
     *
     * @param colorResId 颜色值资源id
     */
    public abstract void setTextColorResId(int colorResId);

    /**
     * 设置菜单大小
     */
    public abstract void setSizeMenu(int width, int height);

    /**
     * 设置图片大小
     */
    public abstract void setSizeImage(int width, int height);

}
