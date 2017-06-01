package com.fanwe.live.business;

import android.app.Activity;
import android.content.Context;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.umeng.UmengSocialManager;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.listener.SDIterateListener;
import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.LiveInformation;
import com.fanwe.live.R;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_shareActModel;
import com.fanwe.live.model.App_viewerActModel;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.RoomShareModel;
import com.fanwe.live.model.RoomUserModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgCreaterComeback;
import com.fanwe.live.model.custommsg.CustomMsgCreaterLeave;
import com.fanwe.live.model.custommsg.CustomMsgData;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgGift;
import com.fanwe.live.model.custommsg.CustomMsgInviteVideo;
import com.fanwe.live.model.custommsg.CustomMsgLight;
import com.fanwe.live.model.custommsg.CustomMsgLiveMsg;
import com.fanwe.live.model.custommsg.CustomMsgPopMsg;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.fanwe.live.model.custommsg.CustomMsgRejectVideo;
import com.fanwe.live.model.custommsg.CustomMsgStopLive;
import com.fanwe.live.model.custommsg.CustomMsgUserStopVideo;
import com.fanwe.live.model.custommsg.CustomMsgViewerJoin;
import com.fanwe.live.model.custommsg.CustomMsgViewerQuit;
import com.fanwe.live.model.custommsg.CustomMsgWarning;
import com.fanwe.live.model.custommsg.MsgModel;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 直播间公共业务类
 */
public class LiveBusiness implements LiveMsgBusiness.LiveMsgBusinessListener
{

    private LiveInfo liveInfo;
    private LiveMsgBusiness msgBusiness;
    private App_get_videoActModel roomInfoActModel;
    private SDRequestHandler requestRoomInfoHandler;
    private LiveBusinessListener businessListener;
    private List<UserModel> listViewer = new ArrayList<>();
    private SDLooper viewerListLooper;
    private SDLooper liveQualityLooper;
    private SDRequestHandler requestViewerListHandler;
    /**
     * 是否正在连麦中
     */
    private boolean isVideoing = false;
    /**
     * 观众数量
     */
    private int viewerNumber;
    /**
     * 印票数量
     */
    private long ticket;
    /**
     * 最后一次刷新观众列表的时间
     */
    private long lastViewerListTime;

    public LiveBusiness(LiveInfo liveInfo)
    {
        this.liveInfo = liveInfo;
    }

    public void setBusinessListener(LiveBusinessListener businessListener)
    {
        this.businessListener = businessListener;
    }

    public void setVideoing(boolean videoing)
    {
        isVideoing = videoing;
    }

    public LiveInfo getLiveInfo()
    {
        return liveInfo;
    }

    public boolean isPCCreate()
    {
        if (getRoomInfo() != null)
        {
            return getRoomInfo().getCreate_type() == 1;
        }
        return false;
    }

    public LiveQualityData getLiveQualityData()
    {
        return businessListener.onLiveGetLiveQualityData();
    }

    /**
     * 设置观众数量
     *
     * @param number
     */
    public void setViewerNumber(int number)
    {
        this.viewerNumber = number;

        if (this.viewerNumber < 0)
        {
            this.viewerNumber = 0;
        }
        notifyViewerNumberChange();
    }

    /**
     * 获得主播印票
     *
     * @return
     */
    public long getTicket()
    {
        return ticket;
    }

    /**
     * 设置印票
     *
     * @param ticket
     */
    public void setTicket(long ticket)
    {
        if (ticket >= this.ticket)
        {
            this.ticket = ticket;
        }

        if (this.ticket < 0)
        {
            this.ticket = 0;
        }
        notifyTicketChange();
    }

    /**
     * 清空印票
     */
    public void clearTicket()
    {
        this.ticket = 0;
        notifyTicketChange();
    }

    /**
     * 通知观众数量发生变化
     */
    private void notifyViewerNumberChange()
    {
        businessListener.onLiveViewerNumberChange(viewerNumber);
    }

    /**
     * 通知印票发生变化
     */
    private void notifyTicketChange()
    {
        businessListener.onLiveTicketChange(ticket);
    }

    public LiveMsgBusiness getMsgBusiness()
    {
        if (msgBusiness == null)
        {
            msgBusiness = new LiveMsgBusiness();
            msgBusiness.setBusinessListener(this);
        }
        return msgBusiness;
    }

    /**
     * 获得房间信息
     */
    public App_get_videoActModel getRoomInfo()
    {
        return roomInfoActModel;
    }

    /**
     * 是否私密直播
     *
     * @return
     */
    public boolean isPrivate()
    {
        if (getRoomInfo() != null)
        {
            return getRoomInfo().getIs_private() == 1;
        } else
        {
            return false;
        }
    }

