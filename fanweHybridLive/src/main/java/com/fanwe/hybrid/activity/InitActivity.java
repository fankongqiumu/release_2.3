package com.fanwe.hybrid.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.constant.Constant.CommonSharePTag;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.http.AppRequestParams;
import com.fanwe.hybrid.map.tencent.SDTencentMapManager;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.hybrid.umeng.UmengSocialManager;
import com.fanwe.hybrid.utils.RetryInitWorker;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.config.SDConfig;
import com.fanwe.library.utils.SDFileUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveLoginActivity;
import com.fanwe.live.activity.LiveMainActivity;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.Login_test_loginActModel;
import com.fanwe.live.model.UserModel;

import java.util.ArrayList;

import static com.fanwe.live.common.AppRuntimeWorker.getIsOpenWebviewMain;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2015-12-16 下午4:39:42 类说明 启动页
 */
public class InitActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.act_init);
        init();

        if (ApkConstant.DEBUG)
        {
            SDFileUtil.copyAnrToCache(this);
        }
    }

    private void init()
    {
        UmengSocialManager.init(getApplication());
        SDTencentMapManager.getInstance().startLocation(null);

        int init_delayed_time = getResources().getInteger(R.integer.init_delayed_time);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (getIsOpenWebviewMain())
                {
                    requestH5Init();
                } else
                {
                    requestInit();
                }
            }
        }, init_delayed_time);
    }

    private void requestH5Init()
    {
        AppRequestParams params = new AppRequestParams();
        params.setUrl(ApkConstant.SERVER_URL_INIT_URL);
        AppHttpUtil.getInstance().get(params, new AppRequestCallback<InitActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (!InitActModelDao.insertOrUpdate(actModel))
                {
                    SDToast.showToast("保存init信息失败");
                }

                startLiveMain();
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
                SDToast.showToast("初始化失败，开始重拾初始化接口,重拾次数1次");
                RetryInitWorker.getInstance().start();
                startLiveMain();
            }

        });
    }

    private void requestInit()
    {
        CommonInterface.requestInit(new AppRequestCallback<InitActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (!InitActModelDao.insertOrUpdate(actModel))
                {
                    SDToast.showToast("保存init信息失败");
                }

                startLiveMain();
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
                RetryInitWorker.getInstance().start();
                startLiveMain();
            }
        });
    }

    private void startLiveMain()
    {
        //启动本地广告图
        int is_first_open_app = SDConfig.getInstance().getInt(CommonSharePTag.IS_FIRST_OPEN_APP, 0);
        boolean is_open_adv = getResources().getBoolean(R.bool.is_open_adv);
        if (is_first_open_app != 1 && is_open_adv)
        {
            ArrayList<String> array = new ArrayList<String>();
            Resources res = getResources();
            String[] adv_img_array = res.getStringArray(R.array.adv_img_array);
            for (int i = 0; i < adv_img_array.length; i++)
            {
                array.add(adv_img_array[i]);
            }
            startInitAdvList(array);
            return;
        }

        startAdImgActivityOrLiveMainActivity();
    }

    private void startAdImgActivityOrLiveMainActivity()
    {
        String mImgUrl = "";
        try
        {
            InitActModel model = InitActModelDao.query();
            if (model.getStart_diagram().size() != 0)
            {
                mImgUrl = model.getStart_diagram().get(0).getImage();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        //如果图片有缓存显示InitActivity，否则显示rLiveMainActivity
        if (!TextUtils.isEmpty(mImgUrl))
        {
            startAdImgActivity();
        } else
        {
            startLiveMainActivity();
        }
    }

    private void startLiveMainActivity()
    {
        if (getIsOpenWebviewMain())
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
                            Intent intent = new Intent(InitActivity.this, LiveMainActivity.class);
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

    private void startInitAdvList(ArrayList<String> array)
    {
        Intent intent = new Intent(InitActivity.this, InitAdvListActivity.class);
        intent.putStringArrayListExtra(InitAdvListActivity.EXTRA_ARRAY, array);
        startActivity(intent);
        finish();
    }

    private void startAdImgActivity()
    {
        Intent intent = new Intent(InitActivity.this, AdImgActivity.class);
        startActivity(intent);
        finish();
    }
}
