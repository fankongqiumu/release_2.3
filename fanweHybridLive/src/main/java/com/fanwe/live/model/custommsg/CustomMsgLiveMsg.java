package com.fanwe.live.model.custommsg;

import android.text.TextUtils;

import com.fanwe.live.LiveConstant.CustomMsgType;

public class CustomMsgLiveMsg extends CustomMsg
{

    private String fonts_color; // 字体颜色
    private String desc;
    private String text;

    public CustomMsgLiveMsg()
    {
        super();
        setType(CustomMsgType.MSG_LIVE_MSG);
    }

    public String getFonts_color()
    {
        return fonts_color;
    }

    public void setFonts_color(String fonts_color)
    {
        this.fonts_color = fonts_color;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getDesc()
    {
        if (TextUtils.isEmpty(desc))
        {
            desc = text;
        }
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

}
