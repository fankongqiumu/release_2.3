package com.fanwe.live.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.config.SDConfig;
import com.fanwe.library.customview.SDViewPager;
import com.fanwe.library.model.SDTaskRunnable;
import com.fanwe.library.model.ViewRecter;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveTabHotAdapter;
import com.fanwe.live.appview.LiveTabHotHeaderView;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dialog.LiveSelectLiveDialog;
import com.fanwe.live.event.EReSelectTabLive;
import com.fanwe.live.event.ESelectLiveFinish;
import com.fanwe.live.model.Index_indexActModel;
import com.fanwe.live.model.LiveBannerModel;
import com.fanwe.live.model.LiveRoomModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 热门直播列表
 *
 * @author Administrator
 * @date 2016-7-2 上午11:28:04
 */
public class LiveTabHotFragment extends LiveTabBaseFragment
{

    /**
     * 话题的id(int)
     */
    public static final String EXTRA_TOPIC_ID = "extra_topic_id";

    @ViewInject(R.id.lv_content)
    protected PullToRefreshListView lv_content;

    protected LiveTabHotHeaderView headerView;

    protected List<LiveRoomModel> listModel = new ArrayList<>();
    protected LiveTabHotAdapter adapter;

    protected int topicId;
    protected int sex;
    protected String city;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_tab_hot;
    }

    @Override
    protected void init()
    {
        super.init();

        topicId = getArguments().getInt(EXTRA_TOPIC_ID);

        addHeaderView();
        setAdapter();
        updateParams();
        initPullToRefresh();
    }

    protected void setAdapter()
    {
        adapter = new LiveTabHotAdapter(listModel, getActivity());
        lv_content.setAdapter(adapter);
    }

    protected void initPullToRefresh()
    {
        lv_content.setMode(Mode.PULL_FROM_START);
        lv_content.setOnRefreshListener(new OnRefreshListener2<ListView>()
        {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                startLoopRunnable();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
            }
        });
        startLoopRunnable();
    }

    protected void addHeaderView()
    {
        headerView = new LiveTabHotHeaderView(getActivity());
        headerView.setBannerClickListener(new SDAdapter.ItemClickListener<LiveBannerModel>()
        {
            @Override
            public void onClick(int position, LiveBannerModel item, View view)
            {
                Intent intent = item.parseType(getActivity());
                if (intent != null)
                {
                    startActivity(intent);
                }
            }
        });
        SDViewPager viewPager = getParentViewPager();
        if (viewPager != null)
        {
            viewPager.addIgnoreRecter(new ViewRecter(headerView.getSlidingPlayView()));
        }
        lv_content.getRefreshableView().addHeaderView(headerView);
    }

    protected void updateParams()
    {
        sex = SDConfig.getInstance().getInt(R.string.config_live_select_sex, 0);
        city = SDConfig.getInstance().getString(R.string.config_live_select_city, "");
    }

    @Override
    public void onResume()
    {
        startLoopRunnable();
        super.onResume();
    }

    public void onEventMainThread(EReSelectTabLive event)
    {
        if (event.index == 1)
        {
            final LiveSelectLiveDialog selectDialog = new LiveSelectLiveDialog(getActivity());
            selectDialog.showBottom();
        }
    }

    public void onEventMainThread(ESelectLiveFinish event)
    {
        updateParams();
        startLoopRunnable();
    }

    @Override
    protected void onLoopRun()
    {
        requestData();
    }

    protected void requestData()
    {
        CommonInterface.requestIndex(1, sex, topicId, city, new AppRequestCallback<Index_indexActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    headerView.setListLiveBannerModel(actModel.getBanner());
                    headerView.setTopicInfoModel(actModel.getCate());

                    synchronized (LiveTabHotFragment.this)
                    {
                        listModel = actModel.getList();
                        adapter.updateData(listModel);
                    }
                }
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                lv_content.onRefreshComplete();
                super.onFinish(resp);
            }
        });
    }

    @Override
    public void scrollToTop()
    {
        lv_content.getRefreshableView().setSelection(0);
    }

    @Override
    protected void onRoomClosed(final int roomId)
    {
        super.onRoomClosed(roomId);
        SDHandlerManager.getBackgroundHandler().post(new SDTaskRunnable<LiveRoomModel>()
        {
            @Override
            public LiveRoomModel onBackground()
            {
                synchronized (LiveTabHotFragment.this)
                {
                    if (SDCollectionUtil.isEmpty(listModel))
                    {
                        return null;
                    }
                    Iterator<LiveRoomModel> it = listModel.iterator();
                    while (it.hasNext())
                    {
                        LiveRoomModel item = it.next();
                        if (roomId == item.getRoom_id())
                        {
                            return item;
                        }
                    }
                }
                return null;
            }

            @Override
            public void onMainThread(LiveRoomModel result)
            {
                if (result != null)
                {
                    synchronized (LiveTabHotFragment.this)
                    {
                        adapter.removeData(result);
                    }
                }
            }
        });
    }
}
