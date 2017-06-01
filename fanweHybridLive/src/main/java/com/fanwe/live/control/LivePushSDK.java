package com.fanwe.live.control;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;

import com.fanwe.hybrid.app.App;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.R;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.model.LiveBeautyConfig;
import com.fanwe.live.model.LiveQualityData;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 腾讯推流sdk封装
 */
public class LivePushSDK implements ITXLivePushListener
{
    private static final int CAMERA_FRONT = 0;
    private static final int CAMERA_BACK = 1;
    private static final int CAMERA_NONE = -1;

    private static LivePushSDK sInstance;

    private TXLivePusher pusher;
    private TXLivePushConfig config;
    private TXCloudVideoView videoView;
    /**
     * 推流是否已经启动
     */
    private boolean isPushStarted;
    /**
     * 推流是否被暂停
     */
    private boolean isPushPaused;
    /**
     * 推流地址
     */
    private String url;

    private int cameraId = CAMERA_FRONT;
    /**
     * 美颜值
     */
    private int beautyValue;
    /**
     * 美白值
     */
    private int whiteValue;
    /**
     * 是否镜像
     */
    private boolean isMirror = false;
    /**
     * 背景音乐是否正在播放中
     */
    private boolean isBGMPlaying;
    /**
     * 背景音乐是否已经被启动
     */
    private boolean isBGMStarted;

    private LiveQualityData liveQualityData;
    private ITXLivePushListener pushListener;

    private LivePushSDK()
    {
        liveQualityData = new LiveQualityData();
        config = new TXLivePushConfig();
    }

    public static LivePushSDK getInstance()
    {
        if (sInstance == null)
        {
            synchronized (LivePushSDK.class)
            {
                if (sInstance == null)
                {
                    sInstance = new LivePushSDK();
                }
            }
        }
        return sInstance;
    }

    public void setPushListener(ITXLivePushListener pushListener)
    {
        this.pushListener = pushListener;
    }

    /**
     * 获得渲染view
     */
    public TXCloudVideoView getVideoView()
    {
        return videoView;
    }

    public boolean isPushStarted()
    {
        return isPushStarted;
    }

    public boolean isPushPaused()
    {
        return isPushPaused;
    }

    public String getUrl()
    {
        return url;
    }

    /**
     * 背景音乐是否正在播放中
     */
    public boolean isBGMPlaying()
    {
        return isBGMPlaying;
    }

    /**
     * 音乐是否被启动
     *
     * @return
     */
    public boolean isBGMStarted()
    {
        return isBGMStarted;
    }

    /**
     * 初始化推流
     */
    public void init(TXCloudVideoView videoView)
    {
        this.videoView = videoView;
        initPusher();
        initConfig();
    }

    private void initPusher()
    {
        pusher = new TXLivePusher(App.getApplication());
        updateConfig();
    }

    public TXLivePushConfig getConfig()
    {
        return config;
    }

    private void initConfig()
    {
        setConfigDefault();

        config.setTouchFocus(false);
        config.setPauseFlag(TXLiveConstants.PAUSE_FLAG_PAUSE_AUDIO | TXLiveConstants.PAUSE_FLAG_PAUSE_VIDEO);
        config.setAudioSampleRate(48000);
        config.setAudioChannels(1);
        config.setFrontCamera(true);

        Bitmap bitmap = BitmapFactory.decodeResource(App.getApplication().getResources(), R.drawable.bg_creater_leave);
        config.setPauseImg(bitmap);
        config.setPauseImg(3600, 10);

        updateConfig();
    }

