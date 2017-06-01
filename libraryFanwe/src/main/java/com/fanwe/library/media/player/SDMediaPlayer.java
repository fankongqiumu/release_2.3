package com.fanwe.library.media.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;

import com.fanwe.library.looper.SDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;

public class SDMediaPlayer
{
    private static SDMediaPlayer instance;

    private MediaPlayer player;
    private State state = State.Idle;
    private String dataFilePath;
    private String dataUrl;
    private int dataRawResId;
    private boolean hasInitialized;
    private SDLooper looper;

    private PlayerListener listener;
    private ProgressListener progressListener;

    private SDMediaPlayer()
    {
        init();
    }

    public static SDMediaPlayer getInstance()
    {
        if (instance == null)
        {
            instance = new SDMediaPlayer();
        }
        return instance;
    }

    /**
     * 初始化播放器，调用release()后如果想要继续使用，要调用此方法初始化
     */
    public void init()
    {
        if (player != null)
        {
            release();
        }
        player = new MediaPlayer();
        player.setOnErrorListener(onErrorListener);
        player.setOnPreparedListener(onPreparedListener);
        player.setOnCompletionListener(onCompletionListener);
    }

    private void startLooper()
    {
        if (looper == null)
        {
            looper = new SDSimpleLooper();
        }

        looper.start(300, looperRunnable);
    }

    private void stopLooper()
    {
        if (looper != null)
        {
            looper.stop();
        }
    }

    private void notifyProgress()
    {
        if (progressListener != null)
        {
            progressListener.onProgress(player.getCurrentPosition(), player.getDuration());
        }
    }

