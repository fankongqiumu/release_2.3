package com.fanwe.live.activity.room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fanwe.games.model.PluginModel;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.dialog.SDDialogProgress;
import com.fanwe.library.listener.SDVisibilityStateListener;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.appview.room.RoomGiftGifView;
import com.fanwe.live.appview.room.RoomGiftPlayView;
import com.fanwe.live.appview.room.RoomHeartView;
import com.fanwe.live.appview.room.RoomInfoView;
import com.fanwe.live.appview.room.RoomMsgView;
import com.fanwe.live.appview.room.RoomPopMsgView;
import com.fanwe.live.appview.room.RoomPrivateRemoveViewerView;
import com.fanwe.live.appview.room.RoomSendMsgView;
import com.fanwe.live.appview.room.RoomViewerJoinRoomView;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dialog.LiveAddViewerDialog;
import com.fanwe.live.dialog.LiveChatC2CDialog;
import com.fanwe.live.dialog.LiveRechargeDialog;
import com.fanwe.live.dialog.LiveRedEnvelopeNewDialog;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_plugin_statusActModel;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * 公共界面
 */
public class LiveLayoutActivity extends LiveActivity implements ILiveChangeRoomListener
{

    protected View rootView;

    protected RoomInfoView roomInfoView;
    protected RoomGiftPlayView roomGiftPlayView;
    protected RoomPopMsgView roomPopMsgView;
    protected RoomViewerJoinRoomView roomViewerJoinRoomView;
    protected RoomMsgView roomMsgView;
    protected RoomSendMsgView roomSendMsgView;
    protected RoomHeartView roomHeartView;
    protected RoomGiftGifView roomGiftGifView;
    protected RoomPrivateRemoveViewerView roomPrivateRemoveViewerView;

    private View rl_root_view; // 根部局
    private RelativeLayout rl_root_layout; // UI表现层根部局
    private View view_close_room; // 关闭房间
    private ViewGroup fl_live_room_info; // 房间信息
    private ViewGroup fl_live_gift_play; // 礼物播放
    private ViewGroup fl_live_pop_msg; // 弹幕
    private ViewGroup fl_live_viewer_join_room; // 加入提示
    private ViewGroup fl_live_msg; // 聊天列表
    private ViewGroup fl_live_send_msg; // 发送消息
    protected ViewGroup fl_live_bottom_menu; // 底部菜单
    private ViewGroup fl_live_heart; // 点亮
    private ViewGroup fl_live_gift_gif; // gif
    private ViewGroup fl_plugin_extend; // 底部扩展

