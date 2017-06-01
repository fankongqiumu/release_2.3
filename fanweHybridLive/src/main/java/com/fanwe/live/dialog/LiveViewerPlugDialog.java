package com.fanwe.live.dialog;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.fanwe.library.dialog.SDDialogBase;
import com.fanwe.live.R;
import com.fanwe.live.appview.AMenuView;
import com.fanwe.live.appview.room.RoomPluginMenuView;
import com.fanwe.live.model.MenuConfig;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fanwe.live.common.AppRuntimeWorker.getIsOpenWebviewMain;
import static com.fanwe.live.common.AppRuntimeWorker.getOpen_podcast_goods;
import static com.fanwe.live.common.AppRuntimeWorker.getShopping_goods;

/**
 * Created by yhz on 2017/4/7.
 */

public class LiveViewerPlugDialog extends SDDialogBase
{
    public static final int MENU_MY_STAR_STORE = 0;
    public static final int MENU_MY_SHOP_STORE = 1;

    private LinearLayout ll_content;

    private MenuConfig star_store = new MenuConfig();
    private MenuConfig shop_store = new MenuConfig();

    private List<MenuConfig> listModel = new ArrayList<>();
    private Map<MenuConfig, RoomPluginMenuView> mapModelView = new HashMap<>();

    public LiveViewerPlugDialog(Activity activity)
    {
        super(activity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_live_viewer_plug);
        paddings(0);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        x.view().inject(this, getContentView());
        initView();
        initData();
        bindData();
    }

    private void initView()
    {
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
    }

    private void initData()
    {
        //开启购物功能
        if (getShopping_goods() == 1 || getIsOpenWebviewMain())
        {
            star_store.setTextNormal("星店");
            star_store.setImageResIdNormal(R.drawable.ic_my_star_store_normal);
            star_store.setImageResIdSelected(R.drawable.ic_my_star_store_selected);
            star_store.setType(MENU_MY_STAR_STORE);
            listModel.add(star_store);
        }

        //开启我的小店功能
        if (getOpen_podcast_goods() == 1)
        {
            shop_store.setTextNormal("小店");
            shop_store.setImageResIdNormal(R.drawable.ic_my_shop_store_normal);
            shop_store.setImageResIdSelected(R.drawable.ic_my_shop_store_selected);
            shop_store.setType(MENU_MY_SHOP_STORE);
            listModel.add(shop_store);
        }
    }

    private void bindData()
    {
        ll_content.removeAllViews();
        mapModelView.clear();
        for (final MenuConfig item : listModel)
        {
            final RoomPluginMenuView view = new RoomPluginMenuView(getContext());
            view.setMenuConfig(item);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null)
                    {
                        switch (item.getType())
                        {
                            case MENU_MY_STAR_STORE:
                                clickListener.onClickStarStore(view);
                                break;
                            case MENU_MY_SHOP_STORE:
                                clickListener.onClickShopStore(view);
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
            mapModelView.put(item, view);
            ll_content.addView(view);
        }
    }

    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public interface ClickListener
    {
        /**
         * 我的星店
         */
        void onClickStarStore(AMenuView view);

        /**
         * 我的小店
         */
        void onClickShopStore(AMenuView view);
    }
}
