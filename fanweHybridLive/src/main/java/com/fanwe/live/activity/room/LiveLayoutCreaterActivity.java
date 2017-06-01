package com.fanwe.live.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fanwe.games.model.PluginModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.umeng.UmengSocialManager;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveSongChooseActivity;
import com.fanwe.live.appview.AMenuView;
import com.fanwe.live.appview.LiveCreaterPluginView;
import com.fanwe.live.appview.room.ARoomMusicView;
import com.fanwe.live.appview.room.RoomCountDownView;
import com.fanwe.live.appview.room.RoomCreaterBottomView;
import com.fanwe.live.appview.room.RoomCreaterFinishView;
import com.fanwe.live.business.LiveCreaterBusiness;
import com.fanwe.live.dialog.LiveRoomPluginsDialog;
import com.fanwe.live.event.ECreateLiveSuccess;
import com.fanwe.live.model.App_end_videoActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_video_cstatusActModel;
import com.fanwe.live.model.RoomShareModel;
import com.fanwe.live.model.custommsg.CustomMsgWarning;
import com.sunday.eventbus.SDEventManager;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 主播界面
 */
public class LiveLayoutCreaterActivity extends LiveLayoutExtendActivity implements LiveCreaterPluginView.ClickListener
{
    /**
     * 1：主播界面被强制关闭后回来(int)
     */
    public static final String EXTRA_IS_CLOSED_BACK = "EXTRA_IS_CLOSED_BACK";
    /**
     * 1：主播界面被强制关闭后回来
     */
    protected int isClosedBack;

    /**
     * 主播是否离开
     */
    protected boolean isCreaterLeave = false;

    /**
     * 是否静音模式
     */
    protected boolean isMuteMode = false;
    /**
     * 是否显示直播结束界面
     */
    protected boolean isNeedShowFinish = false;

    protected RoomCreaterBottomView roomCreaterBottomView;
    private RoomCreaterFinishView roomCreaterFinishView;
    private ARoomMusicView roomMusicView;
    private RoomCountDownView roomCountDownView;

    private ViewGroup fl_live_play_music; // 播放音乐

