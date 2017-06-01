package com.fanwe.live.appview;

import android.content.Context;
import android.util.AttributeSet;

import com.fanwe.live.control.LivePlayerSDK;
import com.fanwe.live.control.PlayerListenerWrapper;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by Administrator on 2017/1/17.
 */

public class LiveVideoView extends TXCloudVideoView
{
    private LivePlayerSDK playerSDK;

    public LiveVideoView(Context context)
    {
        super(context);
        init();
    }

    public LiveVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init();
    }

    private void init()
    {

    }

    public void setPlayerListener(LivePlayerSDK.PlayerListener playerListener)
    {
        playerListenerWrapper.setPlayerListener(playerListener);
    }

    public LivePlayerSDK getPlayer()
    {
        if (playerSDK == null)
        {
            playerSDK = new LivePlayerSDK();
            playerSDK.init(this);
            playerSDK.setPlayerListener(playerListenerWrapper);
        }
        return playerSDK;
    }

    private PlayerListenerWrapper playerListenerWrapper = new PlayerListenerWrapper()
    {

    };

}
