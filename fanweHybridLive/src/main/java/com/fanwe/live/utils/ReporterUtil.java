package com.fanwe.live.utils;

import com.fanwe.hybrid.app.App;
import com.fanwe.library.utils.SDDateUtil;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.UserModel;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/12/7.
 */

public class ReporterUtil
{

    public static void reportUserError(String msg)
    {
        UserModel user = UserModelDao.query();
        if (user != null)
        {
            msg = "user(" + user.getUser_id() + ") " + msg;
        }
        reportError(msg);
    }

    public static void reportError(String msg)
    {
        String time = SDDateUtil.getNow_yyyyMMddHHmmss();
        msg = time + "：" + msg;
        MobclickAgent.reportError(App.getApplication(), msg);
    }


}
