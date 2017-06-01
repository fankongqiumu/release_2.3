package com.fanwe.live.activity.room;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;

import com.fanwe.hybrid.event.EUnLogin;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDRunnableTryer;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveInformation;
import com.fanwe.live.R;
import com.fanwe.live.appview.LiveVideoExtendView;
import com.fanwe.live.appview.room.ARoomMusicView;
import com.fanwe.live.appview.room.RoomPushMusicView;
import com.fanwe.live.control.LivePlayerSDK;
import com.fanwe.live.control.LivePushSDK;
import com.fanwe.live.dialog.LiveCreaterReceiveInviteVideoDialog;
import com.fanwe.live.dialog.LiveSetBeautyDialog;
import com.fanwe.live.dialog.LiveSmallVideoInfoCreaterDialog;
import com.fanwe.live.event.EImOnForceOffline;
import com.fanwe.live.event.EOnCallStateChanged;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_start_lianmaiActModel;
import com.fanwe.live.model.LiveBeautyConfig;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgInviteVideo;
import com.fanwe.live.model.custommsg.CustomMsgRejectVideo;
import com.fanwe.live.model.custommsg.CustomMsgStopLive;
import com.fanwe.live.model.custommsg.CustomMsgUserStopVideo;
import com.fanwe.live.utils.PermissionUtil;
import com.ta.util.netstate.TANetWorkUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 推流直播主播界面
 */
