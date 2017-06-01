package com.fanwe.live.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.constant.Constant;
import com.fanwe.hybrid.event.EPaySdk;
import com.fanwe.hybrid.event.ERefreshReload;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.hybrid.model.PaySdkModel;
import com.fanwe.library.title.SDTitleItem;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.webview.CustomWebView;
import com.fanwe.library.webview.DefaultWebViewClient;
import com.fanwe.live.R;
import com.fanwe.live.event.EWxPayResultCodeComplete;
import com.fanwe.live.wxapi.WXPayEntryActivity;
import com.fanwe.o2o.jshandler.O2OShoppingLiveJsHander;

import org.xutils.http.cookie.DbCookieStore;
import org.xutils.view.annotation.ViewInject;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import static com.fanwe.live.appview.H5AppViewWeb.no_network_url;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class LiveWebViewActivity extends BaseTitleActivity implements PayResultListner
{
    private static final int REQUEST_CODE_BAOFOO_SDK_RZ = 100;// 宝付支付

    private boolean isBackFinish = false;//返回是否关闭页面

    // webview 要加载的链接
    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_IS_BACK_FINISH = "extra_is_back_finish";

    public static final String EXTRA_IS_SHOW_TITLE = "extra_is_show_title";

    private String url;

    private boolean isShowTitle;//是否展示标题

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
        customWebView.addJavascriptInterface(new O2OShoppingLiveJsHander(this, customWebView));
        customWebView.getSettings().setBuiltInZoomControls(true);
        getIntentInfo();
        initWebView();
        initRightText();
    }

    private void getIntentInfo()
    {
        url = getIntent().getStringExtra(EXTRA_URL);
        isBackFinish = getIntent().getBooleanExtra(EXTRA_IS_BACK_FINISH, false);
        isShowTitle = getIntent().getBooleanExtra(EXTRA_IS_SHOW_TITLE, true);

        isShowTitle(isShowTitle);
    }

    private void initWebView()
    {
        customWebView.setWebViewClient(new DefaultWebViewClient()
        {
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

                String cookie = CookieManager.getInstance().getCookie(url);
                LogUtil.i("cookie:" + cookie);
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

        initCookies();
        customWebView.get(url);
    }

    private void initRightText()
    {
        mTitle.initRightItem(1);
        mTitle.getItemRight(0).setTextBot("关闭");
    }

    @Override
    public void onCLickRight_SDTitleSimple(SDTitleItem v, int index)
    {
        super.onCLickRight_SDTitleSimple(v, index);
        onClickCloseWebview();
    }

    protected void onClickCloseWebview()
    {
        finish();
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
                String url_domain = ApkConstant.SERVER_URL;
                URI uri = new URI(url_domain);
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
    protected void onRestart()
    {
        super.onRestart();
        customWebView.reload();
    }

    @Override
    public void onBackPressed()
    {
        if (isBackFinish)
        {
            finish();
            return;
        }

        if (customWebView.canGoBack())
        {
            customWebView.goBack();
        } else
        {
            super.onBackPressed();
        }
    }

    /**
     * 网络刷新
     *
     * @param event
     */
    public void onEventMainThread(ERefreshReload event)
    {
        customWebView.loadUrl(url);
        customWebView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //清除历史记录
                customWebView.clearHistory();
            }
        }, 1000);
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

    private Handler mHandler = new Handler();

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
