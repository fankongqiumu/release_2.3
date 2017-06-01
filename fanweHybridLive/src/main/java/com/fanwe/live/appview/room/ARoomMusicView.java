package com.fanwe.live.appview.room;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDCache;
import com.fanwe.library.utils.SDThread;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.appview.LivePlayMusicView;
import com.fanwe.live.event.EPlayMusic;
import com.fanwe.live.model.LiveSongModel;
import com.fanwe.live.music.effect.MusicEffectDialog;
import com.fanwe.live.music.effect.PlayCtrl;
import com.fanwe.live.music.lrc.LrcParser;
import com.fanwe.live.music.lrc.LrcRow;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */

public abstract class ARoomMusicView extends RoomView implements LivePlayMusicView.ClickListener, MusicEffectDialog.PlayCtrlListener
{

    private LivePlayMusicView view_play_music;
    private SDThread lrcThread;
    private PlayCtrl playCtrl;
    private LiveSongModel songModel;

    /**
     * view是否已经被关闭
     */
    private boolean isViewClosed;

    private boolean isNeedDoLifeCircle;
    private boolean isMusicPlayingOnStop;

    public ARoomMusicView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public ARoomMusicView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ARoomMusicView(Context context)
    {
        super(context);
        init();
    }

    @Override
    protected void init()
    {
        super.init();
        initPlayCtrl();
        invisible();
    }

    public PlayCtrl getPlayCtrl()
    {
        return playCtrl;
    }

    private void initPlayCtrl()
    {
        playCtrl = SDCache.getObject(PlayCtrl.class);
        if (playCtrl == null)
        {
            playCtrl = new PlayCtrl();
            SDCache.setObject(playCtrl);
        }
    }

    @Override
    public void onPlayCtrl(PlayCtrl playCtrl)
    {
        this.playCtrl = playCtrl;
        updateEffectValue(playCtrl);
    }

    /**
     * 更新音效参数，音量
     */
    protected abstract void updateEffectValue(PlayCtrl playCtrl);


    /**
     * 更新播放暂停按钮
     */
    protected void updateViewState()
    {
        if (isMusicPlaying())
        {
            view_play_music.setImageMusicControlPause();
        } else
        {
            view_play_music.setImageMusicControlPlay();
        }
    }

    public LivePlayMusicView getPlayMusicView()
    {
        return view_play_music;
    }

    public void setPlayMusicView(LivePlayMusicView view)
    {
        this.view_play_music = view;
        if (view != null)
        {
            view.setClickListener(this);
        }
    }

    /**
     * 收到选择音乐事件
     */
    public void onEventMainThread(EPlayMusic event)
    {
        LiveSongModel model = event.songModel;
        if (model == null)
        {
            return;
        }
        playMusic(model);
    }

    public void playMusic(LiveSongModel songModel)
    {
        this.songModel = songModel;
        startPlayMusic();
        isNeedDoLifeCircle = false;
    }

    private void startPlayMusic()
    {
        if (songModel == null)
        {
            return;
        }
        stopMusic();

        if (playMusic(songModel.getMusicPath()))
        {
            decodeLrcByPath(songModel.getLrcPath());
            showMusicView();
        }
    }

    /**
     * 解析歌词
     *
     * @param path 本地路径
     */
    private void decodeLrcByPath(String path)
    {
        List<LrcRow> list = LrcParser.parseLrcRows(path);
        view_play_music.getLrc_view().setLrcRows(list);
        getLrcThread().resumeThread();
    }

    /**
     * 获得歌词渲染线程
     */
    protected SDThread getLrcThread()
    {
        if (lrcThread == null)
        {
            lrcThread = new SDThread()
            {
                @Override
                protected void runBackground() throws Exception
                {
                    LogUtil.i("LrcThread running");
                    view_play_music.getLrc_view().changeCurrent(getMusicPosition());
                    sleep(30);
                }

                @Override
                protected void onPause()
                {
                    LogUtil.i("LrcThread pause");
                    super.onPause();
                }
            };
            lrcThread.startLoop();
        }
        return lrcThread;
    }

    public void onStopLifeCircle()
    {
        isNeedDoLifeCircle = isMusicStarted();
        if (isNeedDoLifeCircle)
        {
            isMusicPlayingOnStop = isMusicPlaying();
            pauseMusic();
        }
    }

    public void onResumeLifeCircle()
    {
        if (isNeedDoLifeCircle)
        {
            if (isMusicPlayingOnStop)
            {
                resumeMusic();
            } else
            {
                pauseMusic();
            }
        }
    }

    /**
     * 音乐是否已经开始播放
     */
    public abstract boolean isMusicStarted();

    /**
     * 音乐是否在播放
     */
    public abstract boolean isMusicPlaying();

    /**
     * 播放音乐
     *
     * @param path 本地sd卡路径
     */
    public abstract boolean playMusic(String path);

    /**
     * 暂停音乐
     */
    public abstract boolean pauseMusic();

    /**
     * 恢复音乐
     */
    public abstract boolean resumeMusic();

    /**
     * 停止音乐
     */
    public abstract boolean stopMusic();

    /**
     * 获得当前音乐的播放位置（毫秒）
     */
    public abstract long getMusicPosition();

    /**
     * 显示音效设置窗口
     */
    public abstract void showEffectDialog();

    /**
     * 隐藏音效设置窗口
     */
    public abstract void hideEffectDialog();

    @Override
    public void onClickEffect(View v)
    {
        showEffectDialog();
    }


    @Override
    public void onClickClose(View v)
    {
        closeMusicView();
    }

    /**
     * 显示音乐view
     */
    protected void showMusicView()
    {
        show();
        isViewClosed = false;
    }

    /**
     * 关闭音乐view
     */
    public void closeMusicView()
    {
        if (SDViewUtil.isMainThread())
        {
            closeMusicRunnable.run();
        } else
        {
            SDHandlerManager.getMainHandler().post(closeMusicRunnable);
        }
    }

    private Runnable closeMusicRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopMusic();
            getLrcThread().pauseThread();
            invisible();
            songModel = null;
            isViewClosed = true;
        }
    };

    @Override
    public void onClickMusicControl(View v)
    {
        if (isMusicStarted())
        {
            if (isMusicPlaying())
            {
                pauseMusic();
            } else
            {
                resumeMusic();
            }
        } else
        {
            startPlayMusic();
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        try
        {
            if (getLrcThread() != null)
            {
                getLrcThread().stopThread();
            }
            stopMusic();
            hideEffectDialog();
        } catch (Exception e)
        {
        }
    }
}
