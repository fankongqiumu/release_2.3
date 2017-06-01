package com.fanwe.live.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveSetBeautyFilterAdapter;
import com.fanwe.live.control.LivePushSDK;
import com.fanwe.live.model.LiveBeautyConfig;
import com.fanwe.live.model.LiveBeautyFilterModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 美颜设置窗口
 */
public class LiveSetBeautyDialog extends LiveBaseDialog
{

    private View ll_beauty;
    private SeekBar sb_beauty;
    private TextView tv_beauty_percent;

    private View ll_white;
    private SeekBar sb_white;
    private TextView tv_white_percent;

    private SDGridLinearLayout ll_filter;
    private LiveSetBeautyFilterAdapter filterAdapter;

    private OnSeekBarChangeListener onBeautySeekBarChangeListener;
    private OnSeekBarChangeListener onWhiteSeekBarChangeListener;

    public LiveSetBeautyDialog(Activity activity)
    {
        super(activity);
        init();
    }

    /**
     * 设置美颜百分比监听
     *
     * @param onBeautySeekBarChangeListener
     */
    public void setOnBeautySeekBarChangeListener(OnSeekBarChangeListener onBeautySeekBarChangeListener)
    {
        this.onBeautySeekBarChangeListener = onBeautySeekBarChangeListener;
    }

    /**
     * 设置美白百分比监听
     *
     * @param onWhiteSeekBarChangeListener
     */
    public void setOnWhiteSeekBarChangeListener(OnSeekBarChangeListener onWhiteSeekBarChangeListener)
    {
        this.onWhiteSeekBarChangeListener = onWhiteSeekBarChangeListener;
    }

    /**
     * 设置美颜百分比0-100
     *
     * @param progress
     */
    public void setBeautyProgress(int progress)
    {
        if (sb_beauty != null)
        {
            sb_beauty.setProgress(progress);
            updateBeautyPercent(progress);
        }
    }

    /**
     * 设置美白百分比
     *
     * @param progress
     */
    public void setWhiteProgress(int progress)
    {
        if (sb_white != null)
        {
            sb_white.setProgress(progress);
            updateWhitePercent(progress);
        }
    }

    private void init()
    {
        setContentView(R.layout.dialog_live_set_beauty);
        setCanceledOnTouchOutside(true);
        paddings(0);

        ll_beauty = getContentView().findViewById(R.id.ll_beauty);
        sb_beauty = (SeekBar) getContentView().findViewById(R.id.sb_beauty);
        tv_beauty_percent = (TextView) getContentView().findViewById(R.id.tv_beauty_percent);

        ll_white = getContentView().findViewById(R.id.ll_white);
        sb_white = (SeekBar) getContentView().findViewById(R.id.sb_white);
        tv_white_percent = (TextView) getContentView().findViewById(R.id.tv_white_percent);

        ll_filter = (SDGridLinearLayout) findViewById(R.id.ll_filter);

        float widthPercent = SDViewUtil.measureText(tv_beauty_percent, "100%");
        SDViewUtil.setViewWidth(tv_beauty_percent, (int) widthPercent);
        SDViewUtil.setViewWidth(tv_white_percent, (int) widthPercent);

        sb_beauty.setMax(100);
        sb_beauty.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (onBeautySeekBarChangeListener != null)
                {
                    onBeautySeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                if (onBeautySeekBarChangeListener != null)
                {
                    onBeautySeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (onBeautySeekBarChangeListener != null)
                {
                    onBeautySeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                updateBeautyPercent(progress);
            }
        });

        sb_white.setMax(100);
        sb_white.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                if (onWhiteSeekBarChangeListener != null)
                {
                    onWhiteSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                if (onWhiteSeekBarChangeListener != null)
                {
                    onWhiteSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (onWhiteSeekBarChangeListener != null)
                {
                    onWhiteSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                }
                updateWhitePercent(progress);
            }
        });

        initBeautyFilter();
    }

    private void initBeautyFilter()
    {
        ll_filter.setColNumber(3);

        List<LiveBeautyFilterModel> listFilter = new ArrayList<>();

        LiveBeautyFilterModel none = new LiveBeautyFilterModel();
        none.setName("无滤镜");

        LiveBeautyFilterModel langman = new LiveBeautyFilterModel();
        langman.setName("浪漫");
        langman.setFilterResId(R.drawable.live_filter_langman);

        LiveBeautyFilterModel qingxin = new LiveBeautyFilterModel();
        qingxin.setName("清新");
        qingxin.setFilterResId(R.drawable.live_filter_qingxin);

        LiveBeautyFilterModel weimei = new LiveBeautyFilterModel();
        weimei.setName("唯美");
        weimei.setFilterResId(R.drawable.live_filter_weimei);

        LiveBeautyFilterModel fennen = new LiveBeautyFilterModel();
        fennen.setName("粉嫩");
        fennen.setFilterResId(R.drawable.live_filter_fennen);

        LiveBeautyFilterModel huaijiu = new LiveBeautyFilterModel();
        huaijiu.setName("怀旧");
        huaijiu.setFilterResId(R.drawable.live_filter_huaijiu);

        LiveBeautyFilterModel landiao = new LiveBeautyFilterModel();
        landiao.setName("蓝调");
        landiao.setFilterResId(R.drawable.live_filter_landiao);

        LiveBeautyFilterModel qingliang = new LiveBeautyFilterModel();
        qingliang.setName("清凉");
        qingliang.setFilterResId(R.drawable.live_filter_qingliang);

        LiveBeautyFilterModel rixi = new LiveBeautyFilterModel();
        rixi.setName("日系");
        rixi.setFilterResId(R.drawable.live_filter_rixi);

        listFilter.add(none);
        listFilter.add(langman);
        listFilter.add(qingxin);
        listFilter.add(weimei);
        listFilter.add(fennen);
        listFilter.add(huaijiu);
        listFilter.add(landiao);
        listFilter.add(qingliang);
        listFilter.add(rixi);

        filterAdapter = new LiveSetBeautyFilterAdapter(listFilter, getOwnerActivity());
        filterAdapter.setItemClickListener(new SDAdapter.ItemClickListener<LiveBeautyFilterModel>()
        {
            @Override
            public void onClick(int position, LiveBeautyFilterModel item, View view)
            {
                int filterResId = item.getFilterResId();
                LivePushSDK.getInstance().setBeautyFilter(filterResId);
                LiveBeautyConfig.get().setFilterResId(filterResId).save();
            }
        });
        ll_filter.setAdapter(filterAdapter);

        int savedFilter = LiveBeautyConfig.get().getFilterResId();
        int index = 0;
        for (LiveBeautyFilterModel item : listFilter)
        {
            if (item.getFilterResId() == savedFilter)
            {
                break;
            }
            index++;
        }
        filterAdapter.getSelectManager().setSelected(index, true);
    }

    private void updateBeautyPercent(int percent)
    {
        tv_beauty_percent.setText(String.valueOf(percent + "%"));
    }

    private void updateWhitePercent(int percent)
    {
        tv_white_percent.setText(String.valueOf(percent + "%"));
    }

}
