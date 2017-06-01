package com.fanwe.live.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.dialog.LiveUserInfoDialog;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgPopMsg;
import com.fanwe.live.utils.GlideUtil;

public class LivePopMsgView extends LinearLayout
{

    private static final long DURATION_TRANSLATION = 8 * 1000;
    /**
     * view在屏幕外的偏移量，为了让平移看上去更顺滑
     */
    private static final int DISTANCE_OFFSET = SDViewUtil.dp2px(30);

    private ImageView iv_head_image;
    private TextView tv_nickname;
    private TextView tv_content;

    private AnimatorSet animatorSetIn;
    private boolean isPlaying;
    private CustomMsgPopMsg msg;

    public LivePopMsgView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LivePopMsgView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LivePopMsgView(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_live_pop_msg, this, true);

        iv_head_image = (ImageView) findViewById(R.id.iv_head_image);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_content = (TextView) findViewById(R.id.tv_content);

        iv_head_image.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                clickHeadImage();
            }
        });

        SDViewUtil.invisible(this);

    }

    protected void clickHeadImage()
    {
        UserModel user = msg.getSender();
        LiveUserInfoDialog dialog = new LiveUserInfoDialog((Activity) getContext(), user.getUser_id());
        dialog.showCenter();
    }

    public boolean canPlay()
    {
        return !isPlaying;
    }

    private void setMsg(CustomMsgPopMsg msg)
    {
        this.msg = msg;
    }

    public void playMsg(CustomMsgPopMsg newMsg)
    {
        if (newMsg != null)
        {
            if (canPlay())
            {
                isPlaying = true;

                setMsg(newMsg);

                SDHandlerManager.getMainHandler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        bindData();
                        new SDDelayRunnable()
                        {
                            @Override
                            public void run()
                            {
                                playIn();
                            }
                        }.runDelay(500);
                    }
                });
            }
        }
    }

    private void bindData()
    {
        UserModel sender = msg.getSender();
        if (sender != null)
        {
            SDViewBinder.setTextView(tv_nickname, sender.getNick_name());
            GlideUtil.loadHeadImage(sender.getHead_image()).into(iv_head_image);
            SDViewBinder.setTextView(tv_content, msg.getDesc());
        }
    }

    private void playIn()
    {
        int viewX = SDViewUtil.getViewXOnScreen(this);
        int viewWidth = SDViewUtil.getViewWidth(this);

        int startX = SDViewUtil.getScreenWidth() - viewX;
        int endX = -viewX - viewWidth;

        ObjectAnimator inTranslationX = ObjectAnimator.ofFloat(this, "translationX", startX + (DISTANCE_OFFSET / 2), endX - DISTANCE_OFFSET);
        inTranslationX.setDuration(DURATION_TRANSLATION);

        animatorSetIn = new AnimatorSet();
        animatorSetIn.playTogether(inTranslationX);
        animatorSetIn.setInterpolator(new LinearInterpolator());
        animatorSetIn.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                SDViewUtil.show(LivePopMsgView.this);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                SDViewUtil.invisible(LivePopMsgView.this);
                SDViewUtil.resetView(LivePopMsgView.this);
                isPlaying = false;
            }
        });
        animatorSetIn.start();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        stopAnimator();
        super.onDetachedFromWindow();
    }

    public void stopAnimator()
    {
        if (animatorSetIn != null)
        {
            animatorSetIn.cancel();
        }
    }

}
