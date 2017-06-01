package com.fanwe.live.model;

import android.text.TextUtils;

import com.fanwe.library.common.SDSelectManager.SDSelectable;

/**
 * 直播间插件菜单实体
 */
public class MenuConfig implements SDSelectable
{
    private int id;

    private int type;
    /**
     * 正常本地图标
     */
    private int imageResIdNormal;
    /**
     * 选中本地图标
     */
    private int imageResIdSelected;
    /**
     * 正常url图标
     */
    private String imageUrlNormal;
    /**
     * 选中url图标
     */
    private String imageUrlSelected;
    /**
     * 正常颜色
     */
    private int textColorResIdNormal;
    /**
     * 选中颜色
     */
    private int textColorResIdSelected;
    /**
     * 正常文字
     */
    private String textNormal;
    /**
     * 选中文字
     */
    private String textSelected;
    /**
     * 未读文字
     */
    private String textUnread;
    /**
     * 未读背景
     */
    private int backgroundUnreadResId;
    /**
     * 是否可见
     */
    private boolean visible = true;
    /**
     * 是否可用
     */
    private boolean enable = true;
    /**
     * 是否选中
     */
    private boolean selected = false;
    /**
     * 是否未读
     */
    private boolean unread = false;

    /**
     * 切换选中状态
     *
     * @return 返回切换后的状态
     */
    public boolean toggleSelected()
    {
        this.selected = !this.selected;
        return this.selected;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTextUnread()
    {
        return textUnread;
    }

    public void setTextUnread(String textUnread)
    {
        this.textUnread = textUnread;
    }

    public int getBackgroundUnreadResId()
    {
        return backgroundUnreadResId;
    }

    public void setBackgroundUnreadResId(int backgroundUnreadResId)
    {
        this.backgroundUnreadResId = backgroundUnreadResId;
    }

    public void setUnread(boolean unread)
    {
        this.unread = unread;
    }

    public boolean isUnread()
    {
        return unread;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public String getImageUrlNormal()
    {
        return imageUrlNormal;
    }

    public void setImageUrlNormal(String imageUrlNormal)
    {
        this.imageUrlNormal = imageUrlNormal;
        if (TextUtils.isEmpty(imageUrlSelected))
        {
            this.imageUrlSelected = imageUrlNormal;
        }
    }

    public String getImageUrlSelected()
    {
        return imageUrlSelected;
    }

    public void setImageUrlSelected(String imageUrlSelected)
    {
        this.imageUrlSelected = imageUrlSelected;
    }

    public int getTextColorResIdNormal()
    {
        return textColorResIdNormal;
    }

    public void setTextColorResIdNormal(int textColorResIdNormal)
    {
        this.textColorResIdNormal = textColorResIdNormal;
        if (this.textColorResIdSelected == 0)
        {
            this.textColorResIdSelected = textColorResIdNormal;
        }
    }

    public int getTextColorResIdSelected()
    {
        return textColorResIdSelected;
    }

    public void setTextColorResIdSelected(int textColorResIdSelected)
    {
        this.textColorResIdSelected = textColorResIdSelected;
    }

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public int getImageResIdNormal()
    {
        return imageResIdNormal;
    }

    public void setImageResIdNormal(int imageResIdNormal)
    {
        this.imageResIdNormal = imageResIdNormal;
        if (this.imageResIdSelected == 0)
        {
            this.imageResIdSelected = imageResIdNormal;
        }
    }

    public int getImageResIdSelected()
    {
        return imageResIdSelected;
    }

    public void setImageResIdSelected(int imageResIdSelected)
    {
        this.imageResIdSelected = imageResIdSelected;
    }

    public String getTextNormal()
    {
        return textNormal;
    }

    public void setTextNormal(String textNormal)
    {
        this.textNormal = textNormal;
        if (TextUtils.isEmpty(textSelected))
        {
            this.textSelected = textNormal;
        }
    }

    public String getTextSelected()
    {
        return textSelected;
    }

    public void setTextSelected(String textSelected)
    {
        this.textSelected = textSelected;
    }

    public boolean isEnable()
    {
        return enable;
    }

    public void setEnable(boolean enable)
    {
        this.enable = enable;
    }

}
