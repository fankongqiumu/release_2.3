package com.fanwe.live.activity.room;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.business.LiveBusiness;
import com.fanwe.live.business.LiveViewerBusiness;
import com.fanwe.live.dialog.LiveChatC2CDialog;
import com.fanwe.live.dialog.LiveRechargeDialog;
import com.fanwe.live.dialog.LiveRedEnvelopeNewDialog;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.LiveQualityData;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgRedEnvelope;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * PC端直播--观众界面
 */
public class LivePCLayoutActivity extends LiveActivity implements ILiveChangeRoomListener,
        LiveViewerBusiness.LiveViewerBusinessListener
{

//    protected View view_close_room; // 关闭房间

    private LiveViewerBusiness viewerBusiness;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
    }

    @Override
    protected void onKeyboardVisibilityChange(boolean visible, int height)
    {
        super.onKeyboardVisibilityChange(visible, height);
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

    public LiveBusiness getLiveBusiness()
    {
        return getViewerBusiness();
    }

    @Override
    public void onEventMainThread(EImOnNewMessages event)
    {
        super.onEventMainThread(event);
        getLiveBusiness().getMsgBusiness().parseLiveMsg(event.msg, getGroupId());
    }

    /**
     * 发送礼物的view显示
     *
     * @param view
     */
    protected void onShowSendGiftView(View view)
    {
    }

    /**
     * 发送礼物的view隐藏
     *
     * @param view
     */
    protected void onHideSendGiftView(View view)
    {
    }

    /**
     * 结束界面
     */
    protected void addLiveFinish()
    {
        // 子类实现
    }

    @Override
    public App_get_videoActModel getRoomInfo()
    {
        return getLiveBusiness().getRoomInfo();
    }

    protected SDRequestHandler requestRoomInfo()
    {
        //子类实现
        return null;
    }

    @Override
    public void onLiveRequestRoomInfoError(App_get_videoActModel actModel)
    {
        LogUtil.i("onLiveRequestRoomInfoError");
    }

    @Override
    public void onLiveRequestRoomInfoException(String msg)
    {
        LogUtil.i("onLiveRequestRoomInfoException");
    }

    @Override
    public void onLiveRefreshViewerList(List<UserModel> listModel)
    {
        LogUtil.i("onLiveRefreshViewerList");

    }

    @Override
    public void onLiveRemoveViewer(UserModel model)
    {
        LogUtil.i("onLiveRemoveViewer");

    }

    @Override
    public void onLiveInsertViewer(int position, UserModel model)
    {
        LogUtil.i("onLiveInsertViewer:" + position);

    }

    @Override
    public void onLiveTicketChange(long ticket)
    {
        LogUtil.i("onLiveTicketChange:" + ticket);

    }

    @Override
    public void onLiveViewerNumberChange(int viewerNumber)
    {
        LogUtil.i("onLiveViewerNumberChange:" + viewerNumber);

    }

    @Override
    public void onLiveBindCreaterData(UserModel model)
    {
        LogUtil.i("onLiveBindCreaterData");

    }

    @Override
    public void onLiveShowOperateViewer(boolean show)
    {
        LogUtil.i("onLiveShowOperateViewer:" + show);

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

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        LogUtil.i("onLiveRequestRoomInfoSuccess");
        if (TextUtils.isEmpty(getCreaterId()))
        {
            setCreaterId(actModel.getUser_id());
        }
        if (TextUtils.isEmpty(getGroupId()))
        {
            setGroupId(actModel.getGroup_id());
        }

        bindShowShareView();
        getLiveBusiness().startLiveQualityLooper(this);
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

    @Override
    public void openSendMsg(String content)
    {
        super.openSendMsg(content);
//        roomSendMsgView.setContent(content);
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
     * 发送礼物view是否可见
     *
     * @return
     */
    protected boolean isSendGiftViewVisible()
    {
        return false;
    }

    /**
     * 打开分享面板
     */
    public void openShare(final UMShareListener listener)
    {
        getLiveBusiness().openShare(this, listener);
    }

    @Override
    public boolean isPrivate()
    {
        return getLiveBusiness().isPrivate();
    }

    @Override
    public void onMsgRedEnvelope(CustomMsgRedEnvelope msg)
    {
        super.onMsgRedEnvelope(msg);
        LiveRedEnvelopeNewDialog dialog = new LiveRedEnvelopeNewDialog(this, msg);
        dialog.show();
    }

    @Override
    protected void onDestroy()
    {
        if (viewerBusiness != null)
        {
            viewerBusiness.onDestroy();
        }
        super.onDestroy();
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
    public void onLiveViewerShowCreaterLeave(boolean show)
    {
        LogUtil.i("onLiveViewerShowCreaterLeave:" + show);

    }

    @Override
    public void onLiveViewerStartJoinRoom(boolean delay)
    {
        LogUtil.i("onLiveViewerStartJoinRoom:" + delay);
    }
}
