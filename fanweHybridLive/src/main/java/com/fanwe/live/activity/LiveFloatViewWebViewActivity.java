package com.fanwe.live.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.fanwe.auction.FloatViewPermissionHelp;
import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.constant.Constant;
import com.fanwe.hybrid.event.EPaySdk;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.hybrid.model.PaySdkModel;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.title.SDTitleItem;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.webview.CustomWebView;
import com.fanwe.library.webview.DefaultWebViewClient;
import com.fanwe.live.R;
import com.fanwe.live.activity.room.LivePushCreaterActivity;
import com.fanwe.live.event.EWxPayResultCodeComplete;
import com.fanwe.live.wxapi.WXPayEntryActivity;
import com.fanwe.shop.jshandler.ShopJsHandler;

import org.xutils.http.cookie.DbCookieStore;
import org.xutils.view.annotation.ViewInject;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import static com.fanwe.live.appview.H5AppViewWeb.no_network_url;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class LiveFloatViewWebViewActivity extends BaseTitleActivity implements PayResultListner
{
    private static final int REQUEST_CODE_BAOFOO_SDK_RZ = 100;// 宝付支付

    // webview 要加载的链接
    public static final String EXTRA_URL = "extra_url";
    //要显示的HTML内容
    public static final String EXTRA_HTML_CONTENT = "extra_html_content";

    private String url;
    private String httmContent;

    @ViewInject(R.id.webview)
    protected CustomWebView customWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_live_edit_data);
        init();
    }

    private void init()
    {
        customWebView.addJavascriptInterface(new ShopJsHandler(this));
        customWebView.getSettings().setBuiltInZoomControls(true);
        getIntentInfo();
        initWebView();
        if (!SDActivityManager.getInstance().containActivity(LivePushCreaterActivity.class))
        {
            FloatViewPermissionHelp.checkPermissionAndSwitchSmallScreen(this);
        }
    }

    private void getIntentInfo()
    {
        if (getIntent().hasExtra(EXTRA_URL))
        {
            url = getIntent().getStringExtra(EXTRA_URL);
        }
        if (getIntent().hasExtra(EXTRA_HTML_CONTENT))
        {
            httmContent = getIntent().getStringExtra(EXTRA_HTML_CONTENT);
        }
    }

    private void initWebView()
    {
        customWebView.setWebViewClientListener(new DefaultWebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);
                customWebView.loadUrl(no_network_url);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                mTitle.setMiddleTextTop(view.getTitle());
                mTitle.initRightItem(1);
                mTitle.getItemRight(0).setTextBot("直播");
            }
        });

        customWebView.setWebChromeClientListener(new WebChromeClient()
        {

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                mTitle.setMiddleTextTop(view.getTitle());
            }
        });

        if (!TextUtils.isEmpty(url))
        {
            initCookies();
            customWebView.get(url);
        } else if (!TextUtils.isEmpty(httmContent))
        {
            customWebView.loadData(httmContent, "text/html", "utf-8");
        }
    }

    /**
     * 同步一下cookie
     */
    public void initCookies()
    {
        if (!TextUtils.isEmpty(url))
        {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();//移除
            try
            {
                URI uri = new URI(url);
                List<HttpCookie> list = DbCookieStore.INSTANCE.get(uri);
                for (HttpCookie httpCookie : list)
                {
                    String name = httpCookie.getName();
                    String value = httpCookie.getValue();
                    String cookieString = name + "=" + value;
                    cookieManager.setCookie(url, cookieString);
                }
            } catch (Exception e)
            {

            }
            CookieSyncManager.getInstance().sync();
        }

    }

    @Override
    public void onCLickLeft_SDTitleSimple(SDTitleItem v)
    {
        if (no_network_url.equals(customWebView.getUrl()))
        {
            //如果当前url是网络错误的url，返回就关闭页面
            finish();
            return;
        }
        if (customWebView.canGoBack())
        {
            customWebView.goBack();
        } else
        {
            finish();
        }
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index)
    {
        super.onCLickRight_SDTitleSimple(v, index);
        finish();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        customWebView.reload();
    }

    @Override
    public void onBackPressed()
    {
        if (customWebView.canGoBack())
        {
            customWebView.goBack();
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        customWebView.removeAllViews();
        customWebView.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FloatViewPermissionHelp.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * 支付SDK
     */
    public void onEventMainThread(EPaySdk event)
    {
        openSDKPAY(event.model);
    }

    public void openSDKPAY(PaySdkModel model)
    {
        String payCode = model.getPay_sdk_type();
        if (!TextUtils.isEmpty(payCode))
        {
            if (Constant.PaymentType.UPAPP.equalsIgnoreCase(payCode))
            {
                CommonOpenSDK.payUpApp(model, this, this);
            } else if (Constant.PaymentType.BAOFOO.equalsIgnoreCase(payCode))
            {
                CommonOpenSDK.payBaofoo(model, this, REQUEST_CODE_BAOFOO_SDK_RZ, this);
            } else if (Constant.PaymentType.ALIPAY.equalsIgnoreCase(payCode))
            {
                CommonOpenSDK.payAlipay(model, this, this);
            } else if (Constant.PaymentType.WXPAY.equals(payCode))
            {
                CommonOpenSDK.payWxPay(model, this, this);
            }
        } else
        {
            SDToast.showToast("payCode为空");
            onOther();
        }

    }

    private Handler mHandler =new Handler();

    @Override
    public void onSuccess()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 1);
            }
        });
    }

    @Override
    public void onDealing()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 2);
            }
        });
    }

    @Override
    public void onFail()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 3);
            }
        });
    }

    @Override
    public void onCancel()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 4);
            }
        });
    }

    @Override
    public void onNetWork()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 5);
            }
        });
    }

    @Override
    public void onOther()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                customWebView.loadJsFunction(Constant.JsFunctionName.JS_PAY_SDK, 6);
            }
        });
    }

    /*微信支付回调返回信息*/
    public void onEventMainThread(final EWxPayResultCodeComplete event)
    {
        switch (event.WxPayResultCode)
        {
            case WXPayEntryActivity.RespErrCode.CODE_CANCEL:
                onCancel();
                break;
            case WXPayEntryActivity.RespErrCode.CODE_FAIL:
                onFail();
                break;
            case WXPayEntryActivity.RespErrCode.CODE_SUCCESS:
                onSuccess();
                break;
        }
    }
}
