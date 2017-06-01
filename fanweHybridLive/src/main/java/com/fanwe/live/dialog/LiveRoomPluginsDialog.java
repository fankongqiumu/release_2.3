package com.fanwe.live.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.fanwe.games.adapter.GamePluginsAdapter;
import com.fanwe.games.model.App_InitGamesActModel;
import com.fanwe.games.model.PluginModel;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.dialog.SDDialogBase;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.appview.LiveCreaterPluginView;
import com.fanwe.live.common.CommonInterface;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shibx on 2016/11/23.
 */

public class LiveRoomPluginsDialog extends SDDialogBase {

    private GridView gv_content;
    private LiveCreaterPluginView view_plugin;
    private LinearLayout ll_plugins;
    private GamePluginsAdapter adapter;
    private List<PluginModel> listModel;
    private OnItemClickListener onItemClickListener;

    public LiveRoomPluginsDialog(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_room_plugins);
        paddings(0);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        x.view().inject(this, getContentView());
        gv_content = (GridView) findViewById(R.id.gv_content);
        ll_plugins = (LinearLayout) findViewById(R.id.ll_plugins);
        view_plugin = (LiveCreaterPluginView) findViewById(R.id.view_plugin);
        setAdapter();
    }

    public LiveCreaterPluginView getBasePlugin()
    {
        return view_plugin;
    }

    private void setAdapter() {
        listModel = new ArrayList<>();
        adapter = new GamePluginsAdapter(listModel, getOwnerActivity());
        adapter.setOnItemClickListener(new GamePluginsAdapter.OnItemClickListener() {
            @Override
            public void clickItem(View view, int position, PluginModel model) {
                if(onItemClickListener!=null){
                    onItemClickListener.clickItem(view,position,model);
                }
                adapter.getSelectManager().setSelected(position, true);
                dismiss();
            }
        });
        gv_content.setAdapter(adapter);

    }

    private void requestInitGames(final boolean isLoadMore) {
        CommonInterface.requestInitPlugins(new AppRequestCallback<App_InitGamesActModel>() {
            @Override
            protected void onSuccess(SDResponse resp) {
                if (actModel.isOk()) {
                    if (Integer.valueOf(actModel.getRs_count()) > 0) {
                        ll_plugins.setVisibility(View.VISIBLE);
                    } else {
                        ll_plugins.setVisibility(View.GONE);
                        return;
                    }
                    SDViewUtil.updateAdapterByList(listModel, actModel.getList(), adapter, isLoadMore);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void clickItem(View view, int position, PluginModel model);
    }

    @Override
    public void show()
    {
        super.show();
        requestInitGames(false);
    }
}
