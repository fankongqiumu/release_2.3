package com.fanwe.live.business;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_check_lianmaiActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.custommsg.CustomMsgCreaterComeback;
import com.fanwe.live.model.custommsg.CustomMsgCreaterLeave;

/**
 * 直播间观众业务类
 */
public class LiveViewerBusiness extends LiveBusiness
{
    private LiveViewerBusinessListener businessListener;
    private boolean canJoinRoom = true;
    private SDRequestHandler requestCheckLianmaiHandler;

    public LiveViewerBusiness(LiveInfo liveInfo)
    {
        super(liveInfo);
    }

    public void setBusinessListener(LiveViewerBusinessListener businessListener)
    {
        this.businessListener = businessListener;
        super.setBusinessListener(businessListener);
    }

    public void setCanJoinRoom(boolean canJoinRoom)
    {
        this.canJoinRoom = canJoinRoom;
    }

    public void startJoinRoom(boolean delay)
    {
        if (canJoinRoom)
        {
            businessListener.onLiveViewerStartJoinRoom(delay);
        }
    }

    @Override
    protected void onRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onRequestRoomInfoSuccess(actModel);

        if (actModel.getLive_in() == 1)
        {
            businessListener.onLiveViewerShowCreaterLeave(actModel.getOnline_status() == 0);
        } else
        {
            businessListener.onLiveViewerShowCreaterLeave(false);
        }
    }

    /**
     * 检查连麦权限
     */
    public void requestCheckLianmai(AppRequestCallback<App_check_lianmaiActModel> listener)
    {
        cancelRequestCheckLianmaiHandler();
        requestCheckLianmaiHandler = CommonInterface.requestCheckLianmai(getLiveInfo().getRoomId(), listener);
    }

    private void cancelRequestCheckLianmaiHandler()
    {
        if (requestCheckLianmaiHandler != null)
        {
            requestCheckLianmaiHandler.cancel();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        cancelRequestCheckLianmaiHandler();
    }

    @Override
    public void onMsgCreaterLeave(CustomMsgCreaterLeave msg)
    {
        super.onMsgCreaterLeave(msg);
        businessListener.onLiveViewerShowCreaterLeave(true);
    }

    @Override
    public void onMsgCreaterComeback(CustomMsgCreaterComeback msg)
    {
        super.onMsgCreaterComeback(msg);
        businessListener.onLiveViewerShowCreaterLeave(false);
    }

    public interface LiveViewerBusinessListener extends LiveBusinessListener
    {
        void onLiveViewerShowCreaterLeave(boolean show);

        void onLiveViewerStartJoinRoom(boolean delay);
    }

}
