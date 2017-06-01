package com.librarygames.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.GameConstant;
import com.librarygames.R;
import com.librarygames.utils.GamesAnimationUtil;
import com.librarygames.utils.GamesResUtil;

/**
 * Created by luodong on 2016/11/18.
 */

public class GameToastView extends BaseGamesView {

    private ImageView iv_bg;
    private ImageView iv_text;
    private FrameLayout fl_content;
    private AnimatorSet mShowAnimatorSet;
    private AnimatorSet mHideAnimatorSet;

    private static final long DEFAULT_TIME = 2;   //默认计数时间
    private boolean isTimerShutAuto;       //是否自动关闭计时器
    private boolean isCreater;

    public GameToastView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GameToastView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameToastView(Context context) {
        super(context);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.view_poker_toast;
    }

    @Override
    protected void baseConstructorInit() {
        iv_bg = find(R.id.iv_bg);
        iv_text = find(R.id.iv_text);
        fl_content = find(R.id.fl_content);
//        SDViewUtil.hide(fl_content);

        initShowAnimatorSet();
        initHideAnimatorSet();
    }

    /**
     * 文字显示计时器
     */
    private CountDownTimer timer = new CountDownTimer(DEFAULT_TIME * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            hideAnimator();
        }
    };

    private void initShowAnimatorSet() {
        SDViewUtil.setViewWidth(fl_content, SDViewUtil.getScreenWidthPercent(0.9f));
        SDViewUtil.setViewWidth(iv_bg, SDViewUtil.getScreenWidthPercent(0.064f));
        SDViewUtil.setViewWidth(iv_text, SDViewUtil.getScreenWidthPercent(0.05f));
        SDViewUtil.setViewHeight(iv_bg, SDViewUtil.getScreenWidthPercent(0.15f));
        SDViewUtil.setViewHeight(iv_text, SDViewUtil.getScreenWidthPercent(0.008f));

        Animator[] animators1 = GamesAnimationUtil.scaleView(iv_bg, 300, 0, 12f);
        Animator[] animators2 = GamesAnimationUtil.scaleView(iv_bg, 150, 12f, 10);
        Animator[] animators3 = GamesAnimationUtil.scaleView(iv_text, 300, 0, 12f);
        Animator[] animators4 = GamesAnimationUtil.scaleView(iv_text, 150, 12f, 10);

        mShowAnimatorSet = new AnimatorSet();
        mShowAnimatorSet.play(animators1[0]).with(animators1[1]).before(animators2[0]).before(animators2[1]);
        mShowAnimatorSet.play(animators1[0]).with(animators3[0]);
        mShowAnimatorSet.play(animators3[0]).with(animators3[1]).before(animators4[0]).before(animators4[1]);

        mShowAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isTimerShutAuto) {
                    startTimer();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void initHideAnimatorSet() {

        Animator[] animators1 = GamesAnimationUtil.scaleView(iv_bg, 300, 12f, 0);
        Animator[] animators2 = GamesAnimationUtil.scaleView(iv_bg, 150, 10, 12f);
        Animator[] animators3 = GamesAnimationUtil.scaleView(iv_text, 300, 12f, 0);
        Animator[] animators4 = GamesAnimationUtil.scaleView(iv_text, 150, 10, 12f);

        mHideAnimatorSet = new AnimatorSet();
        mHideAnimatorSet.play(animators4[1]).with(animators4[0]).before(animators3[1]).before(animators3[0]);
        mHideAnimatorSet.play(animators4[1]).with(animators2[1]);
        mHideAnimatorSet.play(animators2[1]).with(animators2[0]).before(animators1[1]).before(animators1[0]);

        mHideAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SDViewUtil.hide(fl_content);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void setIsCreater(boolean isCreater) {
        this.isCreater = isCreater;
    }
    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer.start();
        }
    }

    private void startAnimator() {
        cancel();
        mShowAnimatorSet.start();
    }

    private void hideAnimator() {
        cancel();
        mHideAnimatorSet.start();
    }

    private void cancel() {
        mShowAnimatorSet.cancel();
        mHideAnimatorSet.cancel();
        timer.cancel();
    }

    /**
     * 开始播放 游戏状态对应的文字
     * @param status 游戏状态
     */
    public void start(int status) {
        iv_text.setImageResource(GamesResUtil.getGameToastTextResId(status));
        if(status == GameConstant.START || status == GameConstant.END) {
            isTimerShutAuto(false);//一直显示
        } else
            isTimerShutAuto(true);//自动关闭

        if(!(isCreater && status == GameConstant.BETABLE)) {

            SDViewUtil.show(fl_content);
            startAnimator();
        } else {
            Log.i("poker_start","主播不显示选择下注区域");
            SDViewUtil.hide(fl_content);
        }

    }

    public void end() {
        hideAnimator();
    }

    /**
     * 设置是否自动关闭提醒文字
     * @param isTimerShutAuto true 自动关闭
     */
    private void isTimerShutAuto(boolean isTimerShutAuto) {
        this.isTimerShutAuto = isTimerShutAuto;
    }

}
