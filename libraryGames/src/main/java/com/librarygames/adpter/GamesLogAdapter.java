package com.librarygames.adpter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import java.util.List;

/**
 * 历史记录适配器
 *
 * @author luodong
 * @version 创建时间：2016-12-6
 */
public class GamesLogAdapter extends SDSimpleAdapter<Integer>{

    public GamesLogAdapter(List<Integer> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_games_log;
    }

    @Override
    public void bindData(final int position, View convertView, ViewGroup parent, final Integer model) {
        ImageView iv_arr1 = get(R.id.iv_arr1, convertView);
        ImageView iv_arr2 = get(R.id.iv_arr2, convertView);
        ImageView iv_arr3 = get(R.id.iv_arr3, convertView);
        ImageView iv_zuixin = get(R.id.iv_zuixin, convertView);

        SDViewBinder.setImageViewResource(iv_arr1, R.drawable.ic_fu, false);
        SDViewBinder.setImageViewResource(iv_arr2, R.drawable.ic_fu, false);
        SDViewBinder.setImageViewResource(iv_arr3, R.drawable.ic_fu, false);

        switch (model){
            case 1:
                SDViewBinder.setImageViewResource(iv_arr1, R.drawable.ic_sheng, false);
                break;
            case 2:
                SDViewBinder.setImageViewResource(iv_arr2, R.drawable.ic_sheng, false);
                break;
            case 3:
                SDViewBinder.setImageViewResource(iv_arr3, R.drawable.ic_sheng, false);
                break;
            default:
                break;
        }

        switch (position){
            case 0:
                SDViewUtil.show(iv_zuixin);
                break;
            default:
                SDViewUtil.hide(iv_zuixin);
                break;
        }
    }
}
