package com.fanwe.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.fanwe.hybrid.activity.AppWebViewActivity;
import com.fanwe.hybrid.activity.BaseActivity;
import com.fanwe.hybrid.common.CommonOpenLoginSDK;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.event.EJsWxBackInfo;
import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.blocker.SDBlocker;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.event.EFirstLoginNewLevel;
import com.fanwe.live.event.EUserLoginSuccess;
import com.fanwe.live.model.App_do_updateActModel;
import com.fanwe.live.model.UserModel;
import com.sunday.eventbus.SDEventManager;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.xutils.view.annotation.ViewInject;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/5.
 */
public class LiveLoginActivity extends BaseActivity
{
    @ViewInject(R.id.ll_weixin)
    private LinearLayout ll_weixin;
    @ViewInject(R.id.iv_weixin)
    private ImageView iv_weixin;

    @ViewInject(R.id.ll_qq)
    private LinearLayout ll_qq;
    @ViewInject(R.id.iv_qq)
    private ImageView iv_qq;

    @ViewInject(R.id.iv_xinlang)
    private ImageView iv_xinlang;
    @ViewInject(R.id.ll_xinlang)
    private LinearLayout ll_xinlang;

    @ViewInject(R.id.iv_shouji)
    private ImageView iv_shouji;
    @ViewInject(R.id.ll_shouji)
    private LinearLayout ll_shouji;

    @ViewInject(R.id.tv_agreement)
    private TextView tv_agreement;

