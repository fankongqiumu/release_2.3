package com.fanwe.live.business;

import com.fanwe.games.model.App_InitGamesActModel;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDJsonUtil;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_end_videoActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.App_monitorActModel;
import com.fanwe.live.model.App_start_lianmaiActModel;
import com.fanwe.live.model.LiveQualityData;

import java.util.List;

/**
 * 直播间主播业务类
 */
public class LiveCreaterBusiness extends LiveBusiness
{

    private SDRequestHandler requestMonitorHandler;
    private SDRequestHandler requestEndVideoHandler;
    private SDRequestHandler requestStartLianmaiHandler;
    private SDLooper looperMonitor;
    private LiveCreaterBusinessListener businessListener;

    public LiveCreaterBusiness(LiveInfo liveInfo)
    {
        super(liveInfo);
    }

    public void setBusinessListener(LiveCreaterBusinessListener businessListener)
    {
        this.businessListener = businessListener;
        super.setBusinessListener(businessListener);
    }

    /**
     * 更新房间状态为失败
     *
     * @param room_id 房间id
     */
    public void requestUpdateLiveStateFail(int room_id)
    {
        CommonInterface.requestUpdateLiveState(room_id, null, null, null, null, null, 0, null);
    }

    /**
     * 更新房间状态为成功
     *
     * @param room_id   房间id
     * @param group_id  聊天组id
     * @param channelid 旁路直播频道id
     * @param play_rtmp rtmp拉流地址
     * @param play_flv  flv拉流地址
     * @param play_hls  hls拉流地址
     */
    public void requestUpdateLiveStateSuccess(int room_id,
                                              String group_id,
                                              String channelid,
                                              String play_rtmp,
                                              String play_flv,
                                              String play_hls)
    {
        CommonInterface.requestUpdateLiveState(room_id, group_id, channelid, play_rtmp, play_flv, play_hls, 1, null);
    }

    /**
     * 更新房间状态为主播离开
     *
     * @param room_id 房间id
     */
    public void requestUpdateLiveStateLeave(int room_id)
    {
        CommonInterface.requestUpdateLiveState(room_id, null, null, null, null, null, 2, null);
    }

    /**
     * 更新房间状态为主播回来
     *
     * @param room_id 房间id
     */
    public void requestUpdateLiveStateComeback(int room_id)
    {
        CommonInterface.requestUpdateLiveState(room_id, null, null, null, null, null, 3, null);
    }

