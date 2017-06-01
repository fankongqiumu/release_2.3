package com.fanwe.live.activity;

import android.os.Bundle;

import com.fanwe.hybrid.jshandler.AppJsHandler;
import com.fanwe.library.activity.WebViewActivity;
import com.fanwe.library.handler.js.AppJsWHandler;
import com.fanwe.live.event.EPayWebViewClose;

import de.greenrobot.event.EventBus;

/**
 * Created by shibx on 2017/3/24.
 */

public class LivePayWebViewActivity extends WebViewActivity {

    private AppJsWHandler mHandler;

    @Override
    protected void onResume() {
        super.onResume();
        if(mHandler == null) {
            mHandler = new AppJsHandler(this);
            mFragWebview.getWebView().addJavascriptInterface(mHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //发送页面关闭事件
        //充值窗口刷新
        EventBus.getDefault().post(new EPayWebViewClose());
    }
}