    /**
     * 设置连麦观众配置
     */
    public void setConfigLinkMicSub()
    {
        pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_SUB_PUBLISHER);
        config.setVideoBitrate(200);
        updateConfig();
        LogUtil.i("setConfigLinkMicSub");
    }

    /**
     * 设置主播连麦配置
     */
    public void setConfigLinkMicMain()
    {
        pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_LINKMIC_MAIN_PUBLISHER);
        updateConfig();
        LogUtil.i("setConfigLinkMicMain");
    }

    /**
     * 设置默认配置
     */
    public void setConfigDefault()
    {
        int resolutionType = AppRuntimeWorker.getVideoResolutionType();
        switch (resolutionType)
        {
            case LiveConstant.VideoResolutionType.VIDEO_RESOLUTION_TYPE_360_640:
                pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION);
                config.setVideoBitrate(700); //初始码率
                config.setHardwareAcceleration(false); //硬件加速
                config.setAutoAdjustBitrate(false); //码率自适应
                break;
            case LiveConstant.VideoResolutionType.VIDEO_RESOLUTION_TYPE_540_960:
                pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION);
                config.setVideoBitrate(1000);
                config.setHardwareAcceleration(false);
                config.setAutoAdjustBitrate(false);
                break;
            case LiveConstant.VideoResolutionType.VIDEO_RESOLUTION_TYPE_720_1280:
                pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION);
                config.setVideoBitrate(1500);
                config.setHardwareAcceleration(true);
                config.setAutoAdjustBitrate(false);
                break;
            default:
                pusher.setVideoQuality(TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION);
                config.setVideoBitrate(700);
                config.setHardwareAcceleration(false);
                config.setAutoAdjustBitrate(false);
                break;
        }

        updateConfig();
        LogUtil.i("setConfigDefault");
    }

    public void updateConfig()
    {
        if (pusher == null)
        {
            return;
        }
        pusher.setConfig(config);
    }

    /**
     * 设置美颜滤镜
     *
     * @param resId
     */
    public void setBeautyFilter(int resId)
    {
        if (pusher == null)
        {
            return;
        }

        if (resId == 0)
        {
            pusher.setFilter(null);
        } else
        {
            Resources resources = App.getApplication().getResources();
            TypedValue value = new TypedValue();
            resources.openRawResource(resId, value);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTargetDensity = value.density;
            Bitmap bmp = BitmapFactory.decodeResource(resources, resId, opts);
            pusher.setFilter(bmp);
        }
    }

    /**
     * 重新开始推流
     */
    public void restartPush()
    {
        stopPush(false);
        startPush(url);
    }

    /**
     * 开始推流
     */
    public void startPush(String url)
    {
        if (pusher == null)
        {
            return;
        }
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        if (isPushStarted)
        {
            return;
        }
        this.url = url;

        updateConfig();

        pusher.setPushListener(this);
        if (videoView != null)
        {
            pusher.startCameraPreview(videoView);
        }
        pusher.startPusher(url);

        cameraId = CAMERA_FRONT;
        isPushPaused = false;
        isPushStarted = true;
        LogUtil.i("startPush:" + url);
    }

    /**
     * 暂停推流
     */
    public void pausePush()
    {
        if (pusher == null)
        {
            return;
        }
        if (videoView != null)
        {
            videoView.onPause();
        }

        if (isPushStarted)
        {
            pusher.stopCameraPreview(false);
            pusher.pausePusher();
            isPushPaused = true;
            LogUtil.i("pausePush");
        }
    }

    /**
     * 恢复推流
     */
    public void resumePush()
    {
        if (pusher == null)
        {
            return;
        }
        if (videoView != null)
        {
            videoView.onResume();
        }

        if (isPushStarted)
        {
            if (videoView != null)
            {
                pusher.startCameraPreview(videoView);
            }
            pusher.resumePusher();
            isPushPaused = false;
            LogUtil.i("resumePush");
        }
    }

    /**
     * 停止推流
     */
    public void stopPush(boolean clearLastFrame)
    {
        if (pusher == null)
        {
            return;
        }
        if (!isPushStarted)
        {
            return;
        }

        pusher.stopCameraPreview(clearLastFrame);
        pusher.setPushListener(null);
        pusher.stopPusher();

        cameraId = CAMERA_NONE;
        isPushPaused = false;
        isPushStarted = false;
        LogUtil.i("stopPush");
    }

    /**
     * 设置镜像
     *
     * @param mirror
     */
    public void setMirror(boolean mirror)
    {
        this.isMirror = mirror;
        if (isPushStarted())
        {
            restartPush();
        }
    }

    /**
     * 设置视频码率
     *
     * @param bitrate
     */
    public void setVideoBitrate(int bitrate)
    {
        config.setVideoBitrate(bitrate);
        updateConfig();
    }

    /**
     * 设置最大视频码率
     *
     * @param bitrate
     */
    public void setMaxVideoBitrate(int bitrate)
    {
        config.setMaxVideoBitrate(bitrate);
        updateConfig();
    }

    /**
     * 开关闪关灯
     *
     * @param enable
     */
    public void enableFlashLight(boolean enable)
    {
        if (pusher == null)
        {
            return;
        }
        pusher.turnOnFlashLight(enable);
    }

    /**
     * 开关麦克风
     *
     * @param enable
     */
    public void enableMic(boolean enable)
    {
        if (pusher == null)
        {
            return;
        }
        pusher.setMute(!enable);
    }

    /**
     * 真实值转换
     *
     * @param progress [0-100]
     * @return [0-10]
     */
    public static int getRealValue(int progress)
    {
        float value = ((float) progress / 100) * 10;
        return (int) value;
    }

    /**
     * 设置美颜
     *
     * @param progress [0-100]
     */
    public void inputBeauty(int progress)
    {
        if (pusher == null)
        {
            return;
        }
        this.beautyValue = getRealValue(progress);
        pusher.setBeautyFilter(beautyValue, whiteValue);
    }

    /**
     * 设置美白
     *
     * @param progress [0-100]
     */
    public void inputWhite(int progress)
    {
        if (pusher == null)
        {
            return;
        }
        this.whiteValue = getRealValue(progress);
        pusher.setBeautyFilter(beautyValue, whiteValue);
    }

    /**
     * 开关美颜
     *
     * @param enable
     */
    public void enableBeauty(boolean enable)
    {
        int progress = 0;
        if (enable)
        {
            progress = LiveBeautyConfig.get().getBeautyProgress();
        } else
        {
            progress = 0;
        }
        inputBeauty(progress);
    }

    /**
     * 开关美白
     *
     * @param enable
     */
    public void enableWhite(boolean enable)
    {
        int progress = 0;
        if (enable)
        {
            progress = LiveBeautyConfig.get().getWhiteProgress();
        } else
        {
            progress = 0;
        }
        inputWhite(progress);
    }

    /**
     * 开关滤镜
     *
     * @param enable
     */
    public void enableBeautyFilter(boolean enable)
    {
        int filterResId = 0;
        if (enable)
        {
            filterResId = LiveBeautyConfig.get().getFilterResId();
        } else
        {
            filterResId = 0;
        }
        setBeautyFilter(filterResId);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera()
    {
        if (pusher == null)
        {
            return;
        }
        pusher.switchCamera();
        if (cameraId == CAMERA_FRONT)
        {
            cameraId = CAMERA_BACK;
        } else if (cameraId == CAMERA_BACK)
        {
            cameraId = CAMERA_FRONT;
        }
    }

    /**
     * 当前是否是后置摄像头
     *
     * @return
     */
    public boolean isBackCamera()
    {
        return cameraId == CAMERA_BACK;
    }

    /**
     * 设置最小视频码率
     *
     * @param bitrate
     */
    public void setMinVideoBitrate(int bitrate)
    {
        config.setMinVideoBitrate(bitrate);
        updateConfig();
    }

    public LiveQualityData getLiveQualityData()
    {
        return this.liveQualityData;
    }

    /**
     * 播放背景音乐
     *
     * @param path
     * @return
     */
    public boolean playBGM(String path)
    {
        if (pusher == null)
        {
            return false;
        }
        boolean result = pusher.playBGM(path);
        isBGMPlaying = result;
        isBGMStarted = result;
        return result;
    }

    /**
     * 停止背景音乐
     *
     * @return
     */
    public boolean stopBGM()
    {
        if (pusher == null)
        {
            return false;
        }
        boolean result = pusher.stopBGM();
        isBGMPlaying = !result;
        isBGMStarted = !result;
        return result;
    }

    /**
     * 暂停背景音乐
     *
     * @return
     */
    public boolean pauseBGM()
    {
        if (pusher == null)
        {
            return false;
        }
        boolean result = pusher.pauseBGM();
        if (result)
        {
            isBGMPlaying = false;
        }
        return result;
    }

    /**
     * 恢复背景音乐
     *
     * @return
     */
    public boolean resumeBGM()
    {
        if (pusher == null)
        {
            return false;
        }
        boolean result = pusher.resumeBGM();
        if (result)
        {
            isBGMPlaying = true;
        }
        return result;
    }

    /**
     * 设置背景音乐音量
     *
     * @param progress [0-100]
     */
    public void setBGMVolume(int progress)
    {
        if (pusher == null)
        {
            return;
        }
        float realValue = progress / 50.0f;
        pusher.setBGMVolume(realValue);
    }

    /**
     * 设置麦克风音量
     *
     * @param progress [0-100]
     */
    public void setMicVolume(int progress)
    {
        if (pusher == null)
        {
            return;
        }
        float realValue = progress / 50.0f;
        pusher.setMicVolume(realValue);
    }

    /**
     * 设置背景音乐播放监听
     */
    public void setBGMNofify(TXLivePusher.OnBGMNotify listener)
    {
        if (pusher == null)
        {
            return;
        }
        pusher.setBGMNofify(listener);
    }

    /**
     * 销毁推流
     */
    public void onDestroy()
    {
        if (videoView != null)
        {
            videoView.onDestroy();
        }
        pushRunnable.removeDelay();
        stopPush(true);
        stopBGM();
        this.videoView = null;
        this.pushListener = null;
        this.url = null;
        this.config.setPauseImg(null);
        this.pusher = null;
    }

    private SDDelayRunnable pushRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            LogUtil.i("pushRunnable run delayed...");
            startPush(url);
        }
    };

    @Override
    public void onPushEvent(int event, Bundle bundle)
    {
        LogUtil.i("onPushEvent:" + event);
        switch (event)
        {
            case TXLiveConstants.PUSH_ERR_NET_DISCONNECT:
                stopPush(false);
                pushRunnable.runDelay(3000);
                break;
            case TXLiveConstants.PUSH_EVT_PUSH_BEGIN:
                pusher.setMirror(isMirror);
                break;

            default:
                break;
        }


        if (pushListener != null)
        {
            pushListener.onPushEvent(event, bundle);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle)
    {
        this.liveQualityData.parseBundle(bundle, true);
        if (pushListener != null)
        {
            pushListener.onNetStatus(bundle);
        }
    }
}
