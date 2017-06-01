package com.fanwe.live.control;

import android.os.Bundle;
import android.text.TextUtils;

import com.fanwe.hybrid.app.App;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.model.LiveQualityData;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by Administrator on 2016/7/25.
 */
public class LivePlayerSDK implements ITXLivePlayListener
{
    private TXLivePlayer player;
    private TXCloudVideoView videoView;

    private TXLivePlayConfig config;
    /**
     * 播放链接
     */
    private String url;
    /**
     * 播放类型
     */
    private int playType;
    /**
     * 总的播放时间(秒)
     */
    private int total;
    /**
     * 当前播放的进度(秒)
     */
    private int progress;
    /**
     * 是否已经开始播放
     */
    private boolean isPlayerStarted = false;
    /**
     * 是否暂停
     */
    private boolean isPaused = false;
    private PlayerListener playerListener;
    private LiveQualityData liveQualityData;


    public LivePlayerSDK()
    {
        liveQualityData = new LiveQualityData();
        player = new TXLivePlayer(App.getApplication());
        config = new TXLivePlayConfig();
    }

    public LiveQualityData getLiveQualityData()
    {
        return liveQualityData;
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    /**
     * 初始化
     *
     * @param videoView
     */
    public void init(TXCloudVideoView videoView)
    {
        this.videoView = videoView;
        player.setPlayerView(videoView);

        setRenderModeFill();
        setRenderRotationPortrait();
        enableHardwareDecode(true);
    }

    public TXLivePlayConfig getConfig()
    {
        return config;
    }

    public void updatePlayerConfig()
    {
        player.setConfig(config);
    }

    /**
     * 是否正在播放中
     */
    public boolean isPlaying()
    {
        return isPlayerStarted && !isPaused;
    }

    /**
     * 是否已经开始播放
     */
    public boolean isPlayerStarted()
    {
        return isPlayerStarted;
    }

    public boolean enableHardwareDecode(boolean enable)
    {
        return player.enableHardwareDecode(enable);
    }

    /**
     * 全屏渲染
     */
    public void setRenderModeFill()
    {
        player.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
    }

    /**
     * 按分辨率渲染
     */
    public void setRenderModeAdjustResolution()
    {
        player.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
    }

    /**
     * 竖屏
     */
    public void setRenderRotationPortrait()
    {
        player.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
    }

    /**
     * 横屏
     */
    public void setRenderRotationLandscape()
    {
        player.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
    }

    /**
     * 设置静音
     *
     * @param mute true-静音
     */
    public void setMute(boolean mute)
    {
        player.setMute(mute);
    }

    /**
     * 获得总的播放时间(秒)
     *
     * @return
     */
    public int getTotal()
    {
        return total;
    }

    /**
     * 获得当前播放的进度(秒)
     *
     * @return
     */
    public int getProgress()
    {
        return progress;
    }

    public void setPlayerListener(PlayerListener playerListener)
    {
        this.playerListener = playerListener;
    }

    /**
     * 设置播放地址
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * 设置点播地址
     *
     * @param url
     * @return
     */
    public boolean setVodUrl(String url)
    {
        if (TextUtils.isEmpty(url))
        {
            return false;
        }

        if (url.startsWith("http://") || url.startsWith("https://"))
        {
            if (url.endsWith(".flv"))
            {
                setPlayType(TXLivePlayer.PLAY_TYPE_VOD_FLV);
            } else if (url.endsWith(".m3u8"))
            {
                setPlayType(TXLivePlayer.PLAY_TYPE_VOD_HLS);
            } else if (url.endsWith(".mp4"))
            {
                setPlayType(TXLivePlayer.PLAY_TYPE_VOD_MP4);
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }

        setUrl(url);
        return true;
    }

    public String getUrl()
    {
        return url;
    }

    /**
     * 设置播放类型 flv,mp4等。。。
     *
     * @param playType TXLivePlayer.PLAY_TYPE_XXXXXXX
     */
    public void setPlayType(int playType)
    {
        this.playType = playType;
        if (TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC == playType)
        {
            //快速拉流模式
            config.enableAEC(true);
            config.setAutoAdjustCacheTime(true);
            config.setMaxAutoAdjustCacheTime(0.2f);
            config.setMinAutoAdjustCacheTime(0.2f);
        } else
        {
            //普通模式
            config.enableAEC(false);
            config.setAutoAdjustCacheTime(true);
            config.setMaxAutoAdjustCacheTime(5.0f);
            config.setMinAutoAdjustCacheTime(1.0f);
        }
    }

    public void restartPlay()
    {
        stopPlay();
        startPlay();
    }

    private SDDelayRunnable playRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            LogUtil.i("playRunnable run delayed...");
            startPlay();
        }
    };

