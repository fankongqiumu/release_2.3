package com.librarygames.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.animator.SDAnim;
import com.fanwe.library.common.SDWindowManager;
import com.fanwe.library.customview.FlowLayout;
import com.fanwe.library.utils.SDViewUtil;

/**
 * Created by shibx on 2016/11/21.
 *  游戏动画工具类
 */

public class GamesAnimationUtil {

    /**
     * 获取 oldView 到 newView 的位移动画数组
     * @param oldView 旧位置
     * @param newView 新位置
     * @param durationMillis 耗时
     * @return 动画数组(横向平移动画,竖向平移动画)
     */
    public static ObjectAnimator[] translateViewToView(View oldView, View newView, long durationMillis) {
        ObjectAnimator [] animators = new ObjectAnimator[2];

        SDAnim anim;
        if(oldView != null && newView != null) {
            anim = SDAnim.from(oldView);
            anim.setDuration(durationMillis);
            int oldX = SDViewUtil.getViewXOnScreen(oldView);
            int oldY = SDViewUtil.getViewYOnScreen(oldView);
            int newX = SDViewUtil.getViewXOnScreen(newView);
            int newY = SDViewUtil.getViewYOnScreen(newView);
            anim.setDuration(durationMillis);
            animators[0] = anim.setX(newX-oldX).get();
            animators[1] = anim.clone().setY(newY-oldY).get();
        }
        return animators;
    }

    /**
     * 使 view 获取 oldView 到 newView 的位移动画数组
     * @param view
     * @param oldView
     * @param newView
     * @param durationMillis 耗时
     * @return
     */
    public static ObjectAnimator[] translateViewToView(View view,View oldView, View newView, long durationMillis) {
        ObjectAnimator [] animators = new ObjectAnimator[2];

        SDAnim anim = null;
        if(oldView != null && newView != null) {
            anim = SDAnim.from(view);
            anim.setDuration(durationMillis);
            int oldX = SDViewUtil.getViewXOnScreen(oldView);
            int oldY = SDViewUtil.getViewYOnScreen(oldView);
            int newX = SDViewUtil.getViewXOnScreen(newView);
            int newY = SDViewUtil.getViewYOnScreen(newView);
            anim.setDuration(durationMillis);
            animators[0] = anim.setX(newX-oldX).get();
            animators[1] = anim.clone().setY(newY-oldY).get();
        }
        return animators;
    }

    /**
     * 不在同一个布局下  使复制的oldView 从oldView 移动到 newView 的动画
     * @param oldView
     * @param newView
     * @param durationMillis 耗时
     * @return
     */
    public static void translateViewToViewForWindowManager(View oldView,View newView, long durationMillis){
        int[] lo = SDViewUtil.getLocationOnScreen(oldView);
        Bitmap bitmap = SDViewUtil.createViewBitmap(oldView);
        //imageView
        ImageView imageView = new ImageView(SDLibrary.getInstance().getApplication());
        imageView.setImageBitmap(bitmap);
        SDViewUtil.setViewPadding(imageView,lo[0],lo[1],SDViewUtil.getScreenWidth()-lo[0]-oldView.getWidth(), SDViewUtil.getScreenHeight()-lo[1]-oldView.getHeight());
        //frameLayout
        final FrameLayout frameLayout = new FrameLayout(SDLibrary.getInstance().getApplication());
        frameLayout.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //WindowManager
        WindowManager.LayoutParams layoutParams = SDWindowManager.getInstance().newLayoutParams();
        layoutParams.type= WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //frameLayout.add(imageView)
        SDViewUtil.addView(frameLayout,imageView);
        //WindowManager.add(frameLayout)
        SDWindowManager.getInstance().addView(frameLayout,layoutParams);
        //animatorSet
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] translationAnimators = GamesAnimationUtil.translateViewToView(imageView,oldView,newView, durationMillis);
        animatorSet.play(translationAnimators[0]).with(translationAnimators[1]);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SDWindowManager.getInstance().removeView(frameLayout);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    /**
     * 放大缩小View
     * @param view              view
     * @param durationMillis    运行时间
     * @param values            缩放参数
     * @return
     */
    public static ObjectAnimator[] scaleView(View view, long durationMillis, float... values) {
        ObjectAnimator [] animators = new ObjectAnimator[2];
        SDAnim anim = null;
        if(view != null) {
            anim = SDAnim.from(view);
            anim.setDuration(durationMillis);
            anim.setRepeatCount(0);
            animators[0] = anim.setScaleX(values).get();
            animators[1] = anim.clone().setScaleY(values).get();
        }
        return animators;
    }

    public static ObjectAnimator[] scaleView(View oldView, View newView, long durationMillis) {
        ObjectAnimator[] animators = new ObjectAnimator[2];
        SDAnim anim;
        if(oldView != null && newView != null) {
            anim = SDAnim.from(oldView);
            anim.setDuration(durationMillis);
            int oldWidth = SDViewUtil.getViewWidth(oldView);
            int oldHeight = SDViewUtil.getViewHeight(oldView);
            int newWidth = SDViewUtil.getViewWidth(newView);
            int newHeight = SDViewUtil.getViewHeight(newView);
            anim.setDuration(durationMillis);
            animators[0] = anim.setScaleX(newWidth/oldWidth).get();
            animators[1] = anim.clone().setScaleY(newHeight/oldHeight).get();
        }
        return animators;
    }

    /**
     *  游戏面板出现动画
     * @param view
     * @return
     */
    @NonNull
    public static ObjectAnimator translatePanelViewBotIn(@NonNull View view) {
        SDAnim anim = SDAnim.from(view);
        anim.setDuration(400);
        return anim.setY(view.getHeight(), 0).get();
    }

    /**
     *  游戏面板消失动画
     * @param view
     * @return
     */
    @NonNull
    public static ObjectAnimator translatePanelViewBotOut(@NonNull View view) {
        SDAnim anim = SDAnim.from(view);
        anim.setDuration(400);
        return anim.setY(0, view.getHeight()).get();
    }
}
