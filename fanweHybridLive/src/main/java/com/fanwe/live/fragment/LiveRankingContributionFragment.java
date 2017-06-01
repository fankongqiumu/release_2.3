package com.fanwe.live.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

import com.fanwe.library.adapter.SDFragmentPagerAdapter;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.customview.SDViewPager;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.view.SDTabUnderline;
import com.fanwe.library.view.select.SDSelectViewManager;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveRankingActivity;
import com.fanwe.live.event.EReSelectTabLive;
import com.sunday.eventbus.SDEventManager;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 贡献排行榜
 *
 * @author luodong
 * @date 2016-10-10
 */
public class LiveRankingContributionFragment extends LiveRankingBaseFragment
{

    @ViewInject(R.id.tab_rank_day)
    private SDTabUnderline tab_rank_day;
    @ViewInject(R.id.tab_rank_month)
    private SDTabUnderline tab_rank_month;
    @ViewInject(R.id.tab_rank_total)
    private SDTabUnderline tab_rank_total;

    @ViewInject(R.id.vpg_content)
    private SDViewPager vpg_content;

    private SparseArray<LiveRankingListBaseFragment> arrFragment = new SparseArray<>();

    private SDSelectViewManager<SDTabUnderline> selectViewManager = new SDSelectViewManager<>();

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_ranking;
    }

    @Override
    protected void init() {
        super.init();
        initSDViewPager();
        initTabs();
        setSelectTags();
    }

    private void setSelectTags() {
        //判断选取贡献排行榜类型
        if (getRankingType()!=null){
            switch (getRankingType()){
                case LiveRankingActivity.EXTRA_CONTRIBUTION_TOTAL:
                    vpg_content.setCurrentItem(2);
                    break;
                default:
                    break;
            }
        }
    }

    private void initSDViewPager()
    {
        vpg_content.setOffscreenPageLimit(2);
        List<String> listModel = new ArrayList<>();
        listModel.add("");
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
        vpg_content.setAdapter(new LivePagerAdapter(listModel, getActivity(), getChildFragmentManager()));

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
            LiveRankingListContributionFragment fragment = new LiveRankingListContributionFragment();
            switch (position)
            {
                case 0:
                    fragment.setRankName(LiveRankingListContributionFragment.RANKING_NAME_DAY);
                    break;
                case 1:
                    fragment.setRankName(LiveRankingListContributionFragment.RANKING_NAME_MONTH);
                    break;
                case 2:
                    fragment.setRankName(LiveRankingListContributionFragment.RANKING_NAME_ALL);
                    break;
                default:
                    break;
            }
            arrFragment.put(position, fragment);
            return fragment;
        }
    }

    private void initTabs()
    {
        tab_rank_day.setTextTitle("日榜");
        tab_rank_day.getViewConfig(tab_rank_day.mIvUnderline).setBackgroundColorNormal(Color.TRANSPARENT)
                .setBackgroundColorSelected(Color.TRANSPARENT);
        tab_rank_day.getViewConfig(tab_rank_day.mTvTitle).setTextColorNormal(SDResourcesUtil.getColor(R.color.text_content)).setTextColorSelected(SDResourcesUtil.getColor(R.color.main_color))
                .setTextSizeNormal(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17)).setTextSizeSelected(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17));

        tab_rank_month.setTextTitle("月榜");
        tab_rank_month.getViewConfig(tab_rank_month.mIvUnderline).setBackgroundColorNormal(Color.TRANSPARENT).setBackgroundColorSelected(Color.TRANSPARENT);
        tab_rank_month.getViewConfig(tab_rank_month.mTvTitle).setTextColorNormal(SDResourcesUtil.getColor(R.color.text_content)).setTextColorSelected(SDResourcesUtil.getColor(R.color.main_color))
                .setTextSizeNormal(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17)).setTextSizeSelected(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17));

        tab_rank_total.setTextTitle("总榜");
        tab_rank_total.getViewConfig(tab_rank_total.mIvUnderline).setBackgroundColorNormal(Color.TRANSPARENT).setBackgroundColorSelected(Color.TRANSPARENT);
        tab_rank_total.getViewConfig(tab_rank_total.mTvTitle).setTextColorNormal(SDResourcesUtil.getColor(R.color.text_content)).setTextColorSelected(SDResourcesUtil.getColor(R.color.main_color))
                .setTextSizeNormal(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17)).setTextSizeSelected(SDResourcesUtil.getDimensionPixelSize(R.dimen.base_textsize_17));

        SDTabUnderline[] items = new SDTabUnderline[]{tab_rank_day, tab_rank_month,tab_rank_total};

        selectViewManager.setReSelectListener(new SDSelectManager.ReSelectListener<SDTabUnderline>()
        {
            @Override
            public void onSelected(int index, SDTabUnderline item)
            {
                EReSelectTabLive event = new EReSelectTabLive();
                event.index = index;
                SDEventManager.post(event);
            }
        });

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
                        clickTabDay();
                        break;
                    case 1:
                        clickTabMonth();
                        break;
                    case 2:
                        clickTabTotal();
                        break;

                    default:
                        break;
                }
            }
        });
        selectViewManager.setItems(items);
        selectViewManager.setSelected(0, true);

    }

    protected void clickTabDay()
    {
        vpg_content.setCurrentItem(0);
    }

    protected void clickTabMonth()
    {
        vpg_content.setCurrentItem(1);
    }

    private void clickTabTotal() {
        vpg_content.setCurrentItem(2);
    }

}
