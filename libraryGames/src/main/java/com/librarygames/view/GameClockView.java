package com.librarygames.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanwe.library.utils.SDViewBinder;
import com.librarygames.R;

/**
 * Created by luodong on 2016/11/17.
 */

public class GameClockView extends BaseGamesView {

    private TextView tv_time;
    public static long DEFAULT_TIME = 35;   //默认计数时间
    private long time;                      //计时标志位
    private long timeCount;                 //设置计时时间
    private int timeType;                   //倒计时 时间类型：0，默认计数时间;1,设置计数时间
    private CountDownTimer timer;           //计时工具
    private OnFinishListener onFinishListener;

    public GameClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GameClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameClockView(Context context) {
        super(context);
        setCountDownTimer(timeCount);
    }

    public GameClockView(Context context, long timeCount) {
        super(context);
        this.timeCount = timeCount;
        setCountDownTimer(timeCount);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.view_poker_clock;

    }

    @Override
    protected void baseConstructorInit() {
        tv_time = find(R.id.tv_time);

    }

    private void setCountDownTimer(final long timeCount) {
        if (timeCount == 0) {
            timeType=0;
            time = DEFAULT_TIME;//前后各有一秒看不到了所以+2
        } else {
            timeType=1;
            time = timeCount;
        }

        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time--;
                SDViewBinder.setTextView(tv_time, (millisUntilFinished/1000) + "", "等待");
            }

            @Override
            public void onFinish() {
                SDViewBinder.setTextView(tv_time, "0");
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                }
            }
        };
    }

    public void setTimeCount(long timeCount){
        cancel();
        timeType=1;
        this.timeCount = timeCount;
        setCountDownTimer(timeCount);
    }

    protected long getTime() {
        return this.time;
    }

    protected void cancel(){
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void invisible() {
        super.invisible();
        cancel();
    }

    public void start() {
        cancel();
        this.setVisibility(View.VISIBLE);
        switch (timeType){
            case 0:
                time = DEFAULT_TIME;
                break;
            case 1:
                time = timeCount;
                break;
            default:
                break;
        }

        timer.start();

    }

    public interface OnFinishListener {
        void onFinish();
    }

    public void setFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }
}
