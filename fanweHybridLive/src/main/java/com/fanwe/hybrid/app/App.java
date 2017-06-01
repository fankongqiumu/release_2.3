package com.fanwe.hybrid.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.fanwe.hybrid.activity.MainActivity;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.event.EExitApp;
import com.fanwe.hybrid.event.EJsLogout;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.map.tencent.SDTencentMapManager;
import com.fanwe.hybrid.push.PushRunnable;
import com.fanwe.hybrid.umeng.UmengPushManager;
import com.fanwe.hybrid.utils.RetryInitWorker;
import com.fanwe.library.SDLibrary;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.media.recorder.SDMediaRecorder;
import com.fanwe.library.receiver.SDHeadsetPlugReceiver;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDObjectCache;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.live.DebugHelper;
import com.fanwe.live.LiveIniter;
import com.fanwe.live.activity.LiveLoginActivity;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.event.EOnCallStateChanged;
import com.fanwe.live.event.EUserLoginSuccess;
import com.fanwe.live.event.EUserLogout;
import com.fanwe.live.model.App_userinfoActModel;
import com.fanwe.live.utils.StorageFileUtils;
import com.squareup.leakcanary.LeakCanary;
import com.sunday.eventbus.SDEventManager;
import com.ta.util.netstate.TANetChangeObserver;
import com.ta.util.netstate.TANetWorkUtil.netType;
import com.ta.util.netstate.TANetworkStateReceiver;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.xutils.x;

import de.greenrobot.event.SubscriberExceptionEvent;

public class App extends Application implements TANetChangeObserver
{
    private static App instance;
    private PushRunnable pushRunnable;

    public static App getApplication()
    {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
        init();
    }

    private void init()
    {
        final App app = this;

        new Runnable()
        {
            @Override
            public void run()
            {
                if (SDPackageUtil.isMainProcess(app))
                {
                    // 主进程
                    LeakCanary.install(app);
                    MobclickAgent.setCatchUncaughtExceptions(false);
                    SDObjectCache.put(UserModelDao.query());
                    UMShareAPI.get(app);
                    SDEventManager.register(app);
                    TANetworkStateReceiver.registerNetworkStateReceiver(app);
                    TANetworkStateReceiver.registerObserver(app);
                    SDHeadsetPlugReceiver.registerReceiver(app);
                    x.Ext.init(app);
                    SDLibrary.getInstance().init(app);
                    SDTencentMapManager.getInstance().init(app);
                    new LiveIniter().init(app);
                    initSystemListener();
                    SDMediaRecorder.getInstance().init(app);
                    LogUtil.isDebug = ApkConstant.DEBUG;
                    DebugHelper.init(app);
                }
                // 友盟推送需要在每个进程初始化
                UmengPushManager.init(app);
            }
        }.run();
    }

    private void initSystemListener()
    {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber)
            {
                EOnCallStateChanged event = new EOnCallStateChanged();
                event.state = state;
                event.incomingNumber = incomingNumber;
                SDEventManager.post(event);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public boolean isPushStartActivity(Class<?> clazz)
    {
        boolean result = false;
        if (pushRunnable != null)
        {
            result = pushRunnable.getStartActivity() == clazz;
        }
        return result;
    }

    public void setPushRunnable(PushRunnable pushRunnable)
    {
        this.pushRunnable = pushRunnable;
    }

    public PushRunnable getPushRunnable()
    {
        return pushRunnable;
    }

    public void startPushRunnable()
    {
        if (pushRunnable != null)
        {
            pushRunnable.run();
            pushRunnable = null;
        }
    }


    public void exitApp(boolean isBackground)
    {
        AppRuntimeWorker.logout();
        SDActivityManager.getInstance().finishAllActivity();
        EExitApp event = new EExitApp();
        SDEventManager.post(event);
        if (!isBackground)
        {
            System.exit(0);
        }
    }

    /**
     * 退出登录
     *
     * @param post
     */
    public void logout(boolean post)
    {
        logout(post, true, false);
    }

    public void logout(boolean post, boolean isStartLogin, boolean isStartH5Main)
    {
        UserModelDao.delete();
        AppRuntimeWorker.setUsersig(null);
        AppRuntimeWorker.logout();
        CommonInterface.requestLogout(null);
        RetryInitWorker.getInstance().start();
        StorageFileUtils.deleteCrop_imageFile();

        if (isStartLogin)
        {
            Intent intent = new Intent(this, LiveLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SDActivityManager.getInstance().getLastActivity().startActivity(intent);
        } else if (isStartH5Main)
        {
            //否则启动H5页面
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SDActivityManager.getInstance().getLastActivity().startActivity(intent);
        }

        if (post)
        {
            EUserLogout event = new EUserLogout();
            SDEventManager.post(event);
        }
    }

    /**
     * 退出登录
     *
     * @param event
     */
    public void onEventMainThread(EJsLogout event)
    {
        logout(true);
    }

    public void onEventMainThread(EUserLoginSuccess event)
    {
        AppRuntimeWorker.setUsersig(null);
        CommonInterface.requestMyUserInfo(new AppRequestCallback<App_userinfoActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    CommonInterface.requestUsersig(null);
                }
            }
        });
    }

    public void onEventMainThread(SubscriberExceptionEvent event)
    {
        event.throwable.printStackTrace();
    }

    @Override
    public void onTerminate()
    {
        SDEventManager.unregister(instance);
        TANetworkStateReceiver.unRegisterNetworkStateReceiver(instance);
        TANetworkStateReceiver.removeRegisterObserver(instance);
        SDHandlerManager.stopBackgroundHandler();
        SDMediaRecorder.getInstance().release();
        super.onTerminate();
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
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
