package com.librarygames.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fanwe.library.dialog.SDDialogBase;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import com.librarygames.utils.GamesAnimationUtil;

/**
 * Created by Administrator on 2016/12/6.
 */

public class GamesWinnerDialog extends SDDialogBase {

    private TextView tv_gain;
    private ImageView iv_close;

    private ImageView iv_coin_1;//作为金币飞行动画对象
    private ImageView iv_coin_2;
    private ImageView iv_coin_3;

    private ViewTranslateRunnable mRunnableAnim;

    private AnimatorSet mAnimatorSet;

    private final long DEFAULT_CLOSE_DELAY = 5000;

    public GamesWinnerDialog(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_games_poker_win);
        tv_gain = (TextView) findViewById(R.id.tv_gain);
        iv_close = (ImageView) findViewById(R.id.iv_close);

        iv_coin_1 = (ImageView) findViewById(R.id.iv_coin_1);
        iv_coin_2 = (ImageView) findViewById(R.id.iv_coin_2);
        iv_coin_3 = (ImageView) findViewById(R.id.iv_coin_3);

        setFullScreen();
        setCanceledOnTouchOutside(true);
        iv_close.setOnClickListener(this);
    }

    public void setWinInfo(long money, @NonNull String unit) {
        SDViewBinder.setTextView(tv_gain, money + unit);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        dismiss();
    }

    @Override
    public void show() {
        super.show();
        startDismissRunnable(DEFAULT_CLOSE_DELAY);
    }

    private void playCoinAnimator(View coinView) {
        resetViews();
        iv_coin_1.setVisibility(View.VISIBLE);
        iv_coin_2.setVisibility(View.VISIBLE);
        iv_coin_3.setVisibility(View.VISIBLE);
        if(mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
            ObjectAnimator [] objectAnimator_1 = GamesAnimationUtil.translateViewToView(iv_coin_1, coinView, 3000);
            ObjectAnimator [] objectAnimator_2 = GamesAnimationUtil.translateViewToView(iv_coin_2, coinView, 3000);
            ObjectAnimator [] objectAnimator_3 = GamesAnimationUtil.translateViewToView(iv_coin_3, coinView, 3000);
//            mAnimatorSet.play(objectAnimator_1[0]).with(objectAnimator_1[1]).before(objectAnimator_2[0])
//                    .with(objectAnimator_2[1]).before(objectAnimator_3[0]).with(objectAnimator_3[1]);
            mAnimatorSet.play(objectAnimator_1[0]).with(objectAnimator_1[1]);
//            mAnimatorSet.play(objectAnimator_3[0]).with(objectAnimator_3[1]).after(objectAnimator_2[0]);
//            mAnimatorSet.setDuration(1000);
        }
        mAnimatorSet.start();
    }

    private void resetViews() {
        SDViewUtil.resetView(iv_coin_1);
        SDViewUtil.resetView(iv_coin_2);
        SDViewUtil.resetView(iv_coin_3);
    }

    private class ViewTranslateRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
