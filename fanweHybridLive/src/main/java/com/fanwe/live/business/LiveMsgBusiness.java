package com.fanwe.live.business;

import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.LiveConstant;
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

/**
 * 直播间消息业务类
 */
public class LiveMsgBusiness
{
    private LiveMsgBusinessListener businessListener;

    public void setBusinessListener(LiveMsgBusinessListener businessListener)
    {
        this.businessListener = businessListener;
    }

    /**
     * 解析直播间消息
     */
    public final void parseLiveMsg(MsgModel msg, String groupId)
    {
        String peer = msg.getConversationPeer();
        int type = msg.getCustomMsgType();
        if (peer.equals(groupId))
        {
            // Group消息
            if (LiveConstant.CustomMsgType.MSG_RED_ENVELOPE == type)
            {
                onMsgRedEnvelope(msg);
            } else if (LiveConstant.CustomMsgType.MSG_GIFT == type)
            {
                onMsgGift(msg);
            } else if (LiveConstant.CustomMsgType.MSG_POP_MSG == type)
            {
                onMsgPopMsg(msg);
            } else if (LiveConstant.CustomMsgType.MSG_VIEWER_JOIN == type)
            {
                onMsgViewerJoin(msg);
            } else if (LiveConstant.CustomMsgType.MSG_VIEWER_QUIT == type)
            {
                onMsgViewerQuit(msg);
            } else if (LiveConstant.CustomMsgType.MSG_CREATER_LEAVE == type)
            {
                onMsgCreaterLeave(msg);
            } else if (LiveConstant.CustomMsgType.MSG_CREATER_COME_BACK == type)
            {
                onMsgCreaterComeback(msg);
            } else if (LiveConstant.CustomMsgType.MSG_LIGHT == type)
            {
                onMsgLight(msg);
            } else if (LiveConstant.CustomMsgType.MSG_END_VIDEO == type)
            {
                onMsgEndVideo(msg);
            } else if (LiveConstant.CustomMsgType.MSG_DATA == type)
            {
                onMsgData(msg);
            }

            //大分类
            if (msg.isAuctionMsg())
            {
                //竞拍消息
                onMsgAuction(msg);
            }
            if (msg.isShopPushMsg())
            {
                onMsgShop(msg);
            }
            if (msg.isPayModeMsg())
            {
                //付费模式消息
                onMsgPayMode(msg);
            }
            if (msg.isGameMsg())
            {
                //游戏消息
                onMsgGame(msg);
            }
            if (msg.isLiveMsg())
            {
                //聊天列表消息
                onLiveMsgListMsg(msg);
            }
        } else
        {
            // C2C消息
            if (LiveConstant.CustomMsgType.MSG_STOP_LIVE == type)
            {
                onMsgStopLive(msg);
            } else if (LiveConstant.CustomMsgType.MSG_INVITE_VIDEO == type)
            {
                onMsgInviteVideo(msg);
            } else if (LiveConstant.CustomMsgType.MSG_ACCEPT_VIDEO == type)
            {
                onMsgAcceptVideo(msg);
            } else if (LiveConstant.CustomMsgType.MSG_REJECT_VIDEO == type)
            {
                onMsgRejectVideo(msg);
            } else if (LiveConstant.CustomMsgType.MSG_USER_STOP_VIDEO == type)
            {
                onMsgUserStopVideo(msg);
            } else if (LiveConstant.CustomMsgType.MSG_WARNING_BY_MANAGER == type)
            {
                onMsgWarningByManager(msg);
            }

            //大分类
            if (msg.isPrivateMsg())
            {
                //私聊消息
                onMsgPrivate(msg);
            }
        }
    }

    /**
     * 点亮消息
     */
    protected void onMsgLight(final MsgModel msg)
    {
        businessListener.onMsgLight(msg.getCustomMsgLight());
    }

    /**
     * 主播回来消息
     */
    protected void onMsgCreaterComeback(final MsgModel msg)
    {
        businessListener.onMsgCreaterComeback(msg.getCustomMsgCreaterComeback());
    }

    /**
     * 主播离开消息
     */
    protected void onMsgCreaterLeave(final MsgModel msg)
    {
        businessListener.onMsgCreaterLeave(msg.getCustomMsgCreaterLeave());
    }

    /**
     * 观众退出消息
     */
    protected void onMsgViewerQuit(final MsgModel msg)
    {
        businessListener.onMsgViewerQuit(msg.getCustomMsgViewerQuit());
    }

    /**
     * 观众加入消息
     */
    protected void onMsgViewerJoin(final MsgModel msg)
    {
        businessListener.onMsgViewerJoin(msg.getCustomMsgViewerJoin());
    }

    /**
     * 弹幕消息
     */
    protected void onMsgPopMsg(final MsgModel msg)
    {
        businessListener.onMsgPopMsg(msg.getCustomMsgPopMsg());
    }

    /**
     * 礼物消息
     */
    protected void onMsgGift(final MsgModel msg)
    {
        businessListener.onMsgGift(msg.getCustomMsgGift());
    }

    /**
     * 红包消息
     */
    protected void onMsgRedEnvelope(final MsgModel msg)
    {
        businessListener.onMsgRedEnvelope(msg.getCustomMsgRedEnvelope());
    }