public class LivePushCreaterActivity extends LiveLayoutCreaterExtendActivity implements ITXLivePushListener
{
    private LiveVideoExtendView playView;
    private LivePushSDK pushSDK = LivePushSDK.getInstance();
    protected boolean isCreaterLeaveByCall = false;
    private SDRunnableTryer groupTryer = new SDRunnableTryer();
    private LiveCreaterReceiveInviteVideoDialog receiveInviteVideoDialog;
    /**
     * 是否正在连麦中
     */
    private boolean isVideoing = false;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_push_creater;
    }

    @Override
    protected ARoomMusicView createRoomMusicView()
    {
        return new RoomPushMusicView(this);
    }

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        PermissionUtil.isCameraCanUse();

        if (getRoomId() <= 0)
        {
            SDToast.showToast("房间id为空");
            finish();
            return;
        }

        initPusher();
        initPlayer();

        setCreaterId(strUserId);
        initLayout(getWindow().getDecorView());

        requestRoomInfo();
        LogUtil.i("LivePushCreaterActivity:" + this + ",roomId:" + getRoomId());
    }

    /**
     * 初始化推流对象
     */
    private void initPusher()
    {
        pushSDK.init((TXCloudVideoView) find(R.id.view_video));
        pushSDK.setPushListener(this);
    }

    /**
     * 初始化拉流对象
     */
    private void initPlayer()
    {
        playView = find(R.id.view_small_video);
        playView.setSmallMode(true);
        playView.setPlayerListener(new LivePlayerSDK.PlayerListener()
        {
            @Override
            public void onPlayEvent(int event, Bundle param)
            {
                if (TXLiveConstants.PLAY_ERR_NET_DISCONNECT == event || TXLiveConstants.PLAY_EVT_PLAY_END == event)
                {
                    IMHelper.sendMsgC2C(playView.getUserId(), new CustomMsgUserStopVideo(), null);
                    stopSmallVideo(playView.getUserId());
                }
            }

            @Override
            public void onPlayBegin(int event, Bundle param)
            {
            }

            @Override
            public void onPlayRecvFirstFrame(int event, Bundle param)
            {
            }

            @Override
            public void onPlayProgress(int event, Bundle param, int total, int progress)
            {
            }

            @Override
            public void onPlayEnd(int event, Bundle param)
            {
            }

            @Override
            public void onPlayLoading(int event, Bundle param)
            {
            }

            @Override
            public void onNetStatus(Bundle param)
            {
            }
        });
        playView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LiveSmallVideoInfoCreaterDialog dialog = new LiveSmallVideoInfoCreaterDialog(LivePushCreaterActivity.this, playView.getUserId());
                dialog.setClickListener(new LiveSmallVideoInfoCreaterDialog.ClickListener()
                {
                    @Override
                    public void onClickCloseVideo(View v, String userId)
                    {
                        IMHelper.sendMsgC2C(userId, new CustomMsgUserStopVideo(), null);
                        stopSmallVideo(userId);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void initIM()
    {
        super.initIM();
        if (isClosedBack())
        {
            getGameBusiness().requestGameInfo(getRoomId());
        } else
        {
            if (!TextUtils.isEmpty(getRoomInfo().getGroup_id()))
            {
                final String groupId = getRoomInfo().getGroup_id();
                getCreaterIM().joinGroup(groupId, new TIMCallBack()
                {
                    @Override
                    public void onError(int code, String desc)
                    {
                        dealGroupError(code, desc);
                    }

                    @Override
                    public void onSuccess()
                    {
                        dealGroupSuccess(groupId);
                    }
                });
            } else
            {
                getCreaterIM().createGroup(String.valueOf(getRoomId()), new TIMValueCallBack<String>()
                {
                    @Override
                    public void onError(int code, String desc)
                    {
                        dealGroupError(code, desc);
                    }

                    @Override
                    public void onSuccess(String groupId)
                    {
                        dealGroupSuccess(groupId);
                    }
                });
            }
        }
    }

    /**
     * 加入或者创建聊天组成功处理
     *
     * @param groupId
     */
    private void dealGroupSuccess(String groupId)
    {
        setGroupId(groupId);
        LiveInformation.getInstance().enterRoom(getRoomId(), getGroupId(), getCreaterId());
        requestUpdateLiveStateSuccess();
    }

    /**
     * 加入或者创建聊天组失败处理
     *
     * @param code
     * @param desc
     */
    protected void dealGroupError(int code, String desc)
    {
        boolean result = groupTryer.tryRunDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                initIM();
            }
        }, 3000);

        if (!result)
        {
            showGroupErrorDialog(code, desc);
        }
    }

    /**
     * 显示加入或者创建聊天组失败窗口
     *
     * @param code
     * @param desc
     */
    protected void showGroupErrorDialog(int code, String desc)
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("创建聊天组失败，请退出重试").setTextCancel(null).setTextConfirm("确定");
        dialog.setCancelable(false);
        dialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
                requestUpdateLiveStateFail();
                exitRoom(false);
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
            }
        });
        dialog.show();
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);
        if (isClosedBack())
        {
            final String groupId = actModel.getGroup_id();
            requestUpdateLiveStateComeback(null);
            getCreaterIM().setJoinGroupSuccess(groupId);
            getCreaterIM().sendCreaterComebackMsg(null);
        }

        initIM();
        startPush(actModel.getPush_rtmp());
    }

    @Override
    public void onLiveRequestRoomInfoError(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoError(actModel);
        exitRoom(false);
    }

    /**
     * 开始推流
     */
    protected void startPush(String url)
    {
        if (TextUtils.isEmpty(url))
        {
            SDToast.showToast("推流地址为空");
            return;
        }
        pushSDK.startPush(url);
        sdkEnableBeauty(true);
        sdkEnableWhite(true);
        pushSDK.enableBeautyFilter(true);
        if (!isClosedBack())
        {
            addRoomCountDownView();
        }
    }

    /**
     * 停止推流
     */
    protected void stopPush(boolean clearLastFrame)
    {
        pushSDK.stopPush(clearLastFrame);
    }

    /**
     * 暂停推流
     */
    protected void pausePush()
    {
        pushSDK.pausePush();
    }

    /**
     * 恢复推流
     */
    protected void resumePush()
    {
        pushSDK.resumePush();
    }

    @Override
    public LiveQualityData onLiveGetLiveQualityData()
    {
        return pushSDK.getLiveQualityData();
    }

    @Override
    public void onMsgInviteVideo(final CustomMsgInviteVideo msg)
    {
        super.onMsgInviteVideo(msg);
        if (isVideoing)
        {
            getCreaterBusiness().rejectVideo(msg.getSender().getUser_id(), CustomMsgRejectVideo.MSG_MAX);
            return;
        }

        if (receiveInviteVideoDialog != null && receiveInviteVideoDialog.isShowing())
        {
            // 主播有未处理的连麦请求
            getCreaterBusiness().rejectVideo(msg.getSender().getUser_id(), CustomMsgRejectVideo.MSG_BUSY);
            return;
        }

        receiveInviteVideoDialog = new LiveCreaterReceiveInviteVideoDialog(this, msg);
        receiveInviteVideoDialog.setClickListener(new LiveCreaterReceiveInviteVideoDialog.ClickListener()
        {
            @Override
            public void onClickAccept()
            {
                getCreaterBusiness().acceptVideo(msg.getSender().getUser_id());
            }

            @Override
            public void onClickReject()
            {
                getCreaterBusiness().rejectVideo(msg.getSender().getUser_id(), CustomMsgRejectVideo.MSG_REJECT);
            }
        });
        receiveInviteVideoDialog.show();
    }

    @Override
    public void onLiveCreaterAcceptVideo(final String userId, final App_start_lianmaiActModel actModel)
    {
        super.onLiveCreaterAcceptVideo(userId, actModel);

        CustomMsgAcceptVideo msgAcceptVideo = new CustomMsgAcceptVideo();
        msgAcceptVideo.fillValue(actModel);
        IMHelper.sendMsgC2C(userId, msgAcceptVideo, new TIMValueCallBack<TIMMessage>()
        {
            @Override
            public void onError(int i, String s)
            {
            }

            @Override
            public void onSuccess(TIMMessage timMessage)
            {
                loadSmallVideo(actModel.getPlay_rtmp2_acc(), userId);
            }
        });
    }

    @Override
    public void onMsgUserStopVideo(CustomMsgUserStopVideo msg)
    {
        super.onMsgUserStopVideo(msg);
        UserModel sender = msg.getSender();
        stopSmallVideo(sender.getUser_id());
    }

    /**
     * 加载连麦观众视频
     *
     * @param userUrl 拉流地址
     * @param userId  用户id
     */
    private void loadSmallVideo(String userUrl, String userId)
    {
        if (TextUtils.isEmpty(userUrl))
        {
            return;
        }
        if (isVideoing)
        {
            return;
        }
        playView.show();
        playView.setVideo(userUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC, userId);
        playView.playVideo();
        pushSDK.setConfigLinkMicMain();
        isVideoing = true;
        LogUtil.i("loadSmallVideo:" + userUrl);
    }

    /**
     * 关闭连麦观众视频
     *
     * @param userId 观众id
     */
    private void stopSmallVideo(String userId)
    {
        playView.stopVideo();
        playView.hide();
        playView.setUserId(null);
        playView.getPlayer().setUrl(null);
        pushSDK.setConfigDefault();
        isVideoing = false;
        LogUtil.i("stopSmallVideo:" + userId);
    }

    /**
     * 销毁连麦观众视频
     */
    private void destroySmallVideo()
    {
        playView.destroyVideo();
        playView.hide();
        isVideoing = false;
        LogUtil.i("destroySmallVideo");
    }

    @Override
    public void onLiveCreaterRejectVideo(String userId, String reason)
    {
        super.onLiveCreaterRejectVideo(userId, reason);
        CustomMsgRejectVideo msgRejectVideo = new CustomMsgRejectVideo();
        msgRejectVideo.setMsg(CustomMsgRejectVideo.MSG_REJECT);
        IMHelper.sendMsgC2C(userId, msgRejectVideo, null);
    }

    @Override
    public void onMsgStopLive(CustomMsgStopLive msg)
    {
        super.onMsgStopLive(msg);
        exitRoom(true);
    }

    /**
     * 退出房间
     *
     * @param addLiveFinish true-显示直播结束界面；false-关闭当前Activity
     */
    protected void exitRoom(boolean addLiveFinish)
    {
        getCreaterBusiness().stopMonitor();
        removeRoomCountDownView();
        stopPush(true);
        stopSmallVideo(null);
        stopMusic();
        destroyIM();

        if (addLiveFinish)
        {
            addLiveFinish();
        } else
        {
            finish();
        }
    }

    @Override
    protected void addLiveFinish()
    {
        super.addLiveFinish();
        getCreaterBusiness().requestEndVideo(null);
    }

    @Override
    protected void destroyIM()
    {
        super.destroyIM();

        getCreaterIM().destroyIM();
        LiveInformation.getInstance().exitRoom();
    }

    /**
     * 主播离开
     */
    private void createrLeave()
    {
        if (!isCreaterLeave)
        {
            isCreaterLeave = true;
            requestUpdateLiveStateLeave();
            pausePush();
            getCreaterIM().sendCreaterLeaveMsg(null);
            if (getRoomMusicView() != null)
            {
                getRoomMusicView().onStopLifeCircle();
            }

            playView.stopVideo();
        }
    }

    /**
     * 主播回来
     */
    private void createrComeback()
    {
        if (isCreaterLeave)
        {
            isCreaterLeave = false;
            requestUpdateLiveStateComeback(null);
            resumePush();
            getCreaterIM().sendCreaterComebackMsg(null);
            if (getRoomMusicView() != null)
            {
                getRoomMusicView().onResumeLifeCircle();
            }

            playView.playVideo();
        }
    }

    @Override
    public void onEventMainThread(EUnLogin event)
    {
        exitRoom(false);
    }

    @Override
    public void onEventMainThread(EImOnForceOffline event)
    {
        exitRoom(true);
    }

    @Override
    public void onEventMainThread(EOnCallStateChanged event)
    {
        switch (event.state)
        {
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (isCreaterLeave)
                {
                    isCreaterLeaveByCall = false;
                } else
                {
                    isCreaterLeaveByCall = true;
                }
                createrLeave();
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (isCreaterLeaveByCall)
                {
                    createrComeback();
                    isCreaterLeaveByCall = false;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnect(TANetWorkUtil.netType type)
    {
        if (type == TANetWorkUtil.netType.mobile)
        {
            SDDialogConfirm dialog = new SDDialogConfirm(this);
            dialog.setTextContent("当前处于数据网络下，会耗费较多流量，是否继续？").setTextCancel("否").setTextConfirm("是").setmListener(new SDDialogCustom.SDDialogCustomListener()
            {

                @Override
                public void onDismiss(SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickConfirm(View v, SDDialogCustom dialog)
                {
                }

                @Override
                public void onClickCancel(View v, SDDialogCustom dialog)
                {
                    exitRoom(true);
                }
            }).show();
        }
        super.onConnect(type);
    }

    @Override
    public void onBackPressed()
    {
        if (isAuctioning())
        {
            showActionExitDialog();
        } else
        {
            showExitDialog();
        }
    }

    @Override
    protected void onClickCloseRoom(View v)
    {
        super.onClickCloseRoom(v);
        if (isAuctioning())
        {
            showActionExitDialog();
        } else
        {
            showExitDialog();
        }
    }

    private void showActionExitDialog()
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("您发起的竞拍暂未结束，不能关闭直播");
        dialog.setTextConfirm(null);
        dialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {

            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
            }
        });
        dialog.show();
    }

    private void showExitDialog()
    {
        showNormalExitDialog();
    }

    private void showNormalExitDialog()
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("确定要结束直播吗？");
        dialog.setmListener(new SDDialogCustom.SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {
            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                exitRoom(true);
            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
            }
        });
        dialog.show();
    }

    @Override
    protected void showSetBeautyDialog()
    {
        LiveSetBeautyDialog dialog = new LiveSetBeautyDialog(this);

        // 美颜
        int beautyProgress = LiveBeautyConfig.get().getBeautyProgress();
        dialog.setBeautyProgress(beautyProgress);
        dialog.setOnBeautySeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                pushSDK.inputBeauty(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                LiveBeautyConfig.get().setBeautyProgress(progress).save();
            }
        });

        // 美白
        int whiteProgress = LiveBeautyConfig.get().getWhiteProgress();
        dialog.setWhiteProgress(whiteProgress);
        dialog.setOnWhiteSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                pushSDK.inputWhite(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                int progress = seekBar.getProgress();
                LiveBeautyConfig.get().setWhiteProgress(progress).save();
            }
        });

        dialog.showBottom();
    }

    @Override
    public void onPushEvent(int i, Bundle bundle)
    {

    }

    @Override
    public void onNetStatus(Bundle bundle)
    {

    }

    @Override
    protected void sdkEnableFlash(boolean enable)
    {
        pushSDK.enableFlashLight(enable);
    }

    @Override
    protected void sdkEnableMic(boolean enable)
    {
        pushSDK.enableMic(enable);
    }

    @Override
    protected void sdkSwitchCamera()
    {
        pushSDK.switchCamera();
    }

    @Override
    protected boolean sdkIsBackCamera()
    {
        return pushSDK.isBackCamera();
    }

    @Override
    protected void sdkEnableBeauty(boolean enable)
    {
        pushSDK.enableBeauty(enable);
    }

    @Override
    protected void sdkEnableWhite(boolean enable)
    {
        pushSDK.enableWhite(enable);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        createrComeback();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        createrLeave();
    }

    @Override
    protected void onDestroy()
    {
        groupTryer.onDestroy();
        pushSDK.onDestroy();
        destroySmallVideo();
        super.onDestroy();
    }
}