    private SDBlocker blocker = new SDBlocker(2000);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mIsExitApp = true;
        setFullScreen();
        setContentView(R.layout.act_live_login);
        init();
    }

    private void init()
    {
        register();
        bindDefaultData();
        initLoginIcon();
    }

    private void register()
    {
        iv_qq.setOnClickListener(this);
        iv_xinlang.setOnClickListener(this);
        iv_weixin.setOnClickListener(this);
        iv_shouji.setOnClickListener(this);
        tv_agreement.setOnClickListener(this);
    }

    private void bindDefaultData()
    {
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null)
        {
            String privacy_titile = initActModel.getPrivacy_title();
            SDViewBinder.setTextView(tv_agreement, privacy_titile);
        }
    }

    private void initLoginIcon()
    {
        InitActModel model = InitActModelDao.query();
        if (model != null)
        {
            int has_wx_login = model.getHas_wx_login();
            if (has_wx_login == 1)
            {
                SDViewUtil.show(ll_weixin);
            } else
            {
                SDViewUtil.hide(ll_weixin);
            }
            int has_qq_login = model.getHas_qq_login();
            if (has_qq_login == 1)
            {
                SDViewUtil.show(ll_qq);
            } else
            {
                SDViewUtil.hide(ll_qq);
            }
            int has_sina_login = model.getHas_sina_login();
            if (has_sina_login == 1)
            {
                SDViewUtil.show(ll_xinlang);
            } else
            {
                SDViewUtil.hide(ll_xinlang);
            }
            int has_mobile_login = model.getHas_mobile_login();
            if (has_mobile_login == 1)
            {
                SDViewUtil.show(ll_shouji);
            } else
            {
                SDViewUtil.hide(ll_shouji);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (blocker.block())
        {
            return;
        }
        switch (v.getId())
        {
            case R.id.iv_weixin:
                clickIvWeiXing();
                break;
            case R.id.iv_qq:
                clickIvQQ();
                break;
            case R.id.iv_xinlang:
                clickIvSina();
                break;
            case R.id.iv_shouji:
                clickIvShouJi();
                break;
            case R.id.tv_agreement:
                clickTvAgreement();
                break;
        }
    }

    private void clickTvAgreement()
    {
        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null)
        {
            String privacy_link = initActModel.getPrivacy_link();
            if (!TextUtils.isEmpty(privacy_link))
            {
                Intent intent = new Intent(LiveLoginActivity.this, AppWebViewActivity.class);
                intent.putExtra(AppWebViewActivity.EXTRA_URL, privacy_link);
                intent.putExtra(AppWebViewActivity.EXTRA_IS_SCALE_TO_SHOW_ALL, false);
                startActivity(intent);
            }
        }
    }

    private void clickIvWeiXing()
    {
        CommonOpenLoginSDK.loginWx(this);
    }

    private void clickIvQQ()
    {
        CommonOpenLoginSDK.umQQlogin(this, qqListener);
    }

    /**
     * qq授权监听
     */
    private UMAuthListener qqListener = new UMAuthListener()
    {
        @Override
        public void onStart(SHARE_MEDIA share_media)
        {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data)
        {
            SDToast.showToast("授权成功");
            String openid = data.get("openid");
            String access_token = data.get("access_token");
            requestQQ(openid, access_token);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t)
        {
            SDToast.showToast("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action)
        {
            SDToast.showToast("授权取消");
        }
    };

    private void clickIvSina()
    {
        CommonOpenLoginSDK.umSinalogin(this, sinaListener);
    }

    /**
     * 新浪授权监听
     */
    private UMAuthListener sinaListener = new UMAuthListener()
    {
        @Override
        public void onStart(SHARE_MEDIA share_media)
        {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data)
        {
            SDToast.showToast("授权成功");
            String access_token = data.get("access_token");
            String uid = data.get("uid");
            requestSinaLogin(access_token, uid);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t)
        {
            SDToast.showToast("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action)
        {
            SDToast.showToast("授权取消");
        }
    };

    private void clickIvShouJi()
    {
        Intent intent = new Intent(this, LiveMobielRegisterActivity.class);
        startActivity(intent);
    }

    public void onEventMainThread(final EJsWxBackInfo event)
    {
        String json = event.json;
        if (!TextUtils.isEmpty(json))
        {
            requestWeiXinLogin(json);
        }
    }

    private void requestWeiXinLogin(String json)
    {
        String code = JSON.parseObject(json).getString("code");
        if (!TextUtils.isEmpty(code))
        {
            CommonInterface.requestWxLogin(code, new AppRequestCallback<App_do_updateActModel>()
            {
                @Override
                protected void onStart()
                {
                    super.onStart();
                    iv_weixin.setClickable(false);
                    iv_qq.setClickable(false);
                    iv_xinlang.setClickable(false);
                    iv_shouji.setClickable(false);
                }

                @Override
                protected void onFinish(SDResponse resp)
                {
                    super.onFinish(resp);
                    iv_weixin.setClickable(true);
                    iv_qq.setClickable(true);
                    iv_xinlang.setClickable(true);
                    iv_shouji.setClickable(true);
                }

                @Override
                protected void onSuccess(SDResponse resp)
                {
                    if (actModel.getStatus() == 1)
                    {
                        startMainActivity(actModel);
                        setFirstLoginAndNewLevel(actModel);
                    }
                }
            });
        }
    }

    private void requestQQ(String openid, String access_token)
    {
        CommonInterface.requestQqLogin(openid, access_token, new AppRequestCallback<App_do_updateActModel>()
        {
            @Override
            protected void onStart()
            {
                super.onStart();
                iv_weixin.setClickable(false);
                iv_qq.setClickable(false);
                iv_xinlang.setClickable(false);
                iv_shouji.setClickable(false);
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                iv_weixin.setClickable(true);
                iv_qq.setClickable(true);
                iv_xinlang.setClickable(true);
                iv_shouji.setClickable(true);
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    startMainActivity(actModel);
                    setFirstLoginAndNewLevel(actModel);
                }
            }
        });
    }

    private void requestSinaLogin(String access_token, String uid)
    {
        CommonInterface.requestSinaLogin(access_token, uid, new AppRequestCallback<App_do_updateActModel>()
        {
            @Override
            protected void onStart()
            {
                super.onStart();
                iv_weixin.setClickable(false);
                iv_qq.setClickable(false);
                iv_xinlang.setClickable(false);
                iv_shouji.setClickable(false);
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                iv_weixin.setClickable(true);
                iv_qq.setClickable(true);
                iv_xinlang.setClickable(true);
                iv_shouji.setClickable(true);
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    startMainActivity(actModel);
                    setFirstLoginAndNewLevel(actModel);
                }
            }
        });
    }

    private void setFirstLoginAndNewLevel(App_do_updateActModel actModel)
    {
        InitActModel initActModel = InitActModelDao.query();
        initActModel.setFirst_login(actModel.getFirst_login());
        initActModel.setNew_level(actModel.getNew_level());
        if (!InitActModelDao.insertOrUpdate(initActModel))
        {
            SDToast.showToast("保存init信息失败");
        }
        //发送事件首次登陆升级
        EFirstLoginNewLevel event = new EFirstLoginNewLevel();
        SDEventManager.post(event);
    }

    private void startMainActivity(App_do_updateActModel actModel)
    {
        UserModel user = actModel.getUser_info();
        if (user != null)
        {
            if (UserModel.dealLoginSuccess(user, true))
            {
                Intent intent = new Intent(LiveLoginActivity.this, LiveMainActivity.class);
                startActivity(intent);
                finish();
            } else
            {
                SDToast.showToast("保存用户信息失败");
            }
        } else
        {
            SDToast.showToast("没有获取到用户信息");
        }
    }

    /*登录成功接收事件*/
    public void onEventMainThread(EUserLoginSuccess event)
    {
        finish();
    }

    public void onEventMainThread(ERetryInitSuccess event)
    {
        bindDefaultData();
        initLoginIcon();
    }
}
