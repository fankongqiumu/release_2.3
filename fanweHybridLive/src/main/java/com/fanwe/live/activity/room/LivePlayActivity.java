package com.fanwe.live.activity.room;

import android.os.Bundle;

import com.fanwe.live.appview.LiveVideoView;
import com.fanwe.live.control.LivePlayerSDK;
import com.fanwe.live.model.LiveQualityData;

/**
 * Created by Administrator on 2016/8/7.
 */
public class LivePlayActivity extends LiveLayoutViewerExtendActivity implements LivePlayerSDK.PlayerListener
{

    private LiveVideoView videoView;
    private boolean isPauseMode = false;

    public void setVideoView(LiveVideoView videoView)
    {
        this.videoView = videoView;

        this.videoView.setPlayerListener(this);
    }

    public LivePlayerSDK getPlayer()
    {
        if (videoView != null)
        {
            return videoView.getPlayer();
        }
        return null;
    }

    public LiveVideoView getVideoView()
    {
        return videoView;
    }

    public void setPauseMode(boolean pauseMode)
    {
        isPauseMode = pauseMode;
    }

    @Override
    public void onPlayEvent(int event, Bundle param)
    {

    }

    @Override
    public void onPlayBegin(int event, Bundle param)
    {

    }

    @Override
    public void onPlayRecvFirstFrame(int event, Bundle param)
    {

    }

    @Override
    public void onPlayProgress(int event, Bundle param, int total, int progress)
    {

    }

    @Override
    public void onPlayEnd(int event, Bundle param)
    {

    }

    @Override
    public void onPlayLoading(int event, Bundle param)
    {

    }

    @Override
    public void onNetStatus(Bundle param)
    {

    }

    @Override
    public LiveQualityData onLiveGetLiveQualityData()
    {
        return getPlayer().getLiveQualityData();
    }

    public boolean isPlaying()
    {
        if (getPlayer() != null)
        {
            return getPlayer().isPlaying();
        }
        return false;
    }

    @Override
    protected void onResume()
    {
        if (isPauseMode)
        {
            //暂停模式不处理
        } else
        {
            getPlayer().resume();
        }
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getPlayer().pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        getPlayer().onDestroy();
    }
}
