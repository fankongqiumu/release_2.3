package com.librarygames.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
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

public class GameResultTextView extends BaseGamesView {

    private ImageView iv_bg;
    private ImageView iv_text;
    private FrameLayout fl_content;
    private OnAnimatorListener onAnimatorListener;
    private AnimatorSet animatorSet;

    public GameResultTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GameResultTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameResultTextView(Context context) {
        super(context);
    }

    @Override
    protected int onCreateContentView() {
        return R.layout.view_poker_result_text;
    }

    @Override
    protected void baseConstructorInit() {
        iv_bg = find(R.id.iv_bg);
        iv_text = find(R.id.iv_text);
        fl_content = find(R.id.fl_content);
        SDViewUtil.hide(fl_content);

        Animator[] animators1 = GamesAnimationUtil.scaleView(iv_bg, 150, 0, 1.2f);
        Animator[] animators2 = GamesAnimationUtil.scaleView(iv_bg, 150, 1.2f, 1);
        Animator[] animators3 = GamesAnimationUtil.scaleView(iv_text, 300, 0, 0, 12f);
        Animator[] animators4 = GamesAnimationUtil.scaleView(iv_text, 150, 13, 9);

        animatorSet = new AnimatorSet();
        animatorSet.play(animators1[0]).with(animators1[1]).before(animators2[0]).before(animators2[1]);
        animatorSet.play(animators1[0]).with(animators3[0]);
        animatorSet.play(animators3[0]).with(animators3[1]).before(animators4[0]).before(animators4[1]);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onAnimatorListener != null) {
                    onAnimatorListener.onFinish();
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

    private void startAnimator() {
        cancel();
        SDViewUtil.show(fl_content);
        animatorSet.start();
    }

    private void cancel() {
        if(animatorSet.isRunning()) {
            animatorSet.cancel();
        }
    }

    public void setType(@GameConstant.GameType int gameType, int type) {
        switch(gameType) {
            case GameConstant.POKER_GOLDFLOWER :
                iv_text.setImageResource(GamesResUtil.getGlodFlowerType(type));
                break;
            case GameConstant.POKER_BULL :
                iv_text.setImageResource(GamesResUtil.getBullType(type));
                break;
            default:
                break;
        }

    }

    public void start() {
        startAnimator();
    }


    public void end() {
        cancel();
        SDViewUtil.hide(fl_content);
    }

    public interface OnAnimatorListener {
        public void onFinish();
    }

    public void setAnimatorListener(OnAnimatorListener onAnimatorListener) {
        this.onAnimatorListener = onAnimatorListener;
    }
}
