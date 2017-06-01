package com.fanwe.live.appview;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.control.LivePlayerSDK;
import com.fanwe.live.control.PlayerListenerWrapper;

/**
 * Created by Administrator on 2017/1/19.
 */

public class LiveVideoExtendView extends BaseAppView
{
    private LiveVideoView view_video;
    private ProgressBar pgb_progress;
    private boolean isSmallMode;
    private String userId;

    public LiveVideoExtendView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LiveVideoExtendView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LiveVideoExtendView(Context context)
    {
        super(context);
        init();
    }

    public void setPlayerListener(LivePlayerSDK.PlayerListener playerListener)
    {
        playerListenerWrapper.setPlayerListener(playerListener);
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_live_video_extend);

        pgb_progress = find(R.id.pgb_progress);
        view_video = find(R.id.view_video);
        view_video.setPlayerListener(playerListenerWrapper);
    }

    public void setVideo(String url, int videoType, String userId)
    {
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        setUserId(userId);
        getPlayer().setPlayType(videoType);
        getPlayer().setUrl(url);
    }

    public void playVideo()
    {
        if (TextUtils.isEmpty(getPlayer().getUrl()))
        {
            return;
        }

        showProgress();
        getPlayer().startPlay();
    }

    public void pauseVideo()
    {
        getPlayer().pause();
    }

    public void resumeVideo()
    {
        getPlayer().resume();
    }

    public void stopVideo()
    {
        getPlayer().stopPlay();
        hideProgress();
    }

    public void destroyVideo()
    {
        getPlayer().onDestroy();
        hideProgress();
    }

    public void setSmallMode(boolean smallMode)
    {
        isSmallMode = smallMode;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }

    public LiveVideoView getVideoView()
    {
        return view_video;
    }

    public LivePlayerSDK getPlayer()
    {
        return view_video.getPlayer();
    }

    public void showProgress()
    {
        SDViewUtil.show(pgb_progress);
    }

    public void hideProgress()
    {
        SDViewUtil.hide(pgb_progress);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (isSmallMode)
        {
            if (changed)
            {
                View parent = (View) getParent();
                if (parent != null)
                {
                    int totalHeight = parent.getHeight();
                    if (totalHeight > 0)
                    {
                        int height = totalHeight / 4;
                        int width = SDViewUtil.getScaleWidth(3, 4, height);
                        SDViewUtil.setViewWidthHeight(this, width, height);
                    }
                }
            }
        }
    }

    private PlayerListenerWrapper playerListenerWrapper = new PlayerListenerWrapper()
    {
        @Override
        public void onPlayRecvFirstFrame(int event, Bundle param)
        {
            hideProgress();
            super.onPlayRecvFirstFrame(event, param);
        }

        @Override
        public void onPlayProgress(int event, Bundle param, int total, int progress)
        {
            hideProgress();
            super.onPlayProgress(event, param, total, progress);
        }

        @Override
        public void onPlayLoading(int event, Bundle param)
        {
            showProgress();
            super.onPlayLoading(event, param);
        }
    };
}