    /**
     * 请求主播插件列表接口
     */
    public void requestInitPlugins()
    {
        CommonInterface.requestInitPlugins(new AppRequestCallback<App_InitGamesActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    onRequestInitPluginsSuccess(actModel);
                }
            }
        });
    }

    /**
     * 请求主播插件列表成功回调
     *
     * @param actModel
     */
    protected void onRequestInitPluginsSuccess(App_InitGamesActModel actModel)
    {
        businessListener.onLiveCreaterRequestInitPluginsSuccess(actModel);
    }

    @Override
    protected void onRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onRequestRoomInfoSuccess(actModel);
        startMonitor();
    }

    /**
     * 开始主播心跳
     */
    private void startMonitor()
    {
        long time = 0;
        InitActModel model = InitActModelDao.query();
        if (model != null)
        {
            time = model.getMonitor_second() * 1000;
        }
        if (time <= 0)
        {
            time = 5 * 1000;
        }

        if (looperMonitor == null)
        {
            looperMonitor = new SDSimpleLooper();
        }
        looperMonitor.start(time, new Runnable()
        {

            @Override
            public void run()
            {
                CreaterMonitorData data = getCreaterMonitorData();

                int roomId = data.room_id;
                int watch_number = data.watch_number;
                long vote_number = data.vote_number;
                int lianmai_num = data.lianmai_num;
                LiveQualityData live_quality = data.live_quality;
                String quelityJson = null;
                if (live_quality != null)
                {
                    quelityJson = SDJsonUtil.object2Json(live_quality);
                }
                LogUtil.i("monitor data:" + data.toString());
                cancelRequestMonitorHandler();
                requestMonitorHandler = CommonInterface.requestMonitor(roomId,
                        watch_number,
                        vote_number,
                        lianmai_num,
                        quelityJson,
                        new AppRequestCallback<App_monitorActModel>()
                        {
                            @Override
                            protected void onSuccess(SDResponse resp)
                            {
                                onRequestMonitorSuccess(actModel);
                            }
                        });
            }
        });
    }

    public CreaterMonitorData getCreaterMonitorData()
    {
        return businessListener.onLiveCreaterGetMonitorData();
    }

    /**
     * 主播心跳成功回调
     */
    private void onRequestMonitorSuccess(App_monitorActModel actModel)
    {
        businessListener.onLiveCreaterRequestMonitorSuccess(actModel);
    }

    /**
     * 请求结束直播
     *
     * @param listRecord 录制的视频
     */
    public void requestEndVideo(List<String> listRecord)
    {
        String urlJson = null;
        if (listRecord != null && listRecord.size() > 0)
        {
            urlJson = SDJsonUtil.object2Json(listRecord);
        }
        cancelRequestEndVideoHandler();

        endVideoListener.setBusinessListener(businessListener);
        requestEndVideoHandler = CommonInterface.requestEndVideo(getLiveInfo().getRoomId(), urlJson, endVideoListener);
    }

    private abstract class CreaterRequestCallback<T> extends AppRequestCallback<T>
    {
        protected LiveCreaterBusinessListener nBusinessListener;

        public void setBusinessListener(LiveCreaterBusinessListener businessListener)
        {
            this.nBusinessListener = businessListener;
        }
    }

    private CreaterRequestCallback<App_end_videoActModel> endVideoListener = new CreaterRequestCallback<App_end_videoActModel>()
    {
        @Override
        protected void onSuccess(SDResponse sdResponse)
        {
            if (actModel.isOk() && nBusinessListener != null)
            {
                nBusinessListener.onLiveCreaterRequestEndVideoSuccess(actModel);
            }
        }
    };

    private void cancelRequestEndVideoHandler()
    {
        if (requestEndVideoHandler != null)
        {
            requestEndVideoHandler.cancel();
        }
    }

    /**
     * 停止主播心跳
     */
    public void stopMonitor()
    {
        if (looperMonitor != null)
        {
            looperMonitor.stop();
        }
    }

    private void cancelRequestMonitorHandler()
    {
        if (requestMonitorHandler != null)
        {
            requestMonitorHandler.cancel();
        }
    }

    private void cancelRequestStartLianmaiHandler()
    {
        if (requestStartLianmaiHandler != null)
        {
            requestStartLianmaiHandler.cancel();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopMonitor();
        cancelRequestMonitorHandler();
        cancelRequestStartLianmaiHandler();

        endVideoListener.setBusinessListener(null);
        endVideoListener = null;
    }

    /**
     * 接受连麦
     *
     * @param userId 连麦观众id
     */
    public void acceptVideo(final String userId)
    {
        cancelRequestStartLianmaiHandler();
        requestStartLianmaiHandler = CommonInterface.requestStartLianmai(getLiveInfo().getRoomId(), userId, new AppRequestCallback<App_start_lianmaiActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    businessListener.onLiveCreaterAcceptVideo(userId, actModel);
                }
            }
        });
    }

    /**
     * 拒绝连麦
     *
     * @param userId 连麦观众id
     * @param reason 原因
     */
    public void rejectVideo(String userId, String reason)
    {
        businessListener.onLiveCreaterRejectVideo(userId, reason);
    }

    /**
     * 主播心跳需要上传的数据
     */
    public static class CreaterMonitorData
    {
        /**
         * 房间id
         */
        public int room_id;
        /**
         * 真实观众数量
         */
        public int watch_number;
        /**
         * 直播间印票数量
         */
        public long vote_number;
        /**
         * 连麦数量
         */
        public int lianmai_num;
        /**
         * 直播的质量
         */
        public LiveQualityData live_quality;


        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("room_id:").append(room_id).append("\r\n");
            sb.append("watch_number:").append(watch_number).append("\r\n");
            sb.append("vote_number:").append(vote_number).append("\r\n");
            sb.append("lianmai_num:").append(lianmai_num).append("\r\n");
            sb.append("live_quality:").append(live_quality).append("\r\n");
            return sb.toString();
        }
    }

    public interface LiveCreaterBusinessListener extends LiveBusinessListener
    {
        /**
         * 获得主播心跳要提交的数据
         *
         * @return
         */
        CreaterMonitorData onLiveCreaterGetMonitorData();

        /**
         * 请求主播心跳成功回调
         *
         * @param actModel
         */
        void onLiveCreaterRequestMonitorSuccess(App_monitorActModel actModel);

        /**
         * 请求主播插件列表成功回调
         *
         * @param actModel
         */
        void onLiveCreaterRequestInitPluginsSuccess(App_InitGamesActModel actModel);

        /**
         * 请求结束直播接口成功回调
         *
         * @param actModel
         */
        void onLiveCreaterRequestEndVideoSuccess(App_end_videoActModel actModel);

        /**
         * 同意观众的连麦回调
         *
         * @param userId   连麦观众id
         * @param actModel
         */
        void onLiveCreaterAcceptVideo(String userId, App_start_lianmaiActModel actModel);

        /**
         * 拒绝观众连麦
         *
         * @param userId 连麦观众id
         * @param reason 原因
         */
        void onLiveCreaterRejectVideo(String userId, String reason);
    }

}
