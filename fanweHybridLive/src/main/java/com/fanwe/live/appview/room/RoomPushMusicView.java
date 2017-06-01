package com.fanwe.live.appview.room;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.live.R;
import com.fanwe.live.appview.LivePlayMusicView;
import com.fanwe.live.control.LivePushSDK;
import com.fanwe.live.dialog.PushMusicEffectDialog;
import com.fanwe.live.music.effect.PlayCtrl;
import com.tencent.rtmp.TXLivePusher;

/**
 * 直播sdk播放音乐view
 */
public class RoomPushMusicView extends ARoomMusicView implements TXLivePusher.OnBGMNotify
{
    private LivePushSDK pushSDK;
    private PushMusicEffectDialog effectDialog;
    private long currentBGMPosition;

    public RoomPushMusicView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public RoomPushMusicView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoomPushMusicView(Context context)
    {
        super(context);
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_room_push_play_music);
        setPlayMusicView((LivePlayMusicView) findViewById(R.id.view_play_music));

        pushSDK = LivePushSDK.getInstance();
        pushSDK.setBGMNofify(this);
        updateEffectValue(getPlayCtrl());
    }

    @Override
    protected void updateEffectValue(PlayCtrl playCtrl)
    {
        pushSDK.setBGMVolume(playCtrl.bzVol);
        pushSDK.setMicVolume(playCtrl.micVol);
    }

    @Override
    public boolean isMusicStarted()
    {
        return pushSDK.isBGMStarted();
    }

    @Override
    public boolean isMusicPlaying()
    {
        return pushSDK.isBGMPlaying();
    }

    @Override
    public boolean playMusic(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return false;
        }

        boolean result = pushSDK.playBGM(path);
        updateViewState();
        return result;
    }

    @Override
    public boolean pauseMusic()
    {
        boolean result = pushSDK.pauseBGM();
        updateViewState();
        return result;
    }

    @Override
    public boolean resumeMusic()
    {
        boolean result = pushSDK.resumeBGM();
        updateViewState();
        return result;
    }

    @Override
    public boolean stopMusic()
    {
        boolean result = pushSDK.stopBGM();
        updateViewState();
        return result;
    }

    @Override
    public long getMusicPosition()
    {
        return currentBGMPosition;
    }

    @Override
    public void showEffectDialog()
    {
        if (effectDialog == null)
        {
            effectDialog = new PushMusicEffectDialog(getActivity());
            effectDialog.setPlayCtrlListener(this);
        }
        effectDialog.showBottom();
    }

    @Override
    public void hideEffectDialog()
    {
        if (effectDialog != null)
        {
            effectDialog.dismiss();
        }
    }

    @Override
    public void onBGMStart()
    {
    }

    @Override
    public void onBGMProgress(long current, long total)
    {
        currentBGMPosition = current;
    }

    @Override
    public void onBGMComplete(int i)
    {
        currentBGMPosition = 0;
        SDHandlerManager.getMainHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                stopMusic();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        pushSDK.setBGMNofify(null);
    }
}
