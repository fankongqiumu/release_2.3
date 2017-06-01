package com.fanwe.hybrid.http;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.dao.JsonDbModelDao;
import com.fanwe.hybrid.event.EUnLogin;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.hybrid.model.BaseEncryptModel;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.hybrid.utils.RetryInitWorker;
import com.fanwe.library.adapter.http.callback.SDModelRequestCallback;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.adapter.http.model.SDRequestParams;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.utils.AESUtil;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDJsonUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.common.HostManager;
import com.sunday.eventbus.SDEventManager;

public abstract class AppRequestCallback<D> extends SDModelRequestCallback<D>
{

    protected AppRequestParams requestParams;
    protected BaseActModel baseActModel;
    protected boolean isCache;

    @Override
    protected void onStartAfter()
    {
        SDRequestParams sp = getRequestParams();
        if (sp != null && sp instanceof AppRequestParams)
        {
            requestParams = (AppRequestParams) sp;
        }

        if (clazz != null)
        {
            D model = JsonDbModelDao.getInstance().query(clazz);
            if (model != null)
            {
                if (model instanceof BaseActModel)
                {
                    BaseActModel baseActModel = (BaseActModel) model;

                    long current = System.currentTimeMillis();
                    long endTime = baseActModel.getExpiry_after();

                    if (current <= endTime * 1000)
                    {
                        // 缓存还没过期
                        isCache = true;
                        SDRequestHandler requestHandler = getRequestHandler();
                        if (requestHandler != null)
                        {
                            requestHandler.cancel();
                        }

                        SDResponse resp = new SDResponse();
                        resp.setResult(SDJsonUtil.object2Json(model));
                        if (requestParams != null)
                        {
                            requestParams.setRequestDataType(AppRequestParams.RequestDataType.NORMAL);
                        }
                        notifySuccess(resp);
                    }
                }
            }
        }
    }

    @Override
    protected void onSuccessBefore(SDResponse resp)
    {
        if (requestParams != null)
        {
            String result = resp.getResult();
            if (!TextUtils.isEmpty(result))
            {
                if (ApkConstant.DEBUG)
                {
                    if (result.contains("false"))
                    {
                        SDToast.showToast(requestParams.getCtl() + "," + requestParams.getAct() + " false");
                    }
                }

                switch (requestParams.getRequestDataType())
                {
                    case AppRequestParams.RequestDataType.NORMAL:

                        break;
                    case AppRequestParams.RequestDataType.AES:
                        try
                        {
                            BaseEncryptModel model = SDJsonUtil.json2Object(result, BaseEncryptModel.class);
                            String decryptResult = AESUtil.decrypt(model.getOutput(), ApkConstant.AES_KEY);
                            if (!TextUtils.isEmpty(decryptResult))
                            {
                                result = decryptResult;
                            }
                        } catch (Exception e)
                        {
                            LogUtil.e(e.toString());
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
                // 解密后的result赋值回去
                resp.setResult(result);
                requestParams.setRequestDataType(AppRequestParams.RequestDataType.NORMAL);
                LogUtil.i("onSuccessBefore:" + requestParams.getCtl() + "," + requestParams.getAct() + "：" + result);
            }
        }
        // 调用父类方法转实体
        super.onSuccessBefore(resp);

        if (actModel != null)
        {
            if (actModel instanceof BaseActModel)
            {
                baseActModel = (BaseActModel) actModel;
                if (baseActModel.getExpiry_after() > 0)
                {
                    JsonDbModelDao.getInstance().insertOrUpdate(actModel);
                }
                if (requestParams != null)
                {
                    InitActModel initActModel = InitActModelDao.query();
                    if (initActModel != null)
                    {
                        if (baseActModel.getInit_version() > initActModel.getInit_version())
                        {
                            RetryInitWorker.getInstance().start();
                        }
                    }

                    if (requestParams.isNeedShowActInfo())
                    {
                        SDToast.showToast(baseActModel.getError());
                    }
                    if (requestParams.isNeedCheckLoginState())
                    {
                        if (baseActModel.getUser_login_status() == 0)
                        {
                            // 未登录
                            Activity activity = SDActivityManager.getInstance().getLastActivity();
                            if (ApkConstant.DEBUG && activity != null)
                            {
                                SDDialogConfirm dialogConfirm = new SDDialogConfirm(activity);
                                dialogConfirm.setCanceledOnTouchOutside(false);
                                dialogConfirm.setCancelable(false);
                                dialogConfirm.setTextContent(requestParams.getCtl() + "," + requestParams.getAct() + ":未登录");
                                dialogConfirm.setTextCancel(null).setmListener(new SDDialogCustom.SDDialogCustomListener()
                                {
                                    @Override
                                    public void onClickCancel(View v, SDDialogCustom dialog)
                                    {
                                    }

                                    @Override
                                    public void onClickConfirm(View v, SDDialogCustom dialog)
                                    {
                                        dealUnLogin();
                                    }

                                    @Override
                                    public void onDismiss(SDDialogCustom dialog)
                                    {
                                    }
                                }).show();
                            } else
                            {
                                dealUnLogin();
                            }
                        }
                    }
                }
            }
        }
    }

    private void dealUnLogin()
    {
        EUnLogin event = new EUnLogin();
        SDEventManager.post(event);
        if (AppRuntimeWorker.getIsOpenWebviewMain())
        {
            App.getApplication().logout(false, false, true);
        } else
        {
            App.getApplication().logout(true);
        }
    }

    @Override
    protected void onSuccessAfter(SDResponse resp)
    {
        if (isCache)
        {
            notifyFinish(resp);
        }
    }

    @Override
    protected void onError(SDResponse resp)
    {
        if (requestParams != null)
        {
            LogUtil.i("onError:" + requestParams.getCtl() + "," + requestParams.getAct() + "：" + resp.getThrowable());
            CommonInterface.reportErrorLog("ctl=" + requestParams.getCtl() + "," + "act=" + requestParams.getAct() + ":" + String.valueOf(resp.getThrowable()));
            if (ApkConstant.DEBUG)
            {
                SDToast.showToast(requestParams.getCtl() + "," + requestParams.getAct() + " " + String.valueOf(resp.getThrowable()));
            }
        }
        HostManager.getInstance().retry(this);
    }

    @Override
    protected void onCancel(SDResponse resp)
    {

    }

    @Override
    protected void onFinish(SDResponse resp)
    {

    }

    @Override
    protected <T> T parseActModel(String result, Class<T> clazz)
    {
        return SDJsonUtil.json2Object(result, clazz);
    }
}
