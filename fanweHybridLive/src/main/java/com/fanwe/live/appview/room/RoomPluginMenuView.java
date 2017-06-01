package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.live.R;
import com.fanwe.live.appview.AMenuView;
import com.fanwe.live.utils.GlideUtil;

/**
 * 直播间插件菜单
 */
public class RoomPluginMenuView extends AMenuView
{
    private ImageView iv_image;
    private TextView tv_text;

    public RoomPluginMenuView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public RoomPluginMenuView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RoomPluginMenuView(Context context)
    {
        super(context);
        init();
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_room_plugin_menu);

        iv_image = find(R.id.iv_image);
        tv_text = find(R.id.tv_text);
    }

    @Override
    public void setImageUrl(String url)
    {
        GlideUtil.load(url).into(iv_image);
    }

    @Override
    public void setImageResId(int resId)
    {
        iv_image.setImageResource(resId);
    }

    @Override
    public void setText(String text)
    {
        tv_text.setText(text);
    }

    @Override
    public void setTextUnread(String text)
    {

    }

    @Override
    public void setTextColor(int color)
    {
        tv_text.setTextColor(color);
    }

    @Override
    public void setTextColorResId(int colorResId)
    {
        tv_text.setTextColor(getResources().getColor(colorResId));
    }

    @Override
    public void setSizeMenu(int width, int height)
    {

    }

    @Override
    public void setSizeImage(int width, int height)
    {

    }
}
