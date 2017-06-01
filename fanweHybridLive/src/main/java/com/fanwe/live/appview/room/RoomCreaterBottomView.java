package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fanwe.games.GameBusiness;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.R;
import com.fanwe.live.common.AppRuntimeWorker;

/**
 * Created by Administrator on 2016/8/4.
 */
public class RoomCreaterBottomView extends RoomBottomView implements GameBusiness.IGameCtrlView
{

    public RoomCreaterBottomView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomCreaterBottomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomCreaterBottomView(Context context)
    {
        super(context);
    }

    private RoomMenuView menu_send_msg; //发消息
    private RoomMenuView menu_plugins; //插件中心
    private RoomMenuView menu_private_msg; //私聊消息
    private RoomMenuView menu_share; //分享
    private RoomMenuView menu_pay_mode; //付费模式
    private RoomMenuView menu_pay_mode_upgrade; //付费模式提档
    private RoomMenuView menu_plugin_switch;//游戏缩小按钮

    private RoomMenuView menu_start;
    private RoomMenuView menu_waiting;
    private RoomMenuView menu_close;
    private FrameLayout fl_game_banker;
    private RoomMenuView menu_open_banker;
    private RoomMenuView menu_banker_list;
    private RoomMenuView menu_stop_banker;

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    protected int onCreateContentView()
    {
        return R.layout.view_room_creater_bottom;
    }

    @Override
    protected void baseConstructorInit()
    {
        super.baseConstructorInit();
        menu_send_msg = find(R.id.menu_send_msg);
        menu_plugins = find(R.id.menu_plugins);
        menu_private_msg = find(R.id.menu_private_msg);
        menu_share = find(R.id.menu_share);
        menu_pay_mode = find(R.id.menu_pay_mode);
        menu_pay_mode_upgrade = find(R.id.menu_pay_mode_upgrade);
        menu_plugin_switch = find(R.id.menu_plugin_switch);
        menu_start = find(R.id.menu_start);
        menu_waiting = find(R.id.menu_waiting);
        menu_close = find(R.id.menu_close);
        fl_game_banker = find(R.id.fl_game_banker);
        menu_open_banker = find(R.id.menu_open_banker);
        menu_banker_list = find(R.id.menu_banker_list);
        menu_stop_banker = find(R.id.menu_stop_banker);

        //发消息
        menu_send_msg.setImageResId(R.drawable.ic_live_bottom_open_send);
        //插件中心
        menu_plugins.setImageResId(R.drawable.ic_live_bottom_plugins);
        //私聊消息
        menu_private_msg.setImageResId(R.drawable.ic_live_bottom_msg);
        //分享
        menu_share.setImageResId(R.drawable.ic_live_bottom_share);
        //切换付费模式
        menu_pay_mode.setImageResId(R.drawable.ic_pay_switch_live);
        //付费模式提档
        menu_pay_mode_upgrade.setImageResId(R.drawable.ic_pay_add);

        menu_open_banker.setImageResId(R.drawable.ic_game_banker_open);
        menu_banker_list.setImageResId(R.drawable.ic_game_banker_list);
        menu_stop_banker.setImageResId(R.drawable.ic_game_banker_stop);

        menu_send_msg.setOnClickListener(this);
        menu_plugins.setOnClickListener(this);
        menu_private_msg.setOnClickListener(this);
        menu_share.setOnClickListener(this);
        menu_start.setOnClickListener(this);
        menu_close.setOnClickListener(this);
        menu_pay_mode.setOnClickListener(this);
        menu_pay_mode_upgrade.setOnClickListener(this);
        menu_plugin_switch.setOnClickListener(this);
        menu_open_banker.setOnClickListener(this);
        menu_banker_list.setOnClickListener(this);
        menu_stop_banker.setOnClickListener(this);

        setUnreadMessageModel(IMHelper.getC2CTotalUnreadMessageModel());

        if(AppRuntimeWorker.isOpenBankerModule()) {
            SDViewUtil.show(fl_game_banker);
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);

        if (clickListener != null)
        {
            if (v == menu_send_msg)
            {
                clickListener.onClickMenuSendMsg(v);
            } else if (v == menu_plugins)
            {
                clickListener.onClickMenuPlugins(v);
            } else if (v == menu_start)
            {
                clickListener.onClickMenuStart(v);
            } else if (v == menu_close)
            {
                clickListener.onClickMenuClose(v);
            }else if (v == menu_private_msg)
            {
                clickListener.onClickMenuPrivateMsg(v);
            } else if (v == menu_share)
            {
                clickListener.onClickMenuShare(v);
            } else if (v == menu_pay_mode)
            {
                clickListener.onClickMenuPayMode(v);
            } else if (v == menu_pay_mode_upgrade)
            {
                clickListener.onClickMenuPayModeUpagrade(v);
            } else if (v == menu_plugin_switch)
            {
                clickListener.onClickMenuPluginSwitch(v);
            } else if(v == menu_open_banker) {
                clickListener.onClickMenuOpenBanker(v);
            } else if(v == menu_stop_banker) {
                clickListener.onClickMenuStopBanker(v);
            } else if(v == menu_banker_list) {
                clickListener.onClickMenuBankerList(v);
            }
        }
    }

