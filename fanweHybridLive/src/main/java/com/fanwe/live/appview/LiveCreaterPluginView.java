package com.fanwe.live.appview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.fanwe.live.R;
import com.fanwe.live.appview.room.RoomPluginMenuView;
import com.fanwe.live.model.MenuConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主播基础插件菜单view
 */
public class LiveCreaterPluginView extends BaseAppView
{

    public static final int MENU_BEAUTY = 0;
    public static final int MENU_SWITCH_CAMERA = 1;
    public static final int MENU_MIC = 2;
    public static final int MENU_FLASH_LIGHT = 3;
    public static final int MENU_MUSIC = 4;

    private LinearLayout ll_base_plugins;

    private MenuConfig modelBeauty = new MenuConfig();
    private MenuConfig modelMusic = new MenuConfig();
    private MenuConfig modelSwitchCamera = new MenuConfig();
    private MenuConfig modelMic = new MenuConfig();
    private MenuConfig modelFlashLight = new MenuConfig();

    private List<MenuConfig> listModel = new ArrayList<>();
    private Map<MenuConfig, RoomPluginMenuView> mapModelView = new HashMap<>();
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public LiveCreaterPluginView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LiveCreaterPluginView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LiveCreaterPluginView(Context context)
    {
        super(context);
        init();
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_live_creater_plugin);
        ll_base_plugins = find(R.id.ll_base_plugins);

        initData();
        bindData();
    }

    private void initData()
    {
        modelBeauty.setTextNormal("美颜");
        modelBeauty.setImageResIdNormal(R.drawable.ic_plugins_beauty_off);
        modelBeauty.setImageResIdSelected(R.drawable.ic_plugins_beauty_on);
        modelBeauty.setType(MENU_BEAUTY);

        modelMusic.setTextNormal("音乐");
        modelMusic.setImageResIdNormal(R.drawable.selector_plugin_music);
        modelMusic.setType(MENU_MUSIC);

        modelSwitchCamera.setTextNormal("切换");
        modelSwitchCamera.setImageResIdNormal(R.drawable.selector_plugin_switch_camera);
        modelSwitchCamera.setType(MENU_SWITCH_CAMERA);

        modelMic.setTextNormal("麦克风");
        modelMic.setImageResIdNormal(R.drawable.ic_plugins_macrophone_off);
        modelMic.setImageResIdSelected(R.drawable.ic_plugins_macrophone_on);
        modelMic.setSelected(true);
        modelMic.setType(MENU_MIC);

        modelFlashLight.setTextNormal("闪光灯");
        modelFlashLight.setImageResIdNormal(R.drawable.ic_plugins_flashlight_off);
        modelFlashLight.setImageResIdSelected(R.drawable.ic_plugins_flashlight_on);
        modelFlashLight.setType(MENU_FLASH_LIGHT);

        listModel.add(modelMusic);
        listModel.add(modelBeauty);
        listModel.add(modelMic);
        listModel.add(modelSwitchCamera);
        listModel.add(modelFlashLight);
    }

    private void bindData()
    {
        ll_base_plugins.removeAllViews();
        mapModelView.clear();
        for (final MenuConfig item : listModel)
        {
            final RoomPluginMenuView view = new RoomPluginMenuView(getContext());
            view.setMenuConfig(item);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null)
                    {
                        switch (item.getType())
                        {
                            case MENU_BEAUTY:
                                clickListener.onClickMenuBeauty(view);
                                break;
                            case MENU_MUSIC:
                                clickListener.onClickMenuMusic(view);
                                break;
                            case MENU_SWITCH_CAMERA:
                                clickListener.onClickMenuSwitchCamera(view);
                                break;
                            case MENU_MIC:
                                clickListener.onClickMenuMic(view);
                                break;
                            case MENU_FLASH_LIGHT:
                                clickListener.onClickMenuFlashLight(view);
                                break;

                            default:
                                break;
                        }
                    }
                }
            });
            mapModelView.put(item, view);
            ll_base_plugins.addView(view);
        }
    }

    public void dealFlashLightState(boolean isBackCamera)
    {
        if (isBackCamera)
        {
        } else
        {
            setModelFlashLightState(false);
        }
    }

    public void setModelFlashLightState(boolean isSelected)
    {
        modelFlashLight.setSelected(isSelected);
        updateState(modelFlashLight);
    }

    public void updateState(MenuConfig model)
    {
        RoomPluginMenuView view = mapModelView.get(model);
        if (view != null)
        {
            view.bindData();
        }
    }

    public interface ClickListener
    {
        /**
         * 美颜
         */
        void onClickMenuBeauty(AMenuView view);

        /**
         * 切换摄像头
         */
        void onClickMenuSwitchCamera(AMenuView view);

        /**
         * 麦克风
         */
        void onClickMenuMic(AMenuView view);

        /**
         * 闪关灯
         */
        void onClickMenuFlashLight(AMenuView view);

        /**
         * 音乐
         */
        void onClickMenuMusic(AMenuView view);
    }
}