    private LiveRoomPluginsDialog mDialogPlugins;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        isClosedBack = getIntent().getIntExtra(EXTRA_IS_CLOSED_BACK, 0);
    }

    @Override
    protected void initLayout(View view)
    {
        super.initLayout(view);
        fl_live_play_music = (ViewGroup) view.findViewById(R.id.fl_live_play_music);

        //播放音乐
        roomMusicView = createRoomMusicView();


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        replaceView(fl_live_play_music, roomMusicView, params);
    }

    /**
     * 获得音乐播放view
     *
     * @return
     */
    public ARoomMusicView getRoomMusicView()
    {
        return roomMusicView;
    }

    /**
     * 添加倒计时开播view
     */
    protected void addRoomCountDownView()
    {
        boolean showCount = getResources().getBoolean(R.bool.show_create_room_count_down);
        if (showCount)
        {
            if (roomCountDownView == null)
            {
                roomCountDownView = new RoomCountDownView(this);
                addView(roomCountDownView);
                roomCountDownView.startCountDown(3);
            }
        }
    }

    /**
     * 移除倒计时开播view
     */
    protected void removeRoomCountDownView()
    {
        SDViewUtil.removeViewFromParent(roomCountDownView);
    }

    /**
     * 创建播放音乐的view
     */
    protected ARoomMusicView createRoomMusicView()
    {
        // 子类实现
        return null;
    }

    /**
     * 停止播放音乐
     */
    protected void stopMusic()
    {
        if (roomMusicView != null)
        {
            roomMusicView.stopMusic();
        }
    }

    @Override
    protected void addLiveBottomMenu()
    {
        if (roomCreaterBottomView == null)
        {
            roomCreaterBottomView = new RoomCreaterBottomView(this);
            roomCreaterBottomView.setClickListener(bottomClickListener);
        }
        replaceView(fl_live_bottom_menu, roomCreaterBottomView);
    }

    /**
     * 底部菜单点击监听
     */
    private RoomCreaterBottomView.ClickListener bottomClickListener = new RoomCreaterBottomView.ClickListener()
    {
        @Override
        public void onClickMenuSendMsg(View v)
        {
            LiveLayoutCreaterActivity.this.onClickMenuSendMsg(v);
        }

        @Override
        public void onClickMenuPrivateMsg(View v)
        {
            LiveLayoutCreaterActivity.this.onClickMenuPrivateMsg(v);
        }

        @Override
        public void onClickMenuShare(View v)
        {
            LiveLayoutCreaterActivity.this.onClickMenuShare(v);
        }

        @Override
        public void onClickMenuPlugins(View v)
        {
            showCreaterPlugins();
        }

        @Override
        public void onClickMenuStart(View v)
        {
            onClickGameCtrlStart(v);
        }

        @Override
        public void onClickMenuClose(View v)
        {
            onClickGameCtrlClose(v);
        }

        @Override
        public void onClickMenuPayMode(View v)
        {
            LiveLayoutCreaterActivity.this.onClickMenuPayMode(v);
        }

        @Override
        public void onClickMenuPayModeUpagrade(View v)
        {
            LiveLayoutCreaterActivity.this.onClickMenuPayUpagrade(v);
        }

        @Override
        public void onClickMenuPluginSwitch(View v)
        {
            togglePluginExtend();
        }

        @Override
        public void onClickMenuOpenBanker(View v)
        {
            onClickGameCtrlOpenBanker(v);
        }

        @Override
        public void onClickMenuStopBanker(View v)
        {
            onClickGameCtrlStopBanker(v);
        }

        @Override
        public void onClickMenuBankerList(View v)
        {
            onClickGameCtrlBankerList(v);
        }
    };

    /**
     * 显示主播插件菜单
     */
    protected void showCreaterPlugins()
    {
        if (mDialogPlugins == null)
        {
            mDialogPlugins = new LiveRoomPluginsDialog(this);
            mDialogPlugins.setOnItemClickListener(new LiveRoomPluginsDialog.OnItemClickListener()
            {
                @Override
                public void clickItem(View view, int position, PluginModel model)
                {
                    onClickCreaterPlugin(model);
                }
            });
            mDialogPlugins.getBasePlugin().setClickListener(LiveLayoutCreaterActivity.this);
        }
        mDialogPlugins.getBasePlugin().dealFlashLightState(sdkIsBackCamera());
        mDialogPlugins.showBottom();
    }

    /**
     * 隐藏主播插件菜单
     */
    protected void hideCreaterPlugins()
    {
        if (mDialogPlugins != null)
        {
            mDialogPlugins.dismiss();
        }
    }

    @Override
    public void onClickMenuBeauty(AMenuView view)
    {
        showSetBeautyDialog();

        hideCreaterPlugins();
    }

    @Override
    public void onClickMenuSwitchCamera(AMenuView view)
    {
        view.getMenuConfig().toggleSelected();
        view.bindData();

        sdkSwitchCamera();

        hideCreaterPlugins();
    }

    @Override
    public void onClickMenuMic(AMenuView view)
    {
        boolean result = view.getMenuConfig().toggleSelected();
        view.bindData();

        sdkEnableMic(result);
        isMuteMode = !result;

        hideCreaterPlugins();
    }

    @Override
    public void onClickMenuFlashLight(AMenuView view)
    {
        if (sdkIsBackCamera())
        {
            boolean result = view.getMenuConfig().toggleSelected();
            view.bindData();
            sdkEnableFlash(result);

            hideCreaterPlugins();
        } else
        {
            SDToast.showToast("暂时只支持后置摄像头打开闪关灯");
        }
    }

    @Override
    public void onClickMenuMusic(AMenuView view)
    {
        hideCreaterPlugins();

        Intent intent = new Intent(LiveLayoutCreaterActivity.this, LiveSongChooseActivity.class);
        startActivity(intent);
    }

    /**
     * 显示美颜设置窗口
     */
    protected void showSetBeautyDialog()
    {
        //子类实现
    }

    @Override
    protected SDRequestHandler requestRoomInfo()
    {
        return getLiveBusiness().requestRoomInfo(getRoomId(), 0, 0, 0, 0, 0, null, null);
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);

        String shareType = actModel.getShare_type();
        if (!TextUtils.isEmpty(shareType))
        {
            RoomShareModel share = actModel.getShare();
            if (share != null)
            {
                String title = share.getShare_title();
                String content = share.getShare_content();
                String imageUrl = share.getShare_imageUrl();
                String clickUrl = share.getShare_url();

                // 弹出分享页面
                if (shareType.equalsIgnoreCase(SHARE_MEDIA.WEIXIN.toString()))
                {
                    UmengSocialManager.shareWeixin(title, content, imageUrl, clickUrl, this, null);
                } else if (shareType.equalsIgnoreCase(SHARE_MEDIA.WEIXIN_CIRCLE.toString()))
                {
                    UmengSocialManager.shareWeixinCircle(title, content, imageUrl, clickUrl, this, null);
                } else if (shareType.equalsIgnoreCase(SHARE_MEDIA.QQ.toString()))
                {
                    UmengSocialManager.shareQQ(title, content, imageUrl, clickUrl, this, null);
                } else if (shareType.equalsIgnoreCase(SHARE_MEDIA.QZONE.toString()))
                {
                    UmengSocialManager.shareQzone(title, content, imageUrl, clickUrl, this, null);
                } else if (shareType.equalsIgnoreCase(SHARE_MEDIA.SINA.toString()))
                {
                    UmengSocialManager.shareSina(title, content, imageUrl, clickUrl, this, null);
                }
            }
        }
    }

    @Override
    public LiveCreaterBusiness.CreaterMonitorData onLiveCreaterGetMonitorData()
    {
        LiveCreaterBusiness.CreaterMonitorData data = new LiveCreaterBusiness.CreaterMonitorData();
        data.room_id = getRoomId();
        data.watch_number = -1;
        data.vote_number = getLiveBusiness().getTicket();
        data.live_quality = getLiveBusiness().getLiveQualityData();
        return data;
    }

    @Override
    protected void bindShowShareView()
    {
        super.bindShowShareView();
        if (roomCreaterBottomView != null)
        {
            if (isPrivate() || UmengSocialManager.isAllSocialDisable())
            {
                roomCreaterBottomView.showMenuShare(false);
            } else
            {
                roomCreaterBottomView.showMenuShare(true);
            }
        }
    }

    @Override
    protected void addLiveFinish()
    {
        if (roomCreaterFinishView == null)
        {
            roomCreaterFinishView = new RoomCreaterFinishView(this);
            roomCreaterFinishView.setClickListener(new RoomCreaterFinishView.ClickListener()
            {
                @Override
                public void onClickShare(View view)
                {
                    openShare(null);
                }
            });
            addView(roomCreaterFinishView);
        }
    }

    @Override
    public void onLiveCreaterRequestEndVideoSuccess(App_end_videoActModel actModel)
    {
        super.onLiveCreaterRequestEndVideoSuccess(actModel);
        if (roomCreaterFinishView != null)
        {
            roomCreaterFinishView.onLiveCreaterRequestEndVideoSuccess(actModel);
        }
    }

    protected boolean isClosedBack()
    {
        return isClosedBack == 1;
    }

    /**
     * 设置房间状态为失败
     */
    protected void requestUpdateLiveStateFail()
    {
        getCreaterBusiness().requestUpdateLiveStateFail(getRoomId());
    }

    protected void requestUpdateLiveStateSuccess()
    {
        requestUpdateLiveStateSuccess(null, null, null, null);
    }

    /**
     * 设置房间状态为成功
     */
    protected void requestUpdateLiveStateSuccess(String channelId,
                                                 String play_rtmp,
                                                 String play_flv,
                                                 String play_hls)
    {
        getCreaterBusiness().requestUpdateLiveStateSuccess(getRoomId(), getGroupId(), channelId, play_rtmp, play_flv, play_hls);

        ECreateLiveSuccess event = new ECreateLiveSuccess();
        SDEventManager.post(event);
    }

    /**
     * 设置房间状态主播离开
     */
    protected void requestUpdateLiveStateLeave()
    {
        getCreaterBusiness().requestUpdateLiveStateLeave(getRoomId());
    }

    /**
     * 设置房间状态主播回来
     *
     * @param listener
     */
    protected void requestUpdateLiveStateComeback(AppRequestCallback<App_video_cstatusActModel> listener)
    {
        getCreaterBusiness().requestUpdateLiveStateComeback(getRoomId());
    }

    @Override
    public boolean isCreater()
    {
        return true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mDialogPlugins != null)
        {
            mDialogPlugins.getBasePlugin().setModelFlashLightState(false);
        }
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            hideCreaterPlugins();
        } catch (Exception e)
        {
        }
        super.onDestroy();
    }

    @Override
    public void onGameCtrlShowStart(boolean show, int gameId)
    {
        super.onGameCtrlShowStart(show, gameId);
        roomCreaterBottomView.onGameCtrlShowStart(show, gameId);
    }

    @Override
    public void onGameCtrlShowWaiting(boolean show, int gameId)
    {
        super.onGameCtrlShowWaiting(show, gameId);
        roomCreaterBottomView.onGameCtrlShowWaiting(show, gameId);
    }

    @Override
    public void onGameCtrlShowBankerBtn(int status)
    {
        roomCreaterBottomView.onGameCtrlShowBankerBtn(status);
    }

    @Override
    public void onMsgApplyBanker(GameBankerMsg msg)
    {
        super.onMsgApplyBanker(msg);
        roomCreaterBottomView.onGameCtrlShowApplyList();
    }

    @Override
    public void onGameCtrlShowClose(boolean show, int gameId)
    {
        super.onGameCtrlShowClose(show, gameId);
        roomCreaterBottomView.onGameCtrlShowClose(show, gameId);
    }

    //点击付费按钮
    protected void onClickMenuPayMode(View v)
    {

    }

    //点击提档按钮
    protected void onClickMenuPayUpagrade(View v)
    {

    }

    //点击开启上庄
    protected void onClickMenuOpenBanker(View v)
    {

    }

    @Override
    protected void onHidePluginExtend()
    {
        super.onHidePluginExtend();
        roomCreaterBottomView.setMenuPluginSwitchStateOpen();
    }

    @Override
    protected void onShowPluginExtend()
    {
        super.onShowPluginExtend();
        roomCreaterBottomView.setMenuPluginSwitchStateClose();
    }

    @Override
    protected void showPluginExtendSwitch(boolean show)
    {
        super.showPluginExtendSwitch(show);
        roomCreaterBottomView.showMenuPluginSwitch(show);
    }

    @Override
    protected void showBottomView(boolean show)
    {
        super.showBottomView(show);
        if (show)
        {
            roomCreaterBottomView.show();
        } else
        {
            roomCreaterBottomView.invisible();
        }
    }

    @Override
    public void onMsgWarning(CustomMsgWarning msgWarning)
    {
        super.onMsgWarning(msgWarning);
        new SDDialogConfirm(this).setTextGravity(Gravity.CENTER).setTextContent(msgWarning.getDesc()).setTextCancel("").setTextConfirm("好的").setTextTitle("警告").show();
    }
}
