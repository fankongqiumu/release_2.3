package com.fanwe.hybrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveLoginActivity;
import com.fanwe.live.activity.LiveMainActivity;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.event.EFinishAdImg;
import com.fanwe.live.model.LiveBannerModel;
import com.fanwe.live.model.Login_test_loginActModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.utils.GlideUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-2-23 上午11:35:31 类说明
 */
public class AdImgActivity extends BaseActivity implements OnClickListener
{
    public static final String EXTRA_URL = "extra_url";

    @ViewInject(R.id.iv_ad_img)
    private ImageView mIvAdImg;

    private String mImgUrl;
    private int init_delayed_time;

    private Handler handler;
    private InitActModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ad_img);
        x.view().inject(this);
        init();
    }

    private void init()
    {
        setFullScreen();
        initInitActMode();
        register();
        initView();
        initStartAct();
    }

    private void initStartAct()
    {
        if (!TextUtils.isEmpty(mImgUrl))
        {
            init_delayed_time = getResources().getInteger(R.integer.init_delayed_time);
        }
    }

    private void initInitActMode()
    {
        model = InitActModelDao.query();
        if (model != null && model.getStart_diagram() != null && model.getStart_diagram().size() > 0)
        {
            mImgUrl = model.getStart_diagram().get(0).getImage();
        }
    }

    private void register()
    {
        mIvAdImg.setOnClickListener(this);
    }

    private void initView()
    {
        if (!TextUtils.isEmpty(mImgUrl))
        {
            GlideUtil.load(mImgUrl).into(mIvAdImg);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_ad_img:
                startAdImgWebViewActivity();
                break;
        }
    }

    private void startAdImgWebViewActivity()
    {
        LiveBannerModel item = model.getStart_diagram().get(0);
        Intent intent = item.parseType(this);
        if (intent != null)
        {
            handler.removeCallbacks(mRunnable);//停止计时跳过
            handler = null;
            startActivity(intent);
        }
    }

    public void onEventMainThread(EFinishAdImg event)
    {
        startLiveMainActivity();
    }

    private void startLiveMainActivity()
    {
        boolean is_open_webview_main = getResources().getBoolean(R.bool.is_open_webview_main);
        if (is_open_webview_main)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        UserModel user = UserModelDao.query();
        if (user != null)
        {
            Intent intent = new Intent(this, LiveMainActivity.class);
            startActivity(intent);
            finish();
        } else
        {
            if (ApkConstant.AUTO_REGISTER)
            {
                CommonInterface.requestTestLogin(new AppRequestCallback<Login_test_loginActModel>()
                {
                    @Override
                    protected void onSuccess(SDResponse resp)
                    {
                        if (actModel.isOk())
                        {
                            Intent intent = new Intent(AdImgActivity.this, LiveMainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            } else
            {
                Intent intent = new Intent(this, LiveLoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void initHandler()
    {
        if (handler == null)
        {
            handler = new Handler();
        }
        handler.postDelayed(mRunnable, init_delayed_time);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initHandler();
    }

    private Runnable mRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            startLiveMainActivity();
        }
    };
}
