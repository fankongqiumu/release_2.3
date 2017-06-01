package com.fanwe.hybrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.event.EExitApp;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.hybrid.umeng.UmengPushManager;
import com.fanwe.library.activity.SDBaseActivity;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDOtherUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.utils.LiveVideoChecker;
import com.ta.util.netstate.TANetChangeObserver;
import com.ta.util.netstate.TANetWorkUtil.netType;
import com.ta.util.netstate.TANetworkStateReceiver;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

public class BaseActivity extends SDBaseActivity implements TANetChangeObserver
{
    /**
     * 触摸返回键是否退出App
     */
    protected boolean mIsExitApp = false;
    protected long mExitTime = 0;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        UmengPushManager.getPushAgent().onAppStart();
        TANetworkStateReceiver.registerObserver(this);

        if (App.getApplication().isPushStartActivity(getClass()))
        {
            App.getApplication().startPushRunnable();
        }
    }

    @Override
    public void setContentView(View view)
    {
        super.setContentView(view);
        x.view().inject(this);
    }

    public void exitApp()
    {
        if (System.currentTimeMillis() - mExitTime > 2000)
        {
            SDToast.showToast("再按一次退出!");
        } else
        {
            App.getApplication().exitApp(true);
        }
        mExitTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onBackground()
    {
        CommonInterface.requestStateChangeLogout(new AppRequestCallback<BaseActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    LogUtil.i("requestStateChangeLogout");
                }
            }
        });
        super.onBackground();
    }

    @Override
    protected void onResumeFromBackground()
    {
        CommonInterface.requestStateChangeLogin(new AppRequestCallback<BaseActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    LogUtil.i("requestStateChangeLogin");
                }
            }
        });
        if (getClass() != InitActivity.class)
        {
            checkVideo();
        }
        super.onResumeFromBackground();
    }

    protected void checkVideo()
    {
        LiveVideoChecker checker = new LiveVideoChecker(this);
        CharSequence copyContent = SDOtherUtil.pasteText();
        checker.check(String.valueOf(copyContent));
    }

    @Override
    public void onBackPressed()
    {
        if (mIsExitApp)
        {
            exitApp();
        } else
        {
            super.onBackPressed();
        }
    }

    public void onEventMainThread(EExitApp event)
    {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getApplicationContext()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnect(netType type)
    {

    }

    @Override
    public void onDisConnect()
    {

    }

    @Override
    protected void onDestroy()
    {
        TANetworkStateReceiver.removeRegisterObserver(this);
        super.onDestroy();
    }
}
