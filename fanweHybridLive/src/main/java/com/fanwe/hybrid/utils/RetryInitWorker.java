package com.fanwe.hybrid.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.http.AppRequestParams;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.dialog.SDDialogCustom.SDDialogCustomListener;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.sunday.eventbus.SDEventManager;
import com.ta.util.netstate.TANetChangeObserver;
import com.ta.util.netstate.TANetWorkUtil;
import com.ta.util.netstate.TANetWorkUtil.netType;
import com.ta.util.netstate.TANetworkStateReceiver;

public class RetryInitWorker implements TANetChangeObserver
{

    /**
     * 重试时间间隔
     */
    private static final int RETRY_TIME_SPAN = 1000 * 5;
    /**
     * 重试次数
     */
    private static final int RETRY_MAX_COUNT = 60;

    private static RetryInitWorker sInstance;
    /**
     * 是否正在重试中
     */
    private boolean isInRetryInit = false;
    /**
     * 是否初始化成功
     */
    private boolean isInitSuccess = false;
    /**
     * 重试次数
     */
    private int retryCount = 0;
    /**
     * 重试失败窗口
     */
    private Dialog retryFailDialog;

    private RetryInitWorker()
    {
        TANetworkStateReceiver.registerObserver(this);
    }

    public static RetryInitWorker getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new RetryInitWorker();
        }
        return sInstance;
    }

    /**
     * 开始重试初始化
     */
    public void start()
    {
        if (isInRetryInit)
        {
            return;
        }
        isInRetryInit = true;
        isInitSuccess = false;
        retryCount = 0;

        retryRunnable.run();
    }

    private SDDelayRunnable retryRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            LogUtil.i("retry init:" + retryCount);

            if (isInitSuccess)
            {
                stop();
                return;
            }

            if (retryCount >= RETRY_MAX_COUNT && !isInitSuccess)
            {
                // 达到最大重试次数，并且没有初始化成功
                stop();
                showRetryFailDialog();
                return;
            }

            if (!TANetWorkUtil.isNetworkAvailable(App.getApplication()))
            {
                // 无网络
                LogUtil.i("stop retry none net");
                stop();
                return;
            }

            if (AppRuntimeWorker.getIsOpenWebviewMain())
            {
                requestH5Init();
            } else
            {
                requestInit();
            }
        }
    };

    private void requestH5Init()
    {
        if (isInitSuccess)
        {
            return;
        }
        AppRequestParams params = new AppRequestParams();
        params.setUrl(ApkConstant.SERVER_URL_INIT_URL);
        AppHttpUtil.getInstance().get(params, new AppRequestCallback<InitActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                LogUtil.i("retry init success");
                InitActModelDao.insertOrUpdate(actModel);
                initSuccess();
            }

            @Override
            protected void onError(SDResponse resp)
            {
                retryRunnable.runDelay(RETRY_TIME_SPAN);
                super.onError(resp);
            }
        });

        retryCount++;
    }

    private void requestInit()
    {
        if (isInitSuccess)
        {
            return;
        }
        CommonInterface.requestInit(new AppRequestCallback<InitActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                LogUtil.i("retry init success");
                InitActModelDao.insertOrUpdate(actModel);
                initSuccess();
            }

            @Override
            protected void onError(SDResponse resp)
            {
                retryRunnable.runDelay(RETRY_TIME_SPAN);
                super.onError(resp);
            }
        });
        retryCount++;
    }

    private void initSuccess()
    {
        isInitSuccess = true;
        stop();
        ERetryInitSuccess event = new ERetryInitSuccess();
        SDEventManager.post(event);
    }

    /**
     * 结束重试
     */
    public void stop()
    {
        isInRetryInit = false;
        LogUtil.i("stop retry");
    }

    protected void showRetryFailDialog()
    {
        if (SDPackageUtil.isBackground())
        {
            return;
        }
        if (retryFailDialog != null && retryFailDialog.isShowing())
        {
            retryFailDialog.dismiss();
        }
        Activity activity = SDActivityManager.getInstance().getLastActivity();
        if (activity == null)
        {
            return;
        }

        SDDialogConfirm dialog = new SDDialogConfirm(activity);
        dialog.setCancelable(false);
        dialog.setTextContent("已经尝试初始化" + RETRY_MAX_COUNT + "次失败，是否继续重试？");
        dialog.setTextConfirm("重试").setmListener(new SDDialogCustomListener()
        {

            @Override
            public void onDismiss(SDDialogCustom dialog)
            {

            }

            @Override
            public void onClickConfirm(View v, SDDialogCustom dialog)
            {
                start();
            }

            @Override
            public void onClickCancel(View v, SDDialogCustom dialog)
            {
                stop();
            }
        });
        dialog.show();
        retryFailDialog = dialog;
    }


    @Override
    public void onConnect(netType type)
    {
        if (!isInitSuccess)
        {
            start();
        }
    }

    @Override
    public void onDisConnect()
    {
    }

}
