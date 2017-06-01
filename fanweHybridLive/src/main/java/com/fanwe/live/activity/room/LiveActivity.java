package com.fanwe.live.activity.room;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.fanwe.games.model.App_InitGamesActModel;
import com.fanwe.hybrid.activity.BaseActivity;
import com.fanwe.hybrid.event.EUnLogin;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.LiveCreaterIM;
import com.fanwe.live.LiveViewerIM;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.business.LiveBusiness;
import com.fanwe.live.business.LiveCreaterBusiness;
import com.fanwe.live.business.LiveMsgBusiness;
import com.fanwe.live.business.LiveViewerBusiness;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.event.EImOnForceOffline;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.event.EOnCallStateChanged;
import com.fanwe.live.model.App_end_videoActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_monitorActModel;
import com.fanwe.live.model.App_start_lianmaiActModel;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgCreaterComeback;
import com.fanwe.live.model.custommsg.CustomMsgCreaterLeave;
import com.fanwe.live.model.custommsg.CustomMsgData;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgGift;
import com.fanwe.live.model.custommsg.CustomMsgInviteVideo;
import com.fanwe.live.model.custommsg.CustomMsgLight;
import com.fanwe.live.model.custommsg.CustomMsgPopMsg;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.fanwe.live.model.custommsg.CustomMsgRejectVideo;
import com.fanwe.live.model.custommsg.CustomMsgStopLive;
import com.fanwe.live.model.custommsg.CustomMsgUserStopVideo;
import com.fanwe.live.model.custommsg.CustomMsgViewerJoin;
import com.fanwe.live.model.custommsg.CustomMsgViewerQuit;
import com.fanwe.live.model.custommsg.CustomMsgWarning;
import com.fanwe.live.model.custommsg.MsgModel;
import com.umeng.socialize.UMShareListener;

import java.util.List;

/**
 * 直播间基类
 * <p/>
 * Created by Administrator on 2016/8/4.
 */
public class LiveActivity extends BaseActivity implements LiveInfo, LiveMsgBusiness.LiveMsgBusinessListener, LiveViewerBusiness.LiveViewerBusinessListener, LiveCreaterBusiness.LiveCreaterBusinessListener
{
    /**
     * 房间id(int)
     */
    public static final String EXTRA_ROOM_ID = "extra_room_id";
    /**
     * 讨论组id(String)
     */
    public static final String EXTRA_GROUP_ID = "extra_group_id";
    /**
     * 主播identifier(String)
     */
    public static final String EXTRA_CREATER_ID = "extra_creater_id";

    /**
     * 房间id
     */
    private int roomId;
    /**
     * 群聊id
     */
    private String strGroupId;
    /**
     * 主播id
     */
    private String strCreaterId;
    /**
     * 当前用户id
     */
    protected String strUserId;

    private LiveViewerIM viewerIM;
    private LiveCreaterIM createrIM;
    private LiveMsgBusiness msgBusiness;
    private LiveViewerBusiness viewerBusiness;
    private LiveCreaterBusiness createrBusiness;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 不锁屏

        roomId = getIntent().getIntExtra(EXTRA_ROOM_ID, 0);
        strGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        strCreaterId = getIntent().getStringExtra(EXTRA_CREATER_ID);

        UserModel user = UserModelDao.query();
        if (user != null)
        {
            strUserId = user.getUser_id();
        }

