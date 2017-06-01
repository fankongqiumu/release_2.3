package com.fanwe.live.model;

import com.fanwe.library.common.SDSelectManager;

/**
 * Created by Administrator on 2017/4/1.
 */

public class LiveBeautyFilterModel implements SDSelectManager.SDSelectable
{
    private String name;
    private int filterResId;

    //add
    private boolean selected;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getFilterResId()
    {
        return filterResId;
    }

    public void setFilterResId(int filterResId)
    {
        this.filterResId = filterResId;
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
}
