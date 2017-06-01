package com.fanwe.o2o.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.fanwe.hybrid.fragment.BaseFragment;
import com.fanwe.library.adapter.SDFragmentPagerAdapter;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.config.SDConfig;
import com.fanwe.library.customview.SDViewPager;
import com.fanwe.library.event.EOnClick;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.view.SDTabUnderline;
import com.fanwe.library.view.select.SDSelectViewManager;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveSearchUserActivity;
import com.fanwe.live.event.EReSelectTabLiveBottom;
import com.fanwe.live.event.ESelectLiveFinish;
import com.fanwe.live.fragment.LiveTabBaseFragment;
import com.fanwe.live.fragment.LiveTabFollowFragment;
import com.fanwe.live.fragment.LiveTabHotFragment;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class O2OShoppingLiveTabLiveFragment extends BaseFragment
{
    @ViewInject(R.id.ll_search)
    private View ll_search;

    @ViewInject(R.id.tab_live_follow)
    private SDTabUnderline tab_live_follow;
    @ViewInject(R.id.tab_live_hot)
    private SDTabUnderline tab_live_hot;

    @ViewInject(R.id.vpg_content)
    private SDViewPager vpg_content;

    private SparseArray<LiveTabBaseFragment> arrFragment = new SparseArray<>();

    private SDSelectViewManager<SDTabUnderline> selectViewManager = new SDSelectViewManager<>();

    @Override
    protected int onCreateContentView()
    {
        return R.layout.o2o_shopping_frag_live_tab_live;
    }

    @Override
    protected void init()
    {
        super.init();

        ll_search.setOnClickListener(this);

        initSDViewPager();
        initTabs();
    }

    private void initSDViewPager()
    {
        vpg_content.setOffscreenPageLimit(2);
        List<String> listModel = new ArrayList<>();
        listModel.add("");
        listModel.add("");

        vpg_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {
                if (selectViewManager.getSelectedIndex() != position)
                {
                    selectViewManager.setSelected(position, true);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
            }
        });
        vpg_content.setAdapter(new O2OShoppingLiveTabLiveFragment.LivePagerAdapter(listModel, getActivity(), getChildFragmentManager()));

    }

    private class LivePagerAdapter extends SDFragmentPagerAdapter<String>
    {

        public LivePagerAdapter(List<String> listModel, Activity activity, FragmentManager fm)
        {
            super(listModel, activity, fm);
        }

        @Override
        public Fragment getItemFragment(int position, String model)
        {
            LiveTabBaseFragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new LiveTabFollowFragment();
                    break;
                case 1:
                    fragment = new LiveTabHotFragment();
                    break;
                default:
                    break;
            }
            if (fragment != null)
            {
                arrFragment.put(position, fragment);
                fragment.setParentViewPager(vpg_content);
            }

            return fragment;
        }
    }

    public void onEventMainThread(ESelectLiveFinish event)
    {
        updateTabHotText();
    }

    private void updateTabHotText()
    {
        String text = SDConfig.getInstance().getString(R.string.config_live_select_city, "");
        if (TextUtils.isEmpty(text))
        {
            text = LiveConstant.LIVE_HOT_CITY;
        }
        tab_live_hot.setTextTitle(text);
    }

    private void initTabs()
    {
        // 关注
        String tab_follow_text = getString(R.string.live_tab_follow_text);
        tab_live_follow.setTextTitle(tab_follow_text);
        tab_live_follow.getViewConfig(tab_live_follow.mIvUnderline).setBackgroundColorNormal(Color.TRANSPARENT)
                .setBackgroundColorSelected(SDResourcesUtil.getColor(R.color.main_color));
        tab_live_follow.getViewConfig(tab_live_follow.mTvTitle).setTextColorNormal(SDResourcesUtil.getColor(R.color.text_title_bar)).setTextColorSelected(SDResourcesUtil.getColor(R.color.text_black));

        // 热门
        updateTabHotText();
        tab_live_hot.getViewConfig(tab_live_hot.mIvUnderline).setBackgroundColorNormal(Color.TRANSPARENT).setBackgroundColorSelected(Color.TRANSPARENT).setBackgroundColorSelected(SDResourcesUtil.getColor(R.color.main_color));
        tab_live_hot.getViewConfig(tab_live_hot.mTvTitle).setTextColorNormal(SDResourcesUtil.getColor(R.color.text_title_bar)).setTextColorSelected(SDResourcesUtil.getColor(R.color.text_black));

        SDTabUnderline[] items = new SDTabUnderline[]{tab_live_follow, tab_live_hot};

        selectViewManager.setListener(new SDSelectManager.SDSelectManagerListener<SDTabUnderline>()
        {

            @Override
            public void onNormal(int index, SDTabUnderline item)
            {
            }

            @Override
            public void onSelected(int index, SDTabUnderline item)
            {
                switch (index)
                {
                    case 0:
                        clickTabFollow();
                        break;
                    case 1:
                        clickTabHot();
                        break;
                    default:
                        break;
                }
            }
        });
        selectViewManager.setItems(items);
        selectViewManager.setSelected(1, true);
    }

    protected void clickTabFollow()
    {
        vpg_content.setCurrentItem(0);
    }

    protected void clickTabHot()
    {
        vpg_content.setCurrentItem(1);
    }

    @Override
    public void onClick(View v)
    {
        if (v == ll_search)
        {
            clickSearch();
        }
        super.onClick(v);
    }

    /**
     * 搜索
     */
    private void clickSearch()
    {
        Intent intent = new Intent(getActivity(), LiveSearchUserActivity.class);
        startActivity(intent);
    }

    public void onEventMainThread(EReSelectTabLiveBottom event)
    {
        if (event.index == 0)
        {
            int index = vpg_content.getCurrentItem();
            LiveTabBaseFragment fragment = arrFragment.get(index);
            if (fragment != null)
            {
                fragment.scrollToTop();
            }
        }
    }


    public void onEventMainThread(EOnClick event)
    {
        if (R.id.tv_tab_live_follow_goto_live == event.view.getId())
        {
            clickTabHot();
        }
    }
}