        msgBusiness = new LiveMsgBusiness();
        msgBusiness.setBusinessListener(this);
    }

    public LiveViewerBusiness getViewerBusiness()
    {
        if (viewerBusiness == null)
        {
            viewerBusiness = new LiveViewerBusiness(this);
            viewerBusiness.setBusinessListener(this);
        }
        return viewerBusiness;
    }

    public LiveCreaterBusiness getCreaterBusiness()
    {
        if (createrBusiness == null)
        {
            createrBusiness = new LiveCreaterBusiness(this);
            createrBusiness.setBusinessListener(this);
        }
        return createrBusiness;
    }

    public LiveBusiness getLiveBusiness()
    {
        if (isCreater())
        {
            return getCreaterBusiness();
        } else
        {
            return getViewerBusiness();
        }
    }

    /**
     * 获得观众的IM操作类
     *
     * @return
     */
    public LiveViewerIM getViewerIM()
    {
        if (viewerIM == null)
        {
            viewerIM = new LiveViewerIM();
        }
        return viewerIM;
    }

    /**
     * 获得主播的IM操作类
     *
     * @return
     */
    public LiveCreaterIM getCreaterIM()
    {
        if (createrIM == null)
        {
            createrIM = new LiveCreaterIM();
        }
        return createrIM;
    }

    @Override
    public String getGroupId()
    {
        if (strGroupId == null)
        {
            strGroupId = "";
        }
        return strGroupId;
    }

    public void setGroupId(String groupId)
    {
        this.strGroupId = groupId;
    }

    @Override
    public String getCreaterId()
    {
        if (strCreaterId == null)
        {
            strCreaterId = "";
        }
        return strCreaterId;
    }

    public void setCreaterId(String createrId)
    {
        this.strCreaterId = createrId;
    }

    @Override
    public int getRoomId()
    {
        return roomId;
    }

    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }

    @Override
    public App_get_videoActModel getRoomInfo()
    {
        return getLiveBusiness().getRoomInfo();
    }

    @Override
    public boolean isPrivate()
    {
        return getLiveBusiness().isPrivate();
    }

    @Override
    public boolean isCreater()
    {
        //子类实现
        return false;
    }

    @Override
    public boolean isAuctioning()
    {
        //子类实现
        return false;
    }

    @Override
    public void openSendMsg(String content)
    {
        // 子类实现
    }

    /**
     * 打开分享面板
     */
    public void openShare(UMShareListener listener)
    {
        getLiveBusiness().openShare(this, listener);
    }

    /**
     * 请求房间信息
     *
     * @return
     */
    protected SDRequestHandler requestRoomInfo()
    {
        //子类实现
        return null;
    }

    /**
     * 观众加入聊天组成功回调
     *
     * @param groupId
     */
    protected void onSuccessJoinGroup(String groupId)
    {

    }

    /**
     * 接收im新消息
     *
     * @param event
     */
    public void onEventMainThread(EImOnNewMessages event)
    {
        msgBusiness.parseLiveMsg(event.msg, getGroupId());
        getLiveBusiness().getMsgBusiness().parseLiveMsg(event.msg, getGroupId());

        try
        {
            if (LiveConstant.CustomMsgType.MSG_DATA == event.msg.getCustomMsgType())
            {
                String groupId = getGroupId();
                String peer = event.msg.getConversationPeer();
                if (!TextUtils.isEmpty(groupId))
                {
                    if (!groupId.equals(peer))
                    {
                        // 别的直播间消息
                        IMHelper.quitGroup(peer, null);
                        LogUtil.i("quitGroup other room:" + peer);
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg)
    {
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
    public void onMsgGame(MsgModel msg)
    {
    }

    @Override
    public void onMsgGift(CustomMsgGift msg)
    {
    }

    @Override
    public void onMsgPopMsg(CustomMsgPopMsg msg)
    {
    }

    @Override
    public void onMsgViewerJoin(CustomMsgViewerJoin msg)
    {
    }

    @Override
    public void onMsgViewerQuit(CustomMsgViewerQuit msg)
    {
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

    }

    @Override
    public void onMsgPayMode(MsgModel msg)
    {
    }

    /**
     * 退出登录
     *
     * @param event
     */
    public void onEventMainThread(EUnLogin event)
    {
        //子类实现
    }

    /**
     * 帐号异地登录
     *
     * @param event
     */
    public void onEventMainThread(EImOnForceOffline event)
    {
        //子类实现
    }

    /**
     * 电话监听
     *
     * @param event
     */
    public void onEventMainThread(EOnCallStateChanged event)
    {
        //子类实现
    }

    /**
     * 初始化IM
     */
    protected void initIM()
    {
        //子类实现
    }

    /**
     * 销毁IM
     */
    protected void destroyIM()
    {
        //子类实现
    }

    @Override
    protected void onDestroy()
    {
        viewerIM = null;
        createrIM = null;
        if (viewerBusiness != null)
        {
            viewerBusiness.onDestroy();
            viewerBusiness = null;
        }
        if (createrBusiness != null)
        {
            createrBusiness.onDestroy();
            createrBusiness = null;
        }
        super.onDestroy();
    }

    //sdk

    /**
     * 闪关灯
     *
     * @param enable
     */
    protected void sdkEnableFlash(boolean enable)
    {
        //子类实现
    }

    /**
     * 播放声音
     *
     * @param enable
     */
    protected void sdkEnableAudioDataVolume(boolean enable)
    {
        //子类实现
    }

    /**
     * 麦克风
     *
     * @param enable
     */
    protected void sdkEnableMic(boolean enable)
    {
        //子类实现
    }

    /**
     * 打开后置摄像头
     */
    protected void sdkEnableBackCamera()
    {
        //子类实现
    }

    /**
     * 打开最后一次打开的摄像头
     */
    protected void sdkEnableLastCamera()
    {
        //子类实现
    }

    /**
     * 打开前置摄像头
     */
    protected void sdkEnableFrontCamera()
    {
        //子类实现
    }

    /**
     * 关闭摄像头
     */
    protected void sdkDisableCamera()
    {
        //子类实现
    }

    /**
     * 切换摄像头
     */
    protected void sdkSwitchCamera()
    {
        //子类实现
    }

    /**
     * 当前是否是后置摄像头
     *
     * @return
     */
    protected boolean sdkIsBackCamera()
    {
        //子类实现
        return false;
    }

    /**
     * 美颜
     *
     * @param enable
     */
    protected void sdkEnableBeauty(boolean enable)
    {
        //子类实现
    }

    /**
     * 美白
     *
     * @param enable
     */
    protected void sdkEnableWhite(boolean enable)
    {
        //子类实现
    }

    /**
     * 如果正在连麦则切换为普通观众
     */
    protected void sdkchange2Viewer()
    {
        //子类实现
    }

    @Override
    public LiveCreaterBusiness.CreaterMonitorData onLiveCreaterGetMonitorData()
    {
        return null;
    }

    @Override
    public void onLiveCreaterRequestMonitorSuccess(App_monitorActModel actModel)
    {

    }

    @Override
    public void onLiveCreaterRequestInitPluginsSuccess(App_InitGamesActModel actModel)
    {

    }

    @Override
    public void onLiveCreaterRequestEndVideoSuccess(App_end_videoActModel actModel)
    {

    }

    @Override
    public void onLiveCreaterAcceptVideo(String userId, App_start_lianmaiActModel actModel)
    {

    }

    @Override
    public void onLiveCreaterRejectVideo(String userId, String reason)
    {

    }

    @Override
    public void onLiveViewerShowCreaterLeave(boolean show)
    {

    }

    @Override
    public void onLiveViewerStartJoinRoom(boolean delay)
    {

    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {

    }

    @Override
    public void onLiveRequestRoomInfoError(App_get_videoActModel actModel)
    {

    }

    @Override
    public void onLiveRequestRoomInfoException(String msg)
    {

    }

    @Override
    public void onLiveRefreshViewerList(List<UserModel> listModel)
    {

    }

    @Override
    public void onLiveRemoveViewer(UserModel model)
    {

    }

    @Override
    public void onLiveInsertViewer(int position, UserModel model)
    {

    }

    @Override
    public void onLiveTicketChange(long ticket)
    {

    }

    @Override
    public void onLiveViewerNumberChange(int viewerNumber)
    {

    }

    @Override
    public void onLiveBindCreaterData(UserModel model)
    {

    }

    @Override
    public void onLiveShowOperateViewer(boolean show)
    {

    }

    @Override
    public LiveQualityData onLiveGetLiveQualityData()
    {
        return null;
    }

    @Override
    public void onLiveUpdateLiveQualityData(LiveQualityData data)
    {

    }
}