    /**
     * 发起连麦
     */
    protected void onMsgInviteVideo(final MsgModel msg)
    {
        businessListener.onMsgInviteVideo(msg.getCustomMsgInviteVideo());
    }

    /**
     * 接受连麦
     */
    protected void onMsgAcceptVideo(final MsgModel msg)
    {
        businessListener.onMsgAcceptVideo(msg.getCustomMsgAcceptVideo());
    }

    /**
     * 拒绝连麦
     */
    protected void onMsgRejectVideo(final MsgModel msg)
    {
        businessListener.onMsgRejectVideo(msg.getCustomMsgRejectVideo());
    }

    /**
     * 用户结束连麦
     */
    protected void onMsgUserStopVideo(final MsgModel msg)
    {
        businessListener.onMsgUserStopVideo(msg.getCustomMsgUserStopVideo());
    }

    protected void onMsgWarningByManager(final MsgModel msg)
    {
        LogUtil.i(msg.getCustomMsgWarning().getDesc());
        businessListener.onMsgWarning(msg.getCustomMsgWarning());
    }

    /**
     * 直播结束
     */
    protected void onMsgEndVideo(final MsgModel msg)
    {
        businessListener.onMsgEndVideo(msg.getCustomMsgEndVideo());
    }

    /**
     * 关闭当前直播
     */
    protected void onMsgStopLive(final MsgModel msg)
    {
        businessListener.onMsgStopLive(msg.getCustomMsgStopLive());
    }

    protected void onMsgData(final MsgModel msg)
    {
        businessListener.onMsgData(msg.getCustomMsgData());
    }

    // 大分类消息

    /**
     * 私聊消息
     */
    protected void onMsgPrivate(final MsgModel msg)
    {
        businessListener.onMsgPrivate(msg);
    }

    /**
     * 竞拍消息
     */
    protected void onMsgAuction(final MsgModel msg)
    {
        businessListener.onMsgAuction(msg);
    }

    /**
     * 购物消息
     */
    protected void onMsgShop(MsgModel msg)
    {
        businessListener.onMsgShop(msg);
    }

    /**
     * 付费消息
     */
    protected void onMsgPayMode(final MsgModel msg)
    {
        businessListener.onMsgPayMode(msg);
    }

    /**
     * 游戏消息
     */
    protected void onMsgGame(final MsgModel msg)
    {
        businessListener.onMsgGame(msg);
    }

    /**
     * 直播间聊天列表消息
     */
    protected void onLiveMsgListMsg(final MsgModel msg)
    {
        businessListener.onMsgListMsg(msg);
    }

    public interface LiveMsgBusinessListener
    {
        /**
         * 红包消息
         */
        void onMsgRedEnvelope(CustomMsgRedEnvelope msg);

        /**
         * 发起连麦
         */
        void onMsgInviteVideo(CustomMsgInviteVideo msg);

        /**
         * 接受连麦
         */
        void onMsgAcceptVideo(CustomMsgAcceptVideo msg);

        /**
         * 拒绝连麦
         */
        void onMsgRejectVideo(CustomMsgRejectVideo msg);

        /**
         * 用户结束连麦
         */
        void onMsgUserStopVideo(CustomMsgUserStopVideo msg);

        /**
         * 直播结束
         */
        void onMsgEndVideo(CustomMsgEndVideo msg);

        /**
         * 关闭当前直播
         */
        void onMsgStopLive(CustomMsgStopLive msg);

        /**
         * 私聊消息
         */
        void onMsgPrivate(MsgModel msg);

        /**
         * 竞拍消息
         */
        void onMsgAuction(MsgModel msg);

        /**
         * 购物消息
         */
        void onMsgShop(MsgModel msg);

        /**
         * 付费消息
         */
        void onMsgPayMode(MsgModel msg);

        /**
         * 游戏消息
         */
        void onMsgGame(MsgModel msg);

        /**
         * 礼物消息
         */
        void onMsgGift(CustomMsgGift msg);

        /**
         * 弹幕消息
         */
        void onMsgPopMsg(CustomMsgPopMsg msg);

        /**
         * 观众加入消息
         */
        void onMsgViewerJoin(CustomMsgViewerJoin msg);

        /**
         * 观众退出消息
         */
        void onMsgViewerQuit(CustomMsgViewerQuit msg);

        /**
         * 主播离开消息
         */
        void onMsgCreaterLeave(CustomMsgCreaterLeave msg);

        /**
         * 主播回来消息
         */
        void onMsgCreaterComeback(CustomMsgCreaterComeback msg);

        /**
         * 点亮消息
         */
        void onMsgLight(CustomMsgLight msg);

        /**
         * 直播间聊天列表消息
         */
        void onMsgListMsg(MsgModel msg);

        /**
         * 直播间内警告消息
         */
        void onMsgWarning(CustomMsgWarning msg);

        /**
         * 数据消息
         *
         * @param msg
         */
        void onMsgData(CustomMsgData msg);
    }


    public static abstract class SimpleLiveMsgBusinessListener implements LiveMsgBusinessListener
    {
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
    }

}
