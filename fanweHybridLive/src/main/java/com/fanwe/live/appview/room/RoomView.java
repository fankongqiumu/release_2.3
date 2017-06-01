package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;

import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.activity.room.ILiveChangeRoomListener;
import com.fanwe.live.activity.room.LivePlaybackActivity;
import com.fanwe.live.appview.BaseAppView;
import com.fanwe.live.business.LiveMsgBusiness;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.model.custommsg.CustomMsgAcceptVideo;
import com.fanwe.live.model.custommsg.CustomMsgCreaterComeback;
import com.fanwe.live.model.custommsg.CustomMsgCreaterLeave;
import com.fanwe.live.model.custommsg.CustomMsgData;
import com.fanwe.live.model.custommsg.CustomMsgUserStopVideo;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.CustomMsgGift;
import com.fanwe.live.model.custommsg.CustomMsgInviteVideo;
import com.fanwe.live.model.custommsg.CustomMsgLight;
import com.fanwe.live.model.custommsg.CustomMsgPopMsg;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.fanwe.live.model.custommsg.CustomMsgRejectVideo;
import com.fanwe.live.model.custommsg.CustomMsgStopLive;
import com.fanwe.live.model.custommsg.CustomMsgViewerJoin;
import com.fanwe.live.model.custommsg.CustomMsgViewerQuit;
import com.fanwe.live.model.custommsg.CustomMsgWarning;
import com.fanwe.live.model.custommsg.MsgModel;

/**
 * Created by Administrator on 2016/8/4.
 */
public class RoomView extends BaseAppView implements ILiveChangeRoomListener, LiveMsgBusiness.LiveMsgBusinessListener
{
    private boolean isPlayback;
    private LiveMsgBusiness msgBusiness;

    public RoomView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomView(Context context)
    {
        super(context);
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

    public void setPlayback(boolean playback)
    {
        isPlayback = playback;
    }

    public boolean isPlayback()
    {
        return getActivity() instanceof LivePlaybackActivity || isPlayback;
    }

    public LiveInfo getLiveInfo()
    {
        if (getActivity() instanceof LiveInfo)
        {
            return (LiveInfo) getActivity();
        } else
        {
            return null;
        }
    }

    public void onEventMainThread(EImOnNewMessages event)
    {
        getMsgBusiness().parseLiveMsg(event.msg, getLiveInfo().getGroupId());
    }

    @Override
    public void onChangeRoomActionComplete()
    {

    }

    @Override
    public void onChangeRoomComplete()
    {

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
}
