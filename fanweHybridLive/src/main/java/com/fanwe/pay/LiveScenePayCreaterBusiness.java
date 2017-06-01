package com.fanwe.pay;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.dialog.SDDialogProgress;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.model.App_monitorActModel;
import com.fanwe.pay.common.PayCommonInterface;
import com.fanwe.pay.model.App_live_live_payActModel;
import com.fanwe.pay.model.App_monitorLiveModel;

/**
 * Created by Administrator on 2017/1/9.
 */

public class LiveScenePayCreaterBusiness extends LivePayBusiness
{
    private LiveScenePayCreaterBusinessListener businessListener;

    public void setBusinessListener(LiveScenePayCreaterBusinessListener businessListener)
    {
        this.businessListener = businessListener;
    }

    public LiveScenePayCreaterBusiness(LiveInfo liveInfo)
    {
        super(liveInfo);
    }

    /**
     * 主播心跳回调业务处理
     *
     * @param actModel
     */
    public void onRequestMonitorSuccess(App_monitorActModel actModel)
    {

    }

    /**
     * 按场直播
     *
     * @param live_fee
     */
    public void requestPayScene(int live_fee)
    {
        requestLiveLive_pay(live_fee, 0, 1);
    }


    private void requestLiveLive_pay(int live_fee, final int is_mention, final int live_pay_type)
    {

    }

    public interface LiveScenePayCreaterBusinessListener
    {
        /**
         * 按场付费展示View
         */
        void onScenePayCreaterShowView();

        /**
         * 按场付费返回Model
         *
         * @param actModel
         */
        void onScenePayCreaterSuccess(App_live_live_payActModel actModel);

        /**
         * 主播心跳刷新印票，人数
         *
         * @param app_monitorActModel
         */
        void onScenePayCreaterRequestMonitorSuccess(App_monitorLiveModel app_monitorActModel);
    }
}
