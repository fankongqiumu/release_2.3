package com.fanwe.hybrid.constant;

import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.live.BuildConfig;
import com.fanwe.live.R;

public class ApkConstant
{

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final boolean AUTO_REGISTER = false;

    /**
     * 服务器地址
     */
    public static final String SERVER_URL = BuildConfig.SERVER_URL;
    /**
     * 接口路径
     */
    public static final String SERVER_URL_PATH_API = "/mapi/index.php";
    /**
     * 接口完整地址
     */
    public static final String SERVER_URL_API = SERVER_URL + SERVER_URL_PATH_API;

    /**
     * 初始化接口完整地址
     */
    public static final String SERVER_URL_INIT_URL = BuildConfig.SERVER_URL_INIT_URL;

    /**
     * 数据传输加密key
     */
    public static final String AES_KEY = SDResourcesUtil.getString(R.string.app_id_tencent_live) + "000000";
}