    /**
     * 发送分享成功消息
     *
     * @param groupId
     */
    public void sendShareSuccessMsg(final String groupId)
    {
        UserModel userModel = UserModelDao.query();
        if (userModel == null)
        {
            return;
        }

        final CustomMsgLiveMsg msg = new CustomMsgLiveMsg();
        msg.setDesc(userModel.getNick_name() + " 分享了主播");
        IMHelper.sendMsgGroup(groupId, msg, new TIMValueCallBack<TIMMessage>()
        {
            @Override
            public void onError(int i, String s)
            {
            }

            @Override
            public void onSuccess(TIMMessage timMessage)
            {
                IMHelper.postMsgLocal(msg, groupId);
            }
        });
    }

    public SDRequestHandler requestRoomInfo(int room_id,
                                            int is_vod,
                                            int type,
                                            int has_scroll,
                                            int sex,
                                            int cate_id,
                                            String city,
                                            String private_key)
    {
        cancelRequestRoomInfoHandler();
        requestRoomInfoHandler = CommonInterface.requestRoomInfo(room_id, is_vod, type, has_scroll, sex, cate_id, city, private_key, new AppRequestCallback<App_get_videoActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                setRoomInfo(actModel);
                if (actModel.isOk())
                {
                    onRequestRoomInfoSuccess(actModel);
                } else
                {
                    onRequestRoomInfoError(actModel);
                }
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);

                String msg = "request error";
                if (resp.getThrowable() != null)
                {
                    msg = resp.getThrowable().toString();
                }
                onRequestRoomInfoException(msg);
            }
        });
        return requestRoomInfoHandler;
    }

    public void setRoomInfo(App_get_videoActModel actModel)
    {
        this.roomInfoActModel = actModel;
    }

    /**
     * 请求房间信息成功
     */
    protected void onRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        LiveInformation.getInstance().setVideoType(actModel.getVideo_type());
        LiveInformation.getInstance().setPrivate(actModel.getIs_private() == 1);

        // 绑定主播数据
        RoomUserModel roomUserModel = actModel.getPodcast();
        if (roomUserModel != null)
        {
            UserModel userModel = roomUserModel.getUser();
            if (userModel != null)
            {
                businessListener.onLiveBindCreaterData(userModel);
                clearTicket();
                setTicket(userModel.getTicket());
            }
        }

        bindShowOperateViewer(actModel);

        // 绑定观众数量
        setViewerNumber(actModel.getViewer_num());
        onRequestViewerListSuccess(actModel.getViewer());
        businessListener.onLiveRequestRoomInfoSuccess(actModel);
    }

    /**
     * 绑定显示隐藏私密直播邀请观众的布局
     *
     * @param actModel
     */
    private void bindShowOperateViewer(App_get_videoActModel actModel)
    {
        if (getLiveInfo().isCreater())
        {
            businessListener.onLiveShowOperateViewer(isPrivate());
        } else
        {
            if (isPrivate())
            {
                RoomUserModel roomUserModel = actModel.getPodcast();
                if (roomUserModel != null)
                {
                    businessListener.onLiveShowOperateViewer(roomUserModel.getHas_admin() == 1);
                }
            } else
            {
                businessListener.onLiveShowOperateViewer(false);
            }
        }
    }

    /**
     * 请求房间信息成功，但是业务失败
     */
    protected void onRequestRoomInfoError(App_get_videoActModel actModel)
    {
        businessListener.onLiveRequestRoomInfoError(actModel);
    }

    protected void onRequestRoomInfoException(String msg)
    {
        businessListener.onLiveRequestRoomInfoException(msg);
    }

    /**
     * 请求观众列表
     */
    public void requestViewerList()
    {
        cancelRequestViewerListHandler();
        // 加载观看者数量
        String groupId = getLiveInfo().getGroupId();
        requestViewerListHandler = CommonInterface.requestViewerList(groupId, 1, new AppRequestCallback<App_viewerActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    onRequestViewerListSuccess(actModel);
                    startViewerListLooper();
                }
            }
        });
    }

    /**
     * 请求观众列表成功回调
     *
     * @param actModel
     */
    public void onRequestViewerListSuccess(App_viewerActModel actModel)
    {
        if (actModel != null)
        {
            setViewerNumber(actModel.getWatch_number());
            setViewerList(actModel.getList());
        }
    }

    public void setViewerList(List<UserModel> list)
    {
        listViewer.clear();
        if (list != null)
        {
            listViewer.addAll(list);
        }

        removeCreaterIfNeed(listViewer);
        businessListener.onLiveRefreshViewerList(listViewer);
    }

    private void removeCreaterIfNeed(List<UserModel> listModel)
    {
        if (getRoomInfo() != null && getRoomInfo().getLive_in() == 1)
        {
            SDCollectionUtil.iterate(listModel, new SDIterateListener<UserModel>()
            {
                @Override
                public boolean next(int i, UserModel item, Iterator<UserModel> it)
                {
                    if (item.getUser_id().equals(getLiveInfo().getCreaterId()))
                    {
                        it.remove();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void startViewerListLooper()
    {
        long period = -1;
        if (getLiveInfo().isCreater())
        {
            period = 20 * 1000;
        } else
        {
            Random random = new Random();
            if (viewerNumber < 50)
            {
                period = (random.nextInt(8) + 8) * 1000;
            } else if (viewerNumber < 500)
            {
                period = (random.nextInt(11) + 20) * 1000;
            } else if (viewerNumber < 2000)
            {
                period = (random.nextInt(21) + 30) * 1000;
            } else
            {
                period = (random.nextInt(21) + 60) * 1000;
            }
        }

        if (period > 0)
        {
            LogUtil.i("refresh viewer list period:" + period + ",viewerNumber:" + viewerNumber);
            if (viewerListLooper == null)
            {
                viewerListLooper = new SDSimpleLooper();
            }
            viewerListLooper.start(period, period, new Runnable()
            {

                @Override
                public void run()
                {
                    requestViewerList();
                }
            });
        }
    }

    public void stopViewerListLooper()
    {
        if (viewerListLooper != null)
        {
            viewerListLooper.stop();
        }
    }

    /**
     * 开始显示直播质量
     */
    public void startLiveQualityLooper(Context context)
    {
        boolean show = context.getResources().getBoolean(R.bool.show_live_sdk_info);
        if (!show)
        {
            return;
        }
        if (liveQualityLooper == null)
        {
            liveQualityLooper = new SDSimpleLooper();
        }
        liveQualityLooper.start(1000, new Runnable()
        {
            @Override
            public void run()
            {
                businessListener.onLiveUpdateLiveQualityData(getLiveQualityData());
            }
        });
    }

    public void stopLiveQualityLooper()
    {
        if (liveQualityLooper != null)
        {
            liveQualityLooper.stop();
        }
    }

    /**
     * 分享
     */
    public void openShare(Activity activity, final UMShareListener listener)
    {
        if (getRoomInfo() == null)
        {
            return;
        }
        final RoomShareModel shareModel = getRoomInfo().getShare();
        if (shareModel == null)
        {
            return;
        }
        UmengSocialManager.openShare(shareModel.getShare_title(), shareModel.getShare_content(), shareModel.getShare_imageUrl(),
                shareModel.getShare_url(), activity, new UMShareListener()
                {

                    @Override
                    public void onStart(SHARE_MEDIA share_media)
                    {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA media)
                    {
                        LogUtil.i("openShare success");
                        String shareKey = shareModel.getShare_key();
                        CommonInterface.requestShareComplete(media.toString(), shareKey, new AppRequestCallback<App_shareActModel>()
                        {
                            @Override
                            protected void onSuccess(SDResponse sdResponse)
                            {
                                if (actModel.isOk())
                                {
                                    //如果分享奖励大于0.提示奖励内容
                                    if (actModel.getShare_award() > 0)
                                    {
                                        CommonInterface.requestMyUserInfo(null);
                                        SDToast.showToast(actModel.getShare_award_info());
                                    }
                                }
                            }
                        });

                        if (listener != null)
                        {
                            listener.onResult(media);
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA media, Throwable throwable)
                    {
                        if (listener != null)
                        {
                            listener.onError(media, throwable);
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA media)
                    {
                        if (listener != null)
                        {
                            listener.onCancel(media);
                        }
                    }
                });
    }

    public void cancelRequestRoomInfoHandler()
    {
        if (requestRoomInfoHandler != null)
        {
            requestRoomInfoHandler.cancel();
        }
    }

    public void cancelRequestViewerListHandler()
    {
        if (requestViewerListHandler != null)
        {
            requestViewerListHandler.cancel();
        }
    }

    public void onDestroy()
    {
        cancelRequestRoomInfoHandler();
        cancelRequestViewerListHandler();
        stopViewerListLooper();
        stopLiveQualityLooper();
    }

    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg)
    {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgInviteVideo(CustomMsgInviteVideo msg)
    {

    }

    @Override
    public void onMsgAcceptVideo(CustomMsgAcceptVideo msg)
    {

    }

    @Override
    public void onMsgRejectVideo(CustomMsgRejectVideo msg)
    {

    }

    @Override
    public void onMsgUserStopVideo(CustomMsgUserStopVideo msg)
    {

    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg)
    {

    }

    @Override
    public void onMsgStopLive(CustomMsgStopLive msg)
    {

    }

    @Override
    public void onMsgPrivate(MsgModel msg)
    {

    }

    @Override
    public void onMsgAuction(MsgModel msg)
    {

    }

    @Override
    public void onMsgShop(MsgModel msg)
    {

    }

    @Override
    public void onMsgPayMode(MsgModel msg)
    {

    }

    @Override
    public void onMsgGame(MsgModel msg)
    {

    }

    @Override
    public void onMsgGift(CustomMsgGift msg)
    {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgPopMsg(CustomMsgPopMsg msg)
    {
        setTicket(msg.getTotal_ticket());
    }

    @Override
    public void onMsgViewerJoin(CustomMsgViewerJoin msg)
    {
        insertUser(msg.getSender());
        setViewerNumber(viewerNumber + 1);
    }

    private void insertUser(UserModel userModel)
    {
        if (userModel == null)
        {
            return;
        }
        if (listViewer.contains(userModel))
        {
            return;
        }

        int sortNum = userModel.getSort_num();
        {
            if (sortNum <= 0)
            {
                sortNum = userModel.getUser_level();
            }
        }

        int count = listViewer.size();
        int position = 0;
        for (int i = count - 1; i >= 0; i--)
        {
            UserModel item = listViewer.get(i);
            int itemNum = item.getUser_level();
            if (sortNum > itemNum)
            {
                continue;
            } else
            {
                position = i + 1;
                break;
            }
        }
        LogUtil.i("insert:" + position + ",sortNum:" + sortNum);

        businessListener.onLiveInsertViewer(position, userModel);
    }

    @Override
    public void onMsgViewerQuit(CustomMsgViewerQuit msg)
    {
        businessListener.onLiveRemoveViewer(msg.getSender());
    }

    @Override
    public void onMsgCreaterLeave(CustomMsgCreaterLeave msg)
    {

    }

    @Override
    public void onMsgCreaterComeback(CustomMsgCreaterComeback msg)
    {

    }

    @Override
    public void onMsgLight(CustomMsgLight msg)
    {

    }

    @Override
    public void onMsgListMsg(MsgModel msg)
    {

    }

    @Override
    public void onMsgWarning(CustomMsgWarning msgWarning)
    {

    }

    @Override
    public void onMsgData(CustomMsgData msg)
    {
        if (ApkConstant.DEBUG)
        {
            LogUtil.i("onMsgData " + msg.getData_type() + ":" + msg.getData());
        }

        if (LiveConstant.CustomMsgDataType.DATA_VIEWER_LIST == msg.getData_type())
        {
            App_viewerActModel actModel = msg.parseData(App_viewerActModel.class);
            if (actModel != null)
            {
                if (actModel.getTime() > lastViewerListTime)
                {
                    actModel.parseData();
                    onRequestViewerListSuccess(actModel);
                    lastViewerListTime = actModel.getTime();
                }
            }
        }
    }

    public interface LiveBusinessListener
    {
        /**
         * 请求房间信息成功
         */
        void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel);

        /**
         * 请求房间信息成功，但是业务失败
         */
        void onLiveRequestRoomInfoError(App_get_videoActModel actModel);

        /**
         * 请求房间信息异常
         *
         * @param msg
         */
        void onLiveRequestRoomInfoException(String msg);

        /**
         * 刷新观众列表
         *
         * @param listModel
         */
        void onLiveRefreshViewerList(List<UserModel> listModel);

        /**
         * 移除观众列表中的某个观众
         *
         * @param model
         */
        void onLiveRemoveViewer(UserModel model);

        /**
         * 插入某个观众到观众列表
         *
         * @param position 插入位置
         * @param model
         */
        void onLiveInsertViewer(int position, UserModel model);

        /**
         * 直播间印票变化
         *
         * @param ticket
         */
        void onLiveTicketChange(long ticket);

        /**
         * 直播间观众数量发生变化
         *
         * @param viewerNumber
         */
        void onLiveViewerNumberChange(int viewerNumber);

        /**
         * 绑定直播间主播数据
         */
        void onLiveBindCreaterData(UserModel model);

        /**
         * 显示隐藏私密直播邀请观众的布局
         *
         * @param show
         */
        void onLiveShowOperateViewer(boolean show);

        /**
         * 获得直播间质量数据
         *
         * @return
         */
        LiveQualityData onLiveGetLiveQualityData();

        /**
         * 更新直播间质量数据
         *
         * @param data
         */
        void onLiveUpdateLiveQualityData(LiveQualityData data);


    }

}
