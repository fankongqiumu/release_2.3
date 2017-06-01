package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.R;

import static com.fanwe.live.common.AppRuntimeWorker.getIsOpenWebviewMain;
import static com.fanwe.live.common.AppRuntimeWorker.getIsViewerShowPlug;
import static com.fanwe.live.common.AppRuntimeWorker.getOpen_podcast_goods;
import static com.fanwe.live.common.AppRuntimeWorker.getShopping_goods;

/**
 * 观众底部菜单
 * Created by Administrator on 2016/8/6.
 */
public class RoomViewerBottomView extends RoomBottomView
{
    public RoomViewerBottomView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomViewerBottomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomViewerBottomView(Context context)
    {
        super(context);
    }

    private RoomMenuView menu_send_msg;//发消息
    private RoomMenuView menu_viewer_plugins;//星店，小店
    private ImageView menu_live_bottom_podcast_order;//星店
    private ImageView menu_shop_ic_my_store;//小店
    private RoomMenuView menu_plugin_switch; //显示隐藏插件
    private RoomMenuView menu_viewer_auction_pay; //竞拍订单支付
    private RoomMenuView menu_private_msg; //私聊消息
    private RoomMenuView menu_share; //分享
    private RoomMenuView menu_invite_video; //连麦
    private RoomMenuView menu_send_gift; //礼物

    private RoomMenuView menu_apply_banker;

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    protected int onCreateContentView()
    {
        return R.layout.view_room_viewer_bottom;
    }

    @Override
    protected void baseConstructorInit()
    {
        super.baseConstructorInit();
        menu_send_msg = find(R.id.menu_send_msg);
        menu_viewer_plugins = find(R.id.menu_viewer_plugins);
        menu_live_bottom_podcast_order = find(R.id.menu_live_bottom_podcast_order);
        menu_shop_ic_my_store = find(R.id.menu_shop_ic_my_store);
        menu_plugin_switch = find(R.id.menu_plugin_switch);
        menu_viewer_auction_pay = find(R.id.menu_viewer_auction_pay);
        menu_private_msg = find(R.id.menu_private_msg);
        menu_share = find(R.id.menu_share);
        menu_invite_video = find(R.id.menu_invite_video);
        menu_send_gift = find(R.id.menu_send_gift);

        menu_apply_banker = find(R.id.menu_apply_banker);

        //发消息
        menu_send_msg.setImageResId(R.drawable.ic_live_bottom_open_send);
        //观众插件（星店，小店）
        menu_viewer_plugins.setImageResId(R.drawable.ic_live_bottom_plugins);
        //显示隐藏插件
        menu_plugin_switch.setImageResId(R.drawable.ic_live_hide_plugin);
        //竞拍订单支付
        menu_viewer_auction_pay.setImageResId(R.drawable.ic_live_bottom_create_auction);
        menu_viewer_auction_pay.setTextUnread("1");
        //私聊消息
        menu_private_msg.setImageResId(R.drawable.ic_live_bottom_msg);
        //分享
        menu_share.setImageResId(R.drawable.ic_live_bottom_share);
        //连麦
        menu_invite_video.setImageResId(R.drawable.ic_live_bottom_invite_video);
        //礼物
        menu_send_gift.setImageResId(R.drawable.ic_live_bottom_gift);

        menu_apply_banker.setImageResId(R.drawable.ic_game_banker_apply);

        menu_send_msg.setOnClickListener(this);
        menu_viewer_plugins.setOnClickListener(this);
        menu_live_bottom_podcast_order.setOnClickListener(this);
        menu_shop_ic_my_store.setOnClickListener(this);
        menu_viewer_auction_pay.setOnClickListener(this);
        menu_private_msg.setOnClickListener(this);
        menu_share.setOnClickListener(this);
        menu_invite_video.setOnClickListener(this);
        menu_send_gift.setOnClickListener(this);
        menu_plugin_switch.setOnClickListener(this);
        menu_apply_banker.setOnClickListener(this);

        showMenuViewerPlug(false);
        showMenuViewerPodcast(false);
        showMenuViewerStore(false);
        if (getIsViewerShowPlug() == 1 && (getShopping_goods() == 1 || getIsOpenWebviewMain() || getOpen_podcast_goods() == 1))
        {
            if (isPlayback())
            {
                showMenuViewerPlug(false);
            } else
            {
                showMenuViewerPlug(true);
            }
        } else
        {
            if (isPlayback())
            {
                showMenuViewerPodcast(false);
                showMenuViewerStore(false);
            } else
            {
                if (getShopping_goods() == 1)
                {
                    showMenuViewerPodcast(true);
                }
                if (getOpen_podcast_goods() == 1)
                {
                    showMenuViewerStore(true);
                }
            }

        }
        setUnreadMessageModel(IMHelper.getC2CTotalUnreadMessageModel());
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
            } else if (v == menu_viewer_plugins)
            {
                clickListener.onClickMenuViewerPlug(v);
            } else if (v == menu_viewer_auction_pay)
            {
                clickListener.onClickMenuAuctionPay(v);
            } else if (v == menu_private_msg)
            {
                clickListener.onClickMenuPrivateMsg(v);
            } else if (v == menu_share)
            {
                clickListener.onClickMenuShare(v);
            } else if (v == menu_invite_video)
            {
                clickListener.onClickMenuInviteVideo(v);
            } else if (v == menu_send_gift)
            {
                clickListener.onClickMenuSendGift(v);
            } else if (v == menu_plugin_switch)
            {
                clickListener.onClickMenuPluginSwitch(v);
            } else if (v == menu_apply_banker)
            {
                clickListener.onClickApplyBanker(v);
            } else if (v == menu_live_bottom_podcast_order)
            {
                clickListener.onClickMenuPodcast(v);
            } else if (v == menu_shop_ic_my_store)
            {
                clickListener.onCLickMenuMyStore(v);
            }
        }
    }

    public void showMenuViewerPodcast(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_live_bottom_podcast_order);
        } else
        {
            SDViewUtil.hide(menu_live_bottom_podcast_order);
        }
    }

    public void showMenuViewerStore(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_shop_ic_my_store);
        } else
        {
            SDViewUtil.hide(menu_shop_ic_my_store);
        }

    }

    public void showMenuViewerPlug(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_viewer_plugins);
        } else
        {
            SDViewUtil.hide(menu_viewer_plugins);
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
     * 显示隐藏连麦
     *
     * @param show true-显示
     */
    public void showMenuInviteVideo(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_invite_video);
        } else
        {
            SDViewUtil.hide(menu_invite_video);
        }
    }

    /**
     * 显示隐藏竞拍订单支付
     *
     * @param show true-显示
     */
    public void showMenuAuctionPay(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(menu_viewer_auction_pay);
        } else
        {
            SDViewUtil.hide(menu_viewer_auction_pay);
        }
    }

    public void showMenuApplyBanker(boolean show, long principal)
    {

        if (show)
        {
            SDViewUtil.show(menu_apply_banker);
            menu_apply_banker.setTag(principal);
        } else
        {
            SDViewUtil.hide(menu_apply_banker);
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

    public interface ClickListener
    {
        void onClickMenuSendMsg(View v);

        void onClickMenuViewerPlug(View v);

        void onClickMenuPluginSwitch(View v);

        void onClickMenuPrivateMsg(View v);

        void onClickMenuAuctionPay(View v);

        void onClickMenuInviteVideo(View v);

        void onClickMenuSendGift(View v);

        void onClickMenuShare(View v);

        void onClickApplyBanker(View v);

        void onClickMenuPodcast(View v);

        void onCLickMenuMyStore(View v);
    }
}