    private Runnable looperRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (progressListener != null)
            {
                if (hasInitialized())
                {
                    notifyProgress();
                } else
                {
                    stopLooper();
                }
            }
        }
    };

    public State getState()
    {
        return state;
    }

    public void setListener(PlayerListener listener)
    {
        this.listener = listener;
    }

    public void setProgressListener(ProgressListener progressListener)
    {
        this.progressListener = progressListener;
    }

    public String getDataFilePath()
    {
        return dataFilePath;
    }

    public String getDataUrl()
    {
        return dataUrl;
    }

    public int getDataRawResId()
    {
        return dataRawResId;
    }

    @Deprecated
    public void playAudioFile(String path)
    {
        reset();
        try
        {
            player.setDataSource(path);
            this.dataFilePath = path;
            setState(State.Initialized);
            notifyInitialized();
            start();
        } catch (Exception e)
        {
            notifyException(e);
        }
    }

    @Deprecated
    public boolean isPlayingAudioFile(String path)
    {
        boolean result = false;
        if (isPlaying() && !TextUtils.isEmpty(path))
        {
            result = path.equals(dataFilePath);
        }
        return result;
    }

    @Deprecated
    public void performPlayAudioFile(String path)
    {
        if (!TextUtils.isEmpty(path))
        {
            if (isPlayingAudioFile(path))
            {
                stop();
            } else
            {
                playAudioFile(path);
            }
        }
    }

    public void seekTo(int position)
    {
        try
        {
            if (hasInitialized())
            {
                player.seekTo(position);
            }
        } catch (Exception e)
        {
            notifyException(e);
        }
    }

    public void performPlay()
    {
        performPlayInside(false);
    }

    public void performRestartPlay()
    {
        performPlayInside(true);
    }

    /**
     * 模拟暂停恢复播放
     *
     * @param restart true-恢复的时候重头播放
     */
    private void performPlayInside(boolean restart)
    {
        try
        {
            if (hasInitialized())
            {
                if (isPlaying())
                {
                    if (restart)
                    {
                        player.seekTo(0);
                    }
                    pause();
                } else
                {
                    start();
                }
            }
        } catch (Exception e)
        {
            notifyException(e);
        }
    }

    /**
     * 设置文件本地路径
     *
     * @param path
     */
    public boolean setDataFilePath(String path)
    {
        try
        {
            if (hasDataFilePath(path))
            {
                return true;
            }

            reset();
            player.setDataSource(path);
            this.dataFilePath = path;
            setState(State.Initialized);
            notifyInitialized();
            return true;
        } catch (Exception e)
        {
            notifyException(e);
            return false;
        }
    }

    /**
     * 设置文件网络链接
     *
     * @param url
     */
    public boolean setDataUrl(String url)
    {
        try
        {
            if (hasDataUrl(url))
            {
                return true;
            }

            reset();
            player.setDataSource(url);
            this.dataUrl = url;
            setState(State.Initialized);
            notifyInitialized();
            return true;
        } catch (Exception e)
        {
            notifyException(e);
            return false;
        }
    }

    /**
     * 设置文件rawResId
     *
     * @param rawResId
     * @param context
     */
    public boolean setDataRawResId(int rawResId, Context context)
    {
        try
        {
            if (hasDataRawResId(rawResId))
            {
                return true;
            }

            reset();
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(rawResId);
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            this.dataRawResId = rawResId;
            setState(State.Initialized);
            notifyInitialized();
            return true;
        } catch (Exception e)
        {
            notifyException(e);
            return false;
        }
    }

    /**
     * 当前已经设置的url是否和新的url一致
     *
     * @param url
     * @return
     */
    public boolean hasDataUrl(String url)
    {
        if (!TextUtils.isEmpty(dataUrl) && dataUrl.equals(url))
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 当前当前已经设置的文件路径是否和新的path一致
     *
     * @param path
     * @return
     */
    public boolean hasDataFilePath(String path)
    {
        if (!TextUtils.isEmpty(dataFilePath) && dataFilePath.equals(path))
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 当前当前已经设置的rawResId是否和新的rawResId一致
     *
     * @param rawResId
     * @return
     */
    public boolean hasDataRawResId(int rawResId)
    {
        if (this.dataRawResId != 0 && this.dataRawResId == rawResId)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * 是否播放状态
     *
     * @return
     */
    public boolean isPlaying()
    {
        return State.Playing == state;
    }

    /**
     * 是否暂停状态
     *
     * @return
     */
    public boolean isPaused()
    {
        return State.Paused == state;
    }

    /**
     * 是否已经初始化
     *
     * @return
     */
    public boolean hasInitialized()
    {
        return hasInitialized;
    }

    /**
     * 开始播放
     */
    public void start()
    {
        switch (state)
        {
            case Idle:

                break;
            case Initialized:
                prepareAsyncPlayer();
                break;
            case Preparing:

                break;
            case Prepared:
                startPlayer();
                break;
            case Playing:

                break;
            case Paused:
                startPlayer();
                break;
            case Completed:
                startPlayer();
                break;
            case Stopped:
                prepareAsyncPlayer();
                break;

            default:
                break;
        }
    }

    /**
     * 暂停播放
     */
    public void pause()
    {
        switch (state)
        {
            case Idle:

                break;
            case Initialized:

                break;
            case Preparing:

                break;
            case Prepared:

                break;
            case Playing:
                pausePlayer();
                break;
            case Paused:

                break;
            case Completed:

                break;
            case Stopped:

                break;

            default:
                break;
        }
    }

    /**
     * 停止播放
     */
    public void stop()
    {
        switch (state)
        {
            case Idle:

                break;
            case Initialized:

                break;
            case Preparing:

                break;
            case Prepared:
                stopPlayer();
                break;
            case Playing:
                stopPlayer();
                break;
            case Paused:
                stopPlayer();
                break;
            case Completed:
                stopPlayer();
                break;
            case Stopped:

                break;

            default:
                break;
        }
    }

    /**
     * 重置播放器，一般用于关闭播放界面的时候调用
     */
    public void reset()
    {
        stop();
        resetPlayer();
    }

    /**
     * 释放播放器，用于不再需要播放器的时候调用，调用此方法后，需要手动调用init()方法初始化后才可以使用
     */
    public void release()
    {
        stop();
        releasePlayer();
    }


    private void setState(State state)
    {
        this.state = state;
    }

    private void prepareAsyncPlayer()
    {
        setState(State.Preparing);
        notifyPreparing();
        player.prepareAsync();
    }

    private void startPlayer()
    {
        setState(State.Playing);
        notifyPlaying();
        player.start();

        startLooper();
    }

    private void pausePlayer()
    {
        setState(State.Paused);
        notifyPaused();
        player.pause();

        stopLooper();
    }

    private void stopPlayer()
    {
        setState(State.Stopped);
        notifyStopped();
        player.stop();

        stopLooper();
    }

    private void resetPlayer()
    {
        setState(State.Idle);
        notifyReset();
        player.reset();

        stopLooper();
        dataFilePath = null;
        dataUrl = null;
        dataRawResId = 0;
        hasInitialized = false;
    }

    private void releasePlayer()
    {
        setState(State.Released);
        notifyReleased();
        player.release();
    }

    // notify
    protected void notifyPreparing()
    {
        if (listener != null)
        {
            listener.onPreparing();
        }
    }

    protected void notifyPrepared()
    {
        if (listener != null)
        {
            listener.onPrepared();
        }
    }

    protected void notifyPlaying()
    {
        if (listener != null)
        {
            listener.onPlaying();
        }
    }

    protected void notifyPaused()
    {
        if (listener != null)
        {
            listener.onPaused();
        }
    }

    protected void notifyCompletion()
    {
        if (listener != null)
        {
            listener.onCompletion();
        }
    }

    protected void notifyStopped()
    {
        if (listener != null)
        {
            listener.onStopped();
        }
    }

    protected void notifyReset()
    {
        if (listener != null)
        {
            listener.onReset();
        }
    }

    protected void notifyInitialized()
    {
        hasInitialized = true;
        if (listener != null)
        {
            listener.onInitialized();
        }
    }

    protected void notifyReleased()
    {
        if (listener != null)
        {
            listener.onReleased();
        }
    }

    protected void notifyError(MediaPlayer mp, int what, int extra)
    {
        if (listener != null)
        {
            listener.onError(what, extra);
        }
    }

    protected void notifyException(Exception e)
    {
        if (listener != null)
        {
            listener.onException(e);
        }
    }

    // listener
    private OnErrorListener onErrorListener = new OnErrorListener()
    {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra)
        {
            resetPlayer();
            notifyError(mp, what, extra);
            return true;
        }
    };

    private OnPreparedListener onPreparedListener = new OnPreparedListener()
    {

        @Override
        public void onPrepared(MediaPlayer mp)
        {
            setState(State.Prepared);
            notifyPrepared();
            start();
        }
    };

    private OnCompletionListener onCompletionListener = new OnCompletionListener()
    {

        @Override
        public void onCompletion(MediaPlayer mp)
        {
            stopLooper();
            notifyProgress();

            setState(State.Completed);
            notifyCompletion();
        }
    };

    public enum State
    {
        /**
         * 已经释放资源
         */
        Released,
        /**
         * 空闲，还没设置dataSource
         */
        Idle,
        /**
         * 已经设置dataSource，还未播放
         */
        Initialized,
        /**
         * 准备中
         */
        Preparing,
        /**
         * 准备完毕
         */
        Prepared,
        /**
         * 已经启动播放
         */
        Playing,
        /**
         * 已经暂停播放
         */
        Paused,
        /**
         * 已经播放完毕
         */
        Completed,
        /**
         * 调用stop方法后的状态
         */
        Stopped;
    }

    public interface PlayerListener
    {
        void onReleased();

        void onReset();

        void onInitialized();

        void onPreparing();

        void onPrepared();

        void onPlaying();

        void onPaused();

        void onCompletion();

        void onStopped();

        void onError(int what, int extra);

        void onException(Exception e);
    }

    public static class SimplePlayerListener implements PlayerListener
    {
        @Override
        public void onReleased()
        {
        }

        @Override
        public void onReset()
        {
        }

        @Override
        public void onInitialized()
        {
        }

        @Override
        public void onPreparing()
        {
        }

        @Override
        public void onPrepared()
        {
        }

        @Override
        public void onPlaying()
        {
        }

        @Override
        public void onPaused()
        {
        }

        @Override
        public void onCompletion()
        {
        }

        @Override
        public void onStopped()
        {
        }

        @Override
        public void onError(int what, int extra)
        {
        }

        @Override
        public void onException(Exception e)
        {
        }
    }

    public interface ProgressListener
    {
        void onProgress(long current, long total);
    }
}
