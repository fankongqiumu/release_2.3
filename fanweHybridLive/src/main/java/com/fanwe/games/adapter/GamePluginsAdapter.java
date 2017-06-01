package com.fanwe.games.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.games.model.PluginModel;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.utils.GlideUtil;

import java.util.List;

/**
 * 游戏插件适配器
 * @author luodong
 * @version 创建时间：2016-11-24
 */

public class GamePluginsAdapter extends SDSimpleAdapter<PluginModel> {

    private OnItemClickListener onItemClickListener;

    public GamePluginsAdapter(List<PluginModel> listModel, Activity activity) {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        return R.layout.item_game_plugins;
    }

    @Override
    public void bindData(final int position, View convertView, ViewGroup parent, final PluginModel model) {

        LinearLayout ll_content = get(R.id.ll_content, convertView);
        ImageView iv_image = get(R.id.iv_image, convertView);
        TextView tv_name = get(R.id.tv_name, convertView);
        TextView tv_selected_text = get(R.id.tv_selected_text, convertView);
        GlideUtil.load(model.getImage()).into(iv_image);
        SDViewBinder.setTextView(tv_name,model.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.clickItem(v,position,model);
                }
            }
        });

        if(model.getIs_active() == 1){
            tv_selected_text.setVisibility(View.VISIBLE);
        }else {
            tv_selected_text.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void clickItem(View view, int position, PluginModel model);
    }
}