    @Override
    public void showMenuShare(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_share);
        } else
        {
            SDViewUtil.hide(menu_share);
        }
    }

    /**
     * 显示隐藏付费模式
     *
     * @param show
     */
    public void showMenuPayMode(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_pay_mode);
        } else
        {
            SDViewUtil.hide(menu_pay_mode);
        }
    }

    /**
     * 显示隐藏付费模式提档
     * @param show
     */
    public void showMenuPayModeUpgrade(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_pay_mode_upgrade);
        } else
        {
            SDViewUtil.hide(menu_pay_mode_upgrade);
        }
    }

    @Override
    public void showMenuPluginSwitch(boolean show)
    {
        if (show)
            SDViewUtil.show(menu_plugin_switch);
        else
            SDViewUtil.hide(menu_plugin_switch);
    }

    @Override
    public void setMenuPluginSwitchStateOpen()
    {
        super.setMenuPluginSwitchStateOpen();
        menu_plugin_switch.setImageResId(R.drawable.ic_live_show_plugin);
    }

    @Override
    public void setMenuPluginSwitchStateClose()
    {
        super.setMenuPluginSwitchStateClose();
        menu_plugin_switch.setImageResId(R.drawable.ic_live_hide_plugin);
    }

    @Override
    protected void onIMUnreadNumber(String numberFormat)
    {
        super.onIMUnreadNumber(numberFormat);
        menu_private_msg.setTextUnread(numberFormat);
    }

    @Override
    public void onGameCtrlShowStart(boolean show, int gameId)
    {
        menu_start.setImageResId(R.drawable.ic_bottom_start);
        if (show)
        {
            SDViewUtil.show(menu_start);
        } else
        {
            SDViewUtil.hide(menu_start);
        }
    }

    @Override
    public void onGameCtrlShowClose(boolean show, int gameId)
    {
        menu_close.setImageResId(R.drawable.ic_bottom_close);
        if (show)
        {
            SDViewUtil.show(menu_close);
        } else
        {
            SDViewUtil.hide(menu_close);
        }
    }

    @Override
    public void onGameCtrlShowWaiting(boolean show, int gameId)
    {
        menu_waiting.setImageResId(R.drawable.ic_bottom_waiting);
        if (show)
        {
            SDViewUtil.show(menu_waiting);
        } else
        {
            SDViewUtil.hide(menu_waiting);
        }
    }

    public void onGameCtrlShowBankerBtn(int status) {
        SDViewUtil.hide(menu_open_banker);
        SDViewUtil.hide(menu_banker_list);
        SDViewUtil.hide(menu_stop_banker);
        switch (status) {
            case 1:
                SDViewUtil.show(menu_open_banker);
                break;
            case 2:
                menu_banker_list.setTextUnread(null);
                SDViewUtil.show(menu_banker_list);
                break;
            case 3:
                SDViewUtil.show(menu_stop_banker);
                break;
            default:
                break;
        }
    }

    public void onGameCtrlShowApplyList() {
        menu_banker_list.setTextUnread("");
    }

    public interface ClickListener
    {
        void onClickMenuSendMsg(View v);

        void onClickMenuPlugins(View v);

        void onClickMenuStart(View v);

        void onClickMenuClose(View v);

        void onClickMenuPrivateMsg(View v);

        void onClickMenuShare(View v);

        void onClickMenuPayMode(View v);

        void onClickMenuPayModeUpagrade(View v);

        void onClickMenuPluginSwitch(View v);

        void onClickMenuOpenBanker(View v);

        void onClickMenuStopBanker(View v);

        void onClickMenuBankerList(View v);
    }
}
