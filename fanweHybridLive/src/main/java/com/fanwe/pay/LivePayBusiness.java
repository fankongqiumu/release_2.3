package com.fanwe.pay;

import com.fanwe.live.LiveConstant;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.model.custommsg.MsgModel;
import com.fanwe.pay.model.custommsg.CustomMsgStartPayMode;
import com.fanwe.pay.model.custommsg.CustomMsgStartScenePayMode;

/**
 * Created by Administrator on 2016/12/1.
 */

public class LivePayBusiness
{
    private LiveInfo liveInfo;

    public LivePayBusiness(LiveInfo liveInfo)
    {
        this.liveInfo = liveInfo;
    }

    public LiveInfo getLiveInfo()
    {
        return liveInfo;
    }

    public void onMsgPayMode(MsgModel msg)
    {

    }

    protected void onMsgPayWillStart(CustomMsgStartPayMode customMsg)
    {

    }

    protected void onMsgScenePayWillStart(CustomMsgStartScenePayMode customMsg)
    {

    }
}
