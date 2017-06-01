package com.fanwe.live.fragment;

import com.fanwe.hybrid.fragment.BaseFragment;
import com.fanwe.library.customview.SDViewPager;
import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.model.custommsg.CustomMsgLiveStopped;
import com.fanwe.live.model.custommsg.MsgModel;

/**
 * Created by Administrator on 2016/7/5.
 */
public class LiveTabBaseFragment extends BaseFragment
{
    public static final long DURATION_LOOP = 20 * 1000;

    private SDLooper looper;
    private SDViewPager parentViewPager;

    public void setParentViewPager(SDViewPager parentViewPager)
    {
        this.parentViewPager = parentViewPager;
    }

    public SDViewPager getParentViewPager()
    {
        return parentViewPager;
    }

    public SDLooper getLooper()
    {
        if (looper == null)
        {
            looper = new SDSimpleLooper();
        }
        return looper;
    }

    public void scrollToTop()
    {

    }

    public void onEventMainThread(EImOnNewMessages event)
    {
        try
        {
            if (event.msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_LIVE_STOPPED)
            {
                onMsgLiveStopped(event.msg);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void onMsgLiveStopped(MsgModel msgModel)
    {
        CustomMsgLiveStopped customMsg = msgModel.getCustomMsgLiveStopped();
        if (customMsg != null)
        {
            int roomId = customMsg.getRoom_id();
            onRoomClosed(roomId);
        }
    }

    protected void onRoomClosed(int roomId)
    {

    }

    /**
     * 开始定时执行任务，每隔一段时间执行一下onLoopRun()方法
     */
    protected void startLoopRunnable()
    {
        getLooper().start(DURATION_LOOP, loopRunnable);
    }

    /**
     * 停止定时任务
     */
    private void stopLoopRunnable()
    {
        getLooper().stop();
    }

    private Runnable loopRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            LogUtil.i(getClass().getName() + ":onLoopRun");
            onLoopRun();
        }
    };

    /**
     * 定时执行任务
     */
    protected void onLoopRun()
    {

    }

    @Override
    public void onStop()
    {
        super.onStop();
        stopLoopRunnable();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopLoopRunnable();
    }
}
