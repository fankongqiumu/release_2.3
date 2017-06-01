package com.fanwe.hybrid.common;

import android.app.Activity;

import com.fanwe.hybrid.app.App;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.library.utils.SDToast;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-3-21 上午10:40:36 类说明
 */
public class CommonOpenLoginSDK
{
    /**
     * 点击微信登录，先获取个人资料
     */
    public static void loginWx(final Activity activity)
    {
        umLogin(activity, SHARE_MEDIA.WEIXIN, new UMAuthListener()
        {

            @Override
            public void onStart(SHARE_MEDIA share_media)
            {
                
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data)
            {
                SDToast.showToast("授权成功");
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
        });
    }

    public static void umSinalogin(Activity activity, UMAuthListener listener)
    {
        umLogin(activity, SHARE_MEDIA.SINA, listener);
    }

    public static void umQQlogin(Activity activity, UMAuthListener listener)
    {
        umLogin(activity, SHARE_MEDIA.QQ, listener);
    }

    public static void umLogin(Activity activity, SHARE_MEDIA platform, UMAuthListener listener)
    {
        if (activity == null || platform == null)
        {
            return;
        }

        if (platform == SHARE_MEDIA.WEIXIN)
        {
            if (!SDPackageUtil.isAppInstalled(activity, "com.tencent.mm"))
            {
                SDToast.showToast("您未安装微信客户端");
                return;
            }
        } else if (platform == SHARE_MEDIA.QQ)
        {
            if (!SDPackageUtil.isAppInstalled(activity, "com.tencent.mobileqq"))
            {
                SDToast.showToast("您未安装QQ客户端");
                return;
            }
        }

        UMShareAPI shareAPI = UMShareAPI.get(App.getApplication());
        if (shareAPI == null)
        {
            SDToast.showToast("UMShareAPI is null");
            return;
        }
        shareAPI.doOauthVerify(activity, platform, listener);
    }
}
