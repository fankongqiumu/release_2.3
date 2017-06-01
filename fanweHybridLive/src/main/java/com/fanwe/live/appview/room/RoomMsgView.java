package com.fanwe.live.appview.room;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveMsgRecyclerAdapter;
import com.fanwe.live.event.ERequestFollowSuccess;
import com.fanwe.live.model.custommsg.CustomMsgLight;
import com.fanwe.live.model.custommsg.CustomMsgLiveMsg;
import com.fanwe.live.model.custommsg.MsgModel;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 聊天消息
 */
public class RoomMsgView extends RoomLooperMainView<MsgModel>
{

    public RoomMsgView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public RoomMsgView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RoomMsgView(Context context)
    {
        super(context);
        init();
    }

    /**
     * 列表显示的最大消息数量
     */
    private static final int MAX_MSG_NUMBER = 200;
    /**
     * 刷新每一页的最大消息数量
     */
    private static final int PAGE_MAX_SIZE = 20;
    /**
     * 刷新每一页消息的间隔
     */
    private static final int PAGE_LOOP_TIME = 500;

    private RecyclerView lv_content;
    private LiveMsgRecyclerAdapter adapter;
    private List<MsgModel> listPage = new ArrayList<>();


    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_msg;
    }

    @Override
    protected void init()
    {
        super.init();
        lv_content = find(R.id.lv_content);

        lv_content.setItemAnimator(null);
        adapter = new LiveMsgRecyclerAdapter(getActivity());
        lv_content.setLayoutManager(new LinearLayoutManager(getContext()));
        lv_content.setAdapter(adapter);

        initLiveMsg();
    }

    protected void initLiveMsg()
    {
        InitActModel model = InitActModelDao.query();
        if (model != null)
        {
            List<CustomMsgLiveMsg> listMsg = model.getListmsg();
            if (!SDCollectionUtil.isEmpty(listMsg))
            {
                for (CustomMsgLiveMsg msg : listMsg)
                {
                    MsgModel msgModel = msg.parseToMsgModel();
                    if (msgModel != null)
                    {
                        adapter.getData().add(msgModel);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();
        queue.clear();
        adapter.clearData();
    }

    public void onEventMainThread(ERequestFollowSuccess event)
    {
        if (event.userId.equals(getLiveInfo().getCreaterId()))
        {
            if (event.actModel.getHas_focus() == 1)
            {
                final CustomMsgLiveMsg msg = new CustomMsgLiveMsg();
                msg.setDesc(msg.getSender().getNick_name() + " 关注了主播");
                IMHelper.sendMsgGroup(getLiveInfo().getGroupId(), msg, new TIMValueCallBack<TIMMessage>()
                {
                    @Override
                    public void onError(int i, String s)
                    {
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage)
                    {
                        IMHelper.postMsgLocal(msg, getLiveInfo().getGroupId());
                    }
                });
            }
        }
    }

    @Override
    protected void startLooper(long period)
    {
        super.startLooper(PAGE_LOOP_TIME);
    }

    @Override
    protected void looperWork(LinkedList<MsgModel> queue)
    {
        int size = queue.size();
        LogUtil.i("queue:" + size);
        if (size > 0)
        {
            int loopCount = size;
            if (size > PAGE_MAX_SIZE)
            {
                loopCount = PAGE_MAX_SIZE;
            }

            listPage.clear();
            for (int i = 0; i < loopCount; i++)
            {
                listPage.add(queue.poll());
            }
            LogUtil.i("-------------take:" + listPage.size());
            adapter.appendData(listPage);
            removeBeyondModel();
            dealScrollToBottom();
        }
    }

    @Override
    protected void afterLooperWork()
    {

    }

    @Override
    public void onMsgListMsg(MsgModel msg)
    {
        super.onMsgListMsg(msg);
        if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_LIGHT)
        {
            CustomMsgLight msgLight = msg.getCustomMsgLight();
            if (msgLight.getShowMsg() == 0)
            {
                return;
            }
        }
        addModel(msg);
    }

    private void addModel(MsgModel model)
    {
        offerModel(model);
    }

    private void dealScrollToBottom()
    {
        if (lv_content.getScrollState() == RecyclerView.SCROLL_STATE_IDLE)
        {
            lv_content.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void removeBeyondModel()
    {
        int size = adapter.getItemCount();
        int overSize = size - MAX_MSG_NUMBER;
        if (overSize > 0)
        {
            for (int i = 0; i < overSize; i++)
            {
                adapter.removeData(0);
            }
        }
    }
}