    public void performPlay()
    {
        if (isPlayerStarted)
        {
            if (!isPaused)
            {
                pause();
            } else
            {
                resume();
            }
        } else
        {
            startPlay();
        }
    }

    private boolean canPlay()
    {
        if (TextUtils.isEmpty(url))
        {
            return false;
        }
        return true;
    }

    /**
     * 开始播放
     */
    public void startPlay()
    {
        if (!canPlay())
        {
            return;
        }
        if (isPlayerStarted)
        {
            return;
        }

        updatePlayerConfig();
        player.setPlayListener(this);
        player.startPlay(url, playType);
        isPlayerStarted = true;
        LogUtil.i("startPlay:" + url);
    }

    /**
     * 暂停
     */
    public void pause()
    {
        if (isPlayerStarted)
        {
            if (!isPaused)
            {
                if (videoView != null)
                {
                    videoView.onPause();
                }
                player.pause();

                isPaused = true;
            }
        }
    }

    /**
     * 恢复播放
     */
    public void resume()
    {
        if (isPlayerStarted)
        {
            if (isPaused)
            {
                if (videoView != null)
                {
                    videoView.onResume();
                }
                player.resume();

                isPaused = false;
            }
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay()
    {
        stopPlay(false);
    }

    private void stopPlay(boolean clearLastFrame)
    {
        if (isPlayerStarted)
        {
            player.setPlayListener(null);
            player.stopPlay(clearLastFrame);
            resetState();
            LogUtil.i("stopPlay");
        }
    }

    /**
     * 重置状态
     */
    private void resetState()
    {
        isPlayerStarted = false;
        isPaused = false;
    }

    public void seek(int time)
    {
        player.seek(time);
    }

    @Override
    public void onPlayEvent(int event, Bundle param)
    {
        if (playerListener != null)
        {
            playerListener.onPlayEvent(event, param);
        }

        switch (event)
        {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                LogUtil.i("PLAY_EVT_PLAY_BEGIN");
                if (playerListener != null)
                {
                    playerListener.onPlayBegin(event, param);
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                LogUtil.i("PLAY_EVT_RCV_FIRST_I_FRAME");
                if (playerListener != null)
                {
                    playerListener.onPlayRecvFirstFrame(event, param);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                total = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);

                LogUtil.i("PLAY_EVT_PLAY_PROGRESS:" + progress + "," + total);
                if (playerListener != null)
                {
                    playerListener.onPlayProgress(event, param, total, progress);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                LogUtil.i("PLAY_EVT_PLAY_END");
                stopPlay();
                if (playerListener != null)
                {
                    playerListener.onPlayEnd(event, param);
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                LogUtil.i("PLAY_EVT_PLAY_LOADING");
                if (playerListener != null)
                {
                    playerListener.onPlayLoading(event, param);
                }
                break;

            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                LogUtil.i("PLAY_ERR_NET_DISCONNECT");
                stopPlay(false);
                playRunnable.runDelay(3000);
                break;
            default:
                LogUtil.i("PLAY_EVENT:" + event);
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle)
    {
        this.liveQualityData.parseBundle(bundle, false);
        if (playerListener != null)
        {
            playerListener.onNetStatus(bundle);
        }
    }

    /**
     * 销毁当前播放
     */
    public void onDestroy()
    {
        if (videoView != null)
        {
            videoView.onDestroy();
        }
        stopPlay(true);
        playRunnable.removeDelay();
        videoView = null;
    }

    public interface PlayerListener
    {
        void onPlayEvent(int event, Bundle param);

        void onPlayBegin(int event, Bundle param);

        void onPlayRecvFirstFrame(int event, Bundle param);

        void onPlayProgress(int event, Bundle param, int total, int progress);

        void onPlayEnd(int event, Bundle param);

        void onPlayLoading(int event, Bundle param);

        void onNetStatus(Bundle param);
    }
}