    private ViewGroup fl_container_banker; //游戏庄家信息

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        rl_root_view = find(R.id.rl_root_view);
    }

    @Override
    protected void onKeyboardVisibilityChange(boolean visible, int height)
    {
        if (rl_root_layout != null)
        {
            if (visible)
            {
                rl_root_layout.scrollBy(0, height);
            } else
            {
                rl_root_layout.scrollTo(0, 0);
                rl_root_layout.requestLayout();
                showSendMsgView(false);
            }
        }
        super.onKeyboardVisibilityChange(visible, height);
    }

    protected void initLayout(View view)
    {
        rootView = view;
        view_close_room = findViewById(R.id.view_close_room);
        rl_root_layout = (RelativeLayout) view.findViewById(R.id.rl_root_layout);
        fl_live_room_info = (ViewGroup) view.findViewById(R.id.fl_live_room_info);
        fl_live_gift_play = (ViewGroup) view.findViewById(R.id.fl_live_gift_play);
        fl_live_pop_msg = (ViewGroup) view.findViewById(R.id.fl_live_pop_msg);
        fl_live_viewer_join_room = (ViewGroup) view.findViewById(R.id.fl_live_viewer_join_room);
        fl_live_msg = (ViewGroup) view.findViewById(R.id.fl_live_msg);
        fl_live_send_msg = (ViewGroup) view.findViewById(R.id.fl_live_send_msg);
        fl_live_bottom_menu = (ViewGroup) view.findViewById(R.id.fl_live_bottom_menu);
        fl_live_heart = (ViewGroup) view.findViewById(R.id.fl_live_heart);
        fl_live_gift_gif = (ViewGroup) view.findViewById(R.id.fl_live_gift_gif);
        fl_plugin_extend = (ViewGroup) view.findViewById(R.id.fl_bottom_extend);

        fl_container_banker = (ViewGroup) view.findViewById(R.id.fl_container_banker);

        // 关闭房间监听
        view_close_room.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickCloseRoom(v);
            }
        });

        // 房间信息
        roomInfoView = new RoomInfoView(this);
        roomInfoView.setClickListener(roomInfoClickListener);
        addLiveRoomInfo();

        //礼物播放
        roomGiftPlayView = new RoomGiftPlayView(this);
        addLiveGiftPlay();

        //弹幕
        roomPopMsgView = new RoomPopMsgView(this);
        addLivePopMsg();

        //加入房间提示
        roomViewerJoinRoomView = new RoomViewerJoinRoomView(this);
        addLiveViewerJoinRoom();

        //消息列表
        SDViewUtil.setViewHeight(fl_live_msg, SDViewUtil.getScreenHeightPercent(0.2f));
        SDViewUtil.setViewWidth(fl_live_msg, SDViewUtil.getScreenWidthPercent(0.8f));
        roomMsgView = new RoomMsgView(this);
        addLiveMsg();

        //发消息
        roomSendMsgView = new RoomSendMsgView(this);
        roomSendMsgView.addVisibilityStateListener(new SDVisibilityStateListener()
        {
            @Override
            public void onVisible(View view)
            {
                LiveLayoutActivity.this.onShowSendMsgView(view);
            }

            @Override
            public void onGone(View view)
            {
                LiveLayoutActivity.this.onHideSendMsgView(view);
            }

            @Override
            public void onInvisible(View view)
            {
                LiveLayoutActivity.this.onHideSendMsgView(view);
            }
        });
        addLiveSendMsg();

        //底部菜单
        addLiveBottomMenu();

        //点亮
        SDViewUtil.setViewWidth(fl_live_heart, SDResourcesUtil.getDimensionPixelSize(R.dimen.width_live_bottom_menu) * 3);
        SDViewUtil.setViewHeight(fl_live_heart, SDViewUtil.getScreenWidthPercent(0.5f));
        roomHeartView = new RoomHeartView(this);
        addLiveHeart();

        //gif动画
        roomGiftGifView = new RoomGiftGifView(this);
        addLiveGiftGif();
    }

    /**
     * 替换插件扩展
     *
     * @param view
     */
    protected void replacePluginExtend(View view)
    {
        replaceView(fl_plugin_extend, view);
        updatePluginExtendState();
    }

    /**
     * 移除插件扩展
     */
    protected void removePluginExtend()
    {
        if (fl_plugin_extend != null)
        {
            fl_plugin_extend.removeAllViews();
        }
        updatePluginExtendState();
    }

    /**
     * 插件扩展是否可见
     *
     * @return
     */
    protected boolean isPluginExtendVisible()
    {
        if (getPluginExtendChildView() == null)
        {
            return false;
        }

        if (getPluginExtendChildView().getVisibility() == View.VISIBLE)
        {
            return true;
        } else
        {
            return false;
        }
    }

    private View getPluginExtendChildView()
    {
        if (fl_plugin_extend == null)
        {
            return null;
        }

        return fl_plugin_extend.getChildAt(0);
    }

    /**
     * 显示底部扩展
     */
    protected void showPluginExtend()
    {
        SDViewUtil.show(getPluginExtendChildView());
        updatePluginExtendState();
    }

    /**
     * 延迟显示底部扩展
     *
     * @param delay 延迟
     */
    protected void showPluginExtend(long delay)
    {
        showPluginExtendRunnable.runDelay(delay);
    }

    private SDDelayRunnable showPluginExtendRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            showPluginExtend();
        }
    };

    /**
     * 隐藏底部扩展
     */
    protected void hidePluginExtend()
    {
        SDViewUtil.hide(getPluginExtendChildView());
        updatePluginExtendState();
    }

    /**
     * 切换显示隐藏插件扩展
     */
    protected void togglePluginExtend()
    {
        if (isPluginExtendVisible())
        {
            hidePluginExtend();
        } else
        {
            showPluginExtend();
        }
    }

    /**
     * 更新插件扩展的状态
     */
    protected void updatePluginExtendState()
    {
        if (getPluginExtendChildView() != null)
        {
            showPluginExtendSwitch(true);
            switch (getPluginExtendChildView().getVisibility())
            {
                case View.VISIBLE:
                    onShowPluginExtend();
                    break;
                case View.GONE:
                    onHidePluginExtend();
                    break;
                case View.INVISIBLE:
                    onHidePluginExtend();
                    break;
                default:
                    break;
            }
        } else
        {
            showPluginExtendSwitch(false);
        }
    }

    /**
     * 插件扩展显示回调
     */
    protected void onShowPluginExtend()
    {

    }

    /**
     * 插件扩展隐藏回调
     */
    protected void onHidePluginExtend()
    {

    }

    /**
     * 显示隐藏插件扩展开关
     *
     * @param show
     */
    protected void showPluginExtendSwitch(boolean show)
    {
        // 子类实现
    }

    /**
     * 发送礼物的view显示
     *
     * @param view
     */
    protected void onShowSendGiftView(View view)
    {
        showBottomView(false);
        showMsgView(false);
    }

    /**
     * 发送礼物的view隐藏
     *
     * @param view
     */
    protected void onHideSendGiftView(View view)
    {
        showBottomView(true);
        showMsgView(true);
    }

    /**
     * 发送消息的view显示
     *
     * @param view
     */
    protected void onShowSendMsgView(View view)
    {
        showBottomView(false);
    }

    /**
     * 发送消息的view隐藏
     *
     * @param view
     */
    protected void onHideSendMsgView(View view)
    {
        showBottomView(true);
    }

    public RelativeLayout getLiveRootLayout()
    {
        return rl_root_layout;
    }

    public View getRootView()
    {
        return rl_root_view;
    }

    /**
     * 房间信息
     */
    protected void addLiveRoomInfo()
    {
        replaceView(fl_live_room_info, roomInfoView);
    }

    /**
     * 礼物播放
     */
    protected void addLiveGiftPlay()
    {
        replaceView(fl_live_gift_play, roomGiftPlayView);
    }

    /**
     * 弹幕
     */
    protected void addLivePopMsg()
    {
        replaceView(fl_live_pop_msg, roomPopMsgView);
    }

    /**
     * 进入提示
     */
    protected void addLiveViewerJoinRoom()
    {
        replaceView(fl_live_viewer_join_room, roomViewerJoinRoomView);
    }

    /**
     * 聊天列表
     */
    protected void addLiveMsg()
    {
        replaceView(fl_live_msg, roomMsgView);
    }

    /**
     * 发送消息
     */
    protected void addLiveSendMsg()
    {
        replaceView(fl_live_send_msg, roomSendMsgView);
    }

    /**
     * 底部菜单
     */
    protected void addLiveBottomMenu()
    {
        //子类实现
    }

    /**
     * 点亮
     */
    protected void addLiveHeart()
    {
        replaceView(fl_live_heart, roomHeartView);
    }

    /**
     * gif动画
     */
    protected void addLiveGiftGif()
    {
        replaceView(fl_live_gift_gif, roomGiftGifView);
    }

    /**
     * 私密直播踢人
     */
    protected void addLivePrivateRemoveViewer()
    {
        removeView(roomPrivateRemoveViewerView);
        roomPrivateRemoveViewerView = new RoomPrivateRemoveViewerView(this);
        addView(roomPrivateRemoveViewerView);
    }

    /**
     * 结束界面
     */
    protected void addLiveFinish()
    {
        // 子类实现
    }

    /**
     * 房间信息view点击监听
     */
    private RoomInfoView.ClickListener roomInfoClickListener = new RoomInfoView.ClickListener()
    {
        @Override
        public void onClickAddViewer(View v)
        {
            LiveLayoutActivity.this.onClickAddViewer(v);
        }

        @Override
        public void onClickMinusViewer(View v)
        {
            LiveLayoutActivity.this.onClickMinusViewer(v);
        }
    };

    /**
     * 私密直播点击加号加人
     *
     * @param v
     */
    protected void onClickAddViewer(View v)
    {
        LiveAddViewerDialog dialog = new LiveAddViewerDialog(this, getRoomInfo().getPrivate_share());
        dialog.showBottom();
    }

    /**
     * 私密直播点击减号踢人
     *
     * @param v
     */
    protected void onClickMinusViewer(View v)
    {
        addLivePrivateRemoveViewer();
    }

    @Override
    public void onLiveRefreshViewerList(List<UserModel> listModel)
    {
        super.onLiveRefreshViewerList(listModel);
        roomInfoView.onLiveRefreshViewerList(listModel);
    }

    @Override
    public void onLiveRemoveViewer(UserModel model)
    {
        super.onLiveRemoveViewer(model);
        roomInfoView.onLiveRemoveViewer(model);
    }

    @Override
    public void onLiveInsertViewer(int position, UserModel model)
    {
        super.onLiveInsertViewer(position, model);
        roomInfoView.onLiveInsertViewer(position, model);
    }

    @Override
    public void onLiveTicketChange(long ticket)
    {
        super.onLiveTicketChange(ticket);
        roomInfoView.updateTicket(ticket);
    }

    @Override
    public void onLiveViewerNumberChange(int viewerNumber)
    {
        super.onLiveViewerNumberChange(viewerNumber);
        roomInfoView.updateViewerNumber(viewerNumber);
    }

    @Override
    public void onLiveBindCreaterData(UserModel model)
    {
        super.onLiveBindCreaterData(model);
        roomInfoView.bindCreaterData(model);
    }

    @Override
    public void onLiveShowOperateViewer(boolean show)
    {
        super.onLiveShowOperateViewer(show);
        roomInfoView.showOperateViewerView(show);
    }

    @Override
    public void onLiveUpdateLiveQualityData(LiveQualityData data)
    {
        super.onLiveUpdateLiveQualityData(data);
        roomInfoView.getSdkInfoView().updateLiveQuality(data);
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);
        if (TextUtils.isEmpty(getCreaterId()))
        {
            setCreaterId(actModel.getUser_id());
        }
        if (TextUtils.isEmpty(getGroupId()))
        {
            setGroupId(actModel.getGroup_id());
        }
        roomInfoView.bindData(actModel);
        bindShowShareView();
        getLiveBusiness().startLiveQualityLooper(this);
    }

    /**
     * 显示充值窗口
     * 整合到基类？
     */
    protected void showRechargeDialog()
    {
        LiveRechargeDialog dialog = new LiveRechargeDialog(this);
        dialog.show();
    }

    /**
     * 点击关闭
     *
     * @param v
     */
    protected void onClickCloseRoom(View v)
    {
        //子类实现
    }

    /**
     * 点击打开发送窗口
     *
     * @param v
     */
    protected void onClickMenuSendMsg(View v)
    {
        showSendMsgView(true);
    }

    @Override
    public void openSendMsg(String content)
    {
        super.openSendMsg(content);
        showSendMsgView(true);
        roomSendMsgView.setContent(content);
    }

    /**
     * 点击私聊消息
     *
     * @param v
     */
    protected void onClickMenuPrivateMsg(View v)
    {
        LiveChatC2CDialog dialog = new LiveChatC2CDialog(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {

            @Override
            public void onDismiss(DialogInterface dialog)
            {

            }
        });
        dialog.showBottom();
    }

    /**
     * 绑定是否显示分享view
     */
    protected void bindShowShareView()
    {
    }

    /**
     * 主播插件被点击
     */
    protected void onClickCreaterPlugin(final PluginModel model)
    {
        if (model == null)
        {
            return;
        }
        String plugin_id = model.getId();
        CommonInterface.requestPlugin_status(plugin_id, new AppRequestCallback<App_plugin_statusActModel>()
        {
            private SDDialogProgress dialog = new SDDialogProgress();

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dialog.dismiss();
            }

            @Override
            protected void onStart()
            {
                super.onStart();
                dialog.setTextMsg("");
                dialog.show();
            }

            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    if (actModel.getIs_enable() == 1)
                    {
                        if (model.isNormalPlugin())
                        {
                            //点击普通插件
                            onClickCreaterPluginNormal(model);
                        } else if (model.isGamePlugin())
                        {
                            //点击游戏插件
                            onClickCreaterPluginGame(model);
                        }
                    }
                }
            }
        });

    }

    /**
     * 主播普通插件点击
     *
     * @param model
     */
    protected void onClickCreaterPluginNormal(PluginModel model)
    {

    }

    /**
     * 主播游戏插件点击
     *
     * @param model
     */
    protected void onClickCreaterPluginGame(PluginModel model)
    {

    }

    protected void replaceBankerView(View view) {
        replaceView(fl_container_banker, view);
    }

    /**
     * 点击分享
     *
     * @param v
     */
    protected void onClickMenuShare(View v)
    {
        openShare(new UMShareListener()
        {
            @Override
            public void onStart(SHARE_MEDIA share_media)
            {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media)
            {
                getLiveBusiness().sendShareSuccessMsg(getGroupId());
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable)
            {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media)
            {
            }
        });
    }

    /**
     * 隐藏发送礼物
     */
    protected void hideSendGiftView()
    {

    }

    /**
     * 显示隐藏底部菜单view
     */
    protected void showBottomView(boolean show)
    {
        // 子类实现
    }

    /**
     * 显示隐藏消息列表
     */
    protected void showMsgView(boolean show)
    {
        if (show)
        {
            roomMsgView.show(true);
        } else
        {
            roomMsgView.invisible(true);
        }
    }

    /**
     * 显示隐藏发送消息view
     */
    protected void showSendMsgView(boolean show)
    {
        if (show)
        {
            roomSendMsgView.show();
        } else
        {
            roomSendMsgView.invisible();
        }
    }

    /**
     * 发送消息view是否可见
     *
     * @return
     */
    protected boolean isSendMsgViewVisible()
    {
        if (roomSendMsgView == null)
        {
            return false;
        }

        return roomSendMsgView.isVisible();
    }

    /**
     * 发送礼物view是否可见
     *
     * @return
     */
    protected boolean isSendGiftViewVisible()
    {
        return false;
    }

    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg)
    {
        super.onMsgRedEnvelope(msg);
        LiveRedEnvelopeNewDialog dialog = new LiveRedEnvelopeNewDialog(this, msg);
        dialog.show();
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        roomGiftPlayView.onChangeRoomActionComplete();
        roomMsgView.onChangeRoomActionComplete();
        roomGiftGifView.onChangeRoomActionComplete();
        roomPopMsgView.onChangeRoomActionComplete();
    }

    @Override
    public void onChangeRoomComplete()
    {
        roomHeartView.onChangeRoomComplete();
    }

    @Override
    public void onLiveViewerShowCreaterLeave(boolean show)
    {
        super.onLiveViewerShowCreaterLeave(show);
        if (isAuctioning())
        {
            roomInfoView.showCreaterLeave(false);
        } else
        {
            roomInfoView.showCreaterLeave(show);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        showPluginExtendRunnable.removeDelay();
    }
}
