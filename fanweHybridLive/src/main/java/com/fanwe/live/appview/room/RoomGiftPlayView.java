package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;

import com.fanwe.auction.model.custommsg.CustomMsgAuctionOffer;
import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.LiveConstant.CustomMsgType;
import com.fanwe.live.R;
import com.fanwe.live.appview.ILiveGiftView;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.model.custommsg.CustomMsgGift;
import com.fanwe.live.model.custommsg.ILiveGiftMsg;
import com.fanwe.live.view.LiveGiftPlayView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 礼物播放
 *
 * @author Administrator
 * @date 2016-5-16 下午1:16:27
 */
public class RoomGiftPlayView extends RoomView
{
    public RoomGiftPlayView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomGiftPlayView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomGiftPlayView(Context context)
    {
        super(context);
    }

    private static final long DURATION_LOOPER = 200;

    private LiveGiftPlayView view_gift_play0;
    private LiveGiftPlayView view_gift_play1;

    private List<ILiveGiftView> listPlayView;
    private List<ILiveGiftMsg> listMsg;
    private SDLooper looper;

    private HashMap<ILiveGiftMsg, ILiveGiftMsg> mapGiftNumber;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_gift_play;
    }

    @Override
    protected void baseConstructorInit()
    {
        super.baseConstructorInit();
        view_gift_play0 = find(R.id.view_gift_play0);
        view_gift_play1 = find(R.id.view_gift_play1);

        listPlayView = new ArrayList<>();
        listMsg = new ArrayList<>();
        looper = new SDSimpleLooper();
        mapGiftNumber = new HashMap<>();

        listPlayView.add(view_gift_play0);
        listPlayView.add(view_gift_play1);
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();
        listMsg.clear();
        for (ILiveGiftView item : listPlayView)
        {
            item.stopAnimator();
        }
    }

    private void looperWork()
    {
        boolean foundMsg = false;
        for (ILiveGiftView view : listPlayView)
        {
            if (view.canPlay())
            {
                // 如果当前view满足播放条件，开始遍历msg列表，寻找可以播放的msg
                for (ILiveGiftMsg msg : listMsg)
                {
                    if (msg.isTaked())
                    {
                        continue;
                    } else
                    {
                        // 如果本条msg还没被播放过，遍历view列表，寻找是否已经有包含本条msg的view
                        ILiveGiftView currentMsgView = null;
                        for (ILiveGiftView otherView : listPlayView)
                        {
                            if (otherView.containsMsg(msg))
                            {
                                currentMsgView = otherView;
                                break;
                            }
                        }
                        if (currentMsgView != null)
                        {
                            // 如果找到包含本条msg的view
                            if (view != currentMsgView)
                            {
                                // 这个view不是自己，跳过此条消息
                                continue;
                            } else
                            {
                                // 如果这个view是自己
                                foundMsg = view.playMsg(msg);
                            }
                        } else
                        {
                            // 如果没找到包含本条msg的view
                            foundMsg = view.playMsg(msg);
                        }
                    }
                }
            }
        }

        if (!foundMsg)
        {
            boolean isAllViewFree = true;
            for (ILiveGiftView view : listPlayView)
            {
                if (view.isPlaying())
                {
                    isAllViewFree = false;
                    break;
                }
            }
            if (isAllViewFree)
            {
                looper.stop();
            }
        }
    }

    private void addMsg(ILiveGiftMsg newMsg)
    {
        if (newMsg != null)
        {
            if (!newMsg.canPlay())
            {
                return;
            }

            int showNum = newMsg.getShowNum();
            if (showNum <= 0)
            {
                if (newMsg.isPlusMode())
                {
                    if (newMsg.needPlus())
                    {
                        ILiveGiftMsg mapMsg = mapGiftNumber.get(newMsg);
                        if (mapMsg != null)
                        {
                            newMsg.setShowNum(mapMsg.getShowNum() + 1);
                            mapGiftNumber.remove(newMsg);
                        }
                    }

                    if (newMsg.getShowNum() <= 0)
                    {
                        newMsg.setShowNum(1);
                    }

                    mapGiftNumber.put(newMsg, newMsg);
                    LogUtil.i("map size:" + mapGiftNumber.size());
                }
            }

            Iterator<ILiveGiftMsg> it = listMsg.iterator();
            while (it.hasNext())
            {
                ILiveGiftMsg oldMsg = it.next();
                if (oldMsg.isTaked())
                {
                    it.remove();
                }
            }
            listMsg.add(newMsg);

            if (!looper.isRunning())
            {
                looper.start(DURATION_LOOPER, new Runnable()
                {

                    @Override
                    public void run()
                    {
                        looperWork();
                    }
                });
            }
        }
    }

    public void onEventMainThread(EImOnNewMessages event)
    {
        String peer = event.msg.getConversationPeer();
        if (event.msg.isLocalPost())
        {

        } else
        {
            if (peer.equals(getLiveInfo().getGroupId()))
            {
                if (CustomMsgType.MSG_GIFT == event.msg.getCustomMsgType())
                {
                    CustomMsgGift msg = event.msg.getCustomMsgGift();
                    addMsg(msg);
                } else if (CustomMsgType.MSG_AUCTION_OFFER == event.msg.getCustomMsgType())
                {
                    CustomMsgAuctionOffer msg = event.msg.getCustomMsgAuctionOffer();
                    addMsg(msg);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        looper.stop();
    }

}
