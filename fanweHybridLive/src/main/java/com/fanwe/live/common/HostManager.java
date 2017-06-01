package com.fanwe.live.common;

import android.content.Context;
import android.text.TextUtils;

import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.http.AppRequestParams;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.callback.SDRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.listener.SDResultListener;
import com.fanwe.library.utils.IOUtil;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDJsonUtil;
import com.fanwe.library.utils.SDNetWorkUtil;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.live.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 域名管理类
 */
public class HostManager
{

    private static final int DURATION_SUC_RETRY = 5 * 1000;
    private static final int DURATION_ERR_RETRY = 5 * 1000;

    private static HostManager sInstance;

    private HostModel hostModel;
    private LinkedList<String> listTemp = new LinkedList<>();

    private boolean isFinding = false;
    private long nextRetryTime;

    private HostManager()
    {
        hostModel = HostModel.get();
    }

    public static HostManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (HostManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new HostManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获得接口地址
     *
     * @return
     */
    public String getApiUrl()
    {
        return hostModel.getApiUrl();
    }

    /**
     * 设置接口地址
     *
     * @param apiUrl
     */
    public void setApiUrl(String apiUrl)
    {
        hostModel.setApiUrl(apiUrl);
        hostModel.save();
    }

    /**
     * 保存接口返回的新域名
     */
    public void loadNew()
    {
        hostModel.loadNew();
    }

    /**
     * 设置下次可以重试的时间
     */
    private synchronized void setNextRetryTime(long duration)
    {
        if (duration <= 0)
        {
            duration = DURATION_ERR_RETRY;
        }
        nextRetryTime = System.currentTimeMillis() + duration;
    }

    /**
     * 设置是否正在查找中
     *
     * @param finding
     */
    private synchronized void setFinding(boolean finding)
    {
        isFinding = finding;
    }

    /**
     * 重试
     *
     * @param callback
     */
    public synchronized void retry(final SDRequestCallback callback)
    {
        if (callback == null)
        {
            return;
        }
        if (isFinding)
        {
            LogUtil.i("isFinding");
            return;
        }
        if (System.currentTimeMillis() < nextRetryTime)
        {
            LogUtil.i("waiting next retry");
            return;
        }
        if (!SDNetWorkUtil.isNetworkConnected(App.getApplication()))
        {
            LogUtil.i("network is not connected");
            return;
        }

        setFinding(true);
        fillListTemp();
        findAvailableHost(new SDResultListener<String>()
        {
            @Override
            public void onSuccess(String result)
            {
                LogUtil.i("find success " + result);

                setApiUrl(result + ApkConstant.SERVER_URL_PATH_API);
                AppHttpUtil.getInstance().post(callback.getRequestParams(), callback);

                setFinding(false);
                setNextRetryTime(DURATION_SUC_RETRY);
            }

            @Override
            public void onError(int code, String msg)
            {
                LogUtil.i("find error " + msg);

                setFinding(false);
                setNextRetryTime(DURATION_ERR_RETRY);
            }
        });
        LogUtil.i("start find----------------");
    }

    private void fillListTemp()
    {
        listTemp.clear();
        listTemp.addAll(hostModel.getList());
    }

    /**
     * 找到可用的域名
     *
     * @param listener
     */
    private void findAvailableHost(final SDResultListener<String> listener)
    {
        if (!SDNetWorkUtil.isNetworkConnected(App.getApplication()))
        {
            if (listener != null)
            {
                listener.onError(-1, "no network");
            }
            return;
        }

        if (listTemp.isEmpty())
        {
            if (listener != null)
            {
                listener.onError(-1, "not found");
            }
        } else
        {
            final String host = listTemp.pollFirst();

            AppRequestParams params = new AppRequestParams();
            params.setUrl(host + ApkConstant.SERVER_URL_PATH_API);
            params.putCtl("app");
            params.putAct("init");
            AppHttpUtil.getInstance().post(params, new AppRequestCallback<InitActModel>()
            {
                @Override
                protected void onSuccess(SDResponse sdResponse)
                {
                    if (actModel != null)
                    {
                        if (listener != null)
                        {
                            listener.onSuccess(host);
                        }
                    } else
                    {
                        findAvailableHost(listener);
                    }
                }

                @Override
                protected void onError(SDResponse resp)
                {
                    super.onError(resp);
                    findAvailableHost(listener);
                }
            });
        }
    }

    private static class HostModel
    {
        private static final String FILE_NAME = "domains";

        private LinkedList<String> list = new LinkedList<>();
        private String apiUrl;
        private int versionCode;

        public void setVersionCode(int versionCode)
        {
            this.versionCode = versionCode;
        }

        public int getVersionCode()
        {
            return versionCode;
        }

        public LinkedList<String> getList()
        {
            return list;
        }

        public void setList(LinkedList<String> list)
        {
            this.list = list;
        }

        public String getApiUrl()
        {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl)
        {
            this.apiUrl = apiUrl;
        }

        /**
         * 加载打包配置的域名
         */
        public void loadLocal()
        {
            String[] arrHost = App.getApplication().getResources().getStringArray(R.array.arr_host);
            if (arrHost != null && arrHost.length > 0)
            {
                List<String> listLocal = Arrays.asList(arrHost);
                this.list.addAll(listLocal);
                LogUtil.i("load local:" + Arrays.toString(arrHost));
            }
        }

        /**
         * 把初始化的新列表保存到本地
         */
        public void loadNew()
        {
            InitActModel actModel = InitActModelDao.query();
            if (actModel != null)
            {
                List<String> listNew = actModel.getDomain_list();
                if (listNew != null && !listNew.isEmpty())
                {
                    this.list.clear();
                    this.list.addAll(listNew);
                    LogUtil.i("load new:" + this.list.toString());
                    save();
                }
            }
        }

        public boolean isEmpty()
        {
            return this.list.isEmpty();
        }

        public static HostModel get()
        {
            try
            {
                HostModel model = query();
                if (model == null)
                {
                    model = new HostModel();
                }

                boolean needSave = false;
                if (SDPackageUtil.getVersionCode() > model.getVersionCode())
                {
                    //app升级或者第一次安装
                    model.setApiUrl(ApkConstant.SERVER_URL_API);
                    model.setVersionCode(SDPackageUtil.getVersionCode());
                    model.loadLocal();
                    needSave = true;
                }

                if (model.isEmpty())
                {
                    model.loadLocal();
                    needSave = true;
                }

                if (needSave)
                {
                    model.save();
                }
                return model;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 查找本地对象
         *
         * @return
         */
        private static HostModel query()
        {
            InputStream is = null;
            try
            {
                is = App.getApplication().openFileInput(FILE_NAME);
                String content = IOUtil.readStr(is);
                if (!TextUtils.isEmpty(content))
                {
                    HostModel model = SDJsonUtil.json2Object(content, HostModel.class);
                    LogUtil.i("query model success");
                    return model;
                } else
                {
                    LogUtil.i("query model empty");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                LogUtil.i("query model error:" + e.toString());
            } finally
            {
                IOUtil.closeQuietly(is);
            }
            return null;
        }

        /**
         * 保存对象到本地
         */
        private void save()
        {
            OutputStream os = null;
            try
            {
                String content = SDJsonUtil.object2Json(this);
                os = App.getApplication().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                IOUtil.writeStr(os, content);
                LogUtil.i("save model success");
            } catch (Exception e)
            {
                e.printStackTrace();
                LogUtil.i("save model error:" + e.toString());
            } finally
            {
                IOUtil.closeQuietly(os);
            }
        }
    }

}
