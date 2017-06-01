package com.fanwe.library.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fanwe.library.common.SDActivityManager;
import com.fanwe.library.common.SDFragmentManager;
import com.fanwe.library.dialog.SDDialogProgress;
import com.fanwe.library.event.EKeyboardVisibilityChange;
import com.fanwe.library.event.EOnActivityResult;
import com.fanwe.library.event.EOnBackground;
import com.fanwe.library.event.EOnResumeFromBackground;
import com.fanwe.library.listener.SDDispatchKeyEventListener;
import com.fanwe.library.listener.SDDispatchTouchEventListener;
import com.fanwe.library.listener.SDIterateListener;
import com.fanwe.library.listener.SDSizeListener;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDListenerManager;
import com.fanwe.library.utils.SDPackageUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.utils.SDWindowSizeListener;
import com.fanwe.library.view.SDAppView;
import com.sunday.eventbus.SDBaseEvent;
import com.sunday.eventbus.SDEventManager;
import com.sunday.eventbus.SDEventObserver;

import java.util.Iterator;
import java.util.List;

public class SDBaseActivity extends FragmentActivity implements SDEventObserver, OnClickListener
{
    private FrameLayout flRootLayout;
    private SDFragmentManager fragmentManager;

    private static boolean isBackground = false;
    private static long backgroundTime;

    private boolean isStopped;
    private SDDialogProgress progressDialog;
    private SDWindowSizeListener windowSizeListener = new SDWindowSizeListener();

    private SDListenerManager<Application.ActivityLifecycleCallbacks> activityLifecycleListenerManager = new SDListenerManager<>();
    private SDListenerManager<SDDispatchTouchEventListener> dispatchTouchEventListenerManager = new SDListenerManager<>();
    private SDListenerManager<SDDispatchKeyEventListener> dispatchKeyEventListenerManager = new SDListenerManager<>();

    public static boolean isBackground()
    {
        return isBackground;
    }

    public Activity getActivity()
    {
        return this;
    }

    public static long getBackgroundTime()
    {
        return backgroundTime;
    }

    //SDListenerManager
    public SDListenerManager<Application.ActivityLifecycleCallbacks> getActivityLifecycleListenerManager()
    {
        return activityLifecycleListenerManager;
    }

    public SDListenerManager<SDDispatchTouchEventListener> getDispatchTouchEventListenerManager()
    {
        return dispatchTouchEventListenerManager;
    }

    public SDListenerManager<SDDispatchKeyEventListener> getDispatchKeyEventListenerManager()
    {
        return dispatchKeyEventListenerManager;
    }

    public boolean isStopped()
    {
        return isStopped;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        flRootLayout = (FrameLayout) findViewById(android.R.id.content);
        SDActivityManager.getInstance().onCreate(this);
        SDEventManager.register(this);
        afterOnCreater(savedInstanceState);

        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityCreated(SDBaseActivity.this, savedInstanceState);
                return false;
            }
        });
    }

    private void afterOnCreater(Bundle savedInstanceState)
    {
        initWindowSizeListener();

        int layoutId = onCreateContentView();
        if (layoutId != 0)
        {
            setContentView(layoutId);
        }

        init(savedInstanceState);
    }

    /**
     * 返回布局activity布局id，基类调用的顺序：onCreateContentView()->setContentView()->init()
     *
     * @return
     */
    protected int onCreateContentView()
    {
        return 0;
    }

    /**
     * 重写此方法初始化，如果没有重写onCreateContentView()方法，则要手动调用setContentView()设置activity布局;
     *
     * @param savedInstanceState
     */
    protected void init(Bundle savedInstanceState)
    {

    }

    private void initWindowSizeListener()
    {
        windowSizeListener.listen(this, new SDSizeListener<View>()
        {
            @Override
            public void onWidthChanged(int newWidth, int oldWidth, int differ, View view)
            {
            }

            @Override
            public void onHeightChanged(int newHeight, int oldHeight, int differ, View view)
            {
                if (oldHeight > 0 && newHeight > 0)
                {
                    int absDiffer = Math.abs(differ);
                    if (absDiffer > 400)
                    {
                        if (differ > 0)
                        {
                            //键盘收起
                            onKeyboardVisibilityChange(false, absDiffer);
                        } else
                        {
                            // 键盘弹出
                            onKeyboardVisibilityChange(true, absDiffer);
                        }
                    }
                }
            }
        });
    }

    protected void onKeyboardVisibilityChange(boolean visible, int height)
    {
        EKeyboardVisibilityChange event = new EKeyboardVisibilityChange();
        event.visible = visible;
        event.height = height;
        SDEventManager.post(event);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V find(int id)
    {
        View view = findViewById(id);
        return (V) view;
    }

    @Override
    public void setContentView(int layoutResID)
    {
        View contentView = getLayoutInflater().inflate(layoutResID, null);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view)
    {
        View contentView = addTitleView(view);
        contentView.setFitsSystemWindows(true);
        super.setContentView(contentView);
    }

    /**
     * 为contentView添加titleView
     *
     * @param contentView
     * @return
     */
    private View addTitleView(View contentView)
    {
        View viewFinal = contentView;

        View titleView = createTitleView();
        if (titleView != null)
        {
            LinearLayout linAll = new LinearLayout(this);
            linAll.setOrientation(LinearLayout.VERTICAL);
            linAll.addView(titleView, createTitleViewLayoutParams());
            linAll.addView(contentView, createContentViewLayoutParams());
            viewFinal = linAll;
        }
        return viewFinal;
    }

    private View createTitleView()
    {
        View view = onCreateTitleView();
        if (view == null)
        {
            int resId = onCreateTitleViewResId();
            if (resId != 0)
            {
                view = LayoutInflater.from(this).inflate(resId, null);
            }
        }
        return view;
    }

    protected View onCreateTitleView()
    {
        return null;
    }

    protected int onCreateTitleViewResId()
    {
        return 0;
    }

    protected LinearLayout.LayoutParams createTitleViewLayoutParams()
    {
        return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    protected LinearLayout.LayoutParams createContentViewLayoutParams()
    {
        return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public SDFragmentManager getSDFragmentManager()
    {
        if (fragmentManager == null)
        {
            fragmentManager = new SDFragmentManager(getSupportFragmentManager());
        }
        return fragmentManager;
    }

    public boolean isEmpty(CharSequence content)
    {
        return TextUtils.isEmpty(content);
    }

    public boolean isEmpty(List<?> list)
    {
        return SDCollectionUtil.isEmpty(list);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityStarted(SDBaseActivity.this);
                return false;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isStopped = false;
        SDActivityManager.getInstance().onResume(this);
        if (isBackground)
        {
            isBackground = false;
            onResumeFromBackground();
            SDEventManager.post(new EOnResumeFromBackground());
            backgroundTime = 0;
        }
        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityResumed(SDBaseActivity.this);
                return false;
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityPaused(SDBaseActivity.this);
                return false;
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isStopped = true;
        if (!isBackground)
        {
            if (SDPackageUtil.isBackground())
            {
                isBackground = true;
                backgroundTime = System.currentTimeMillis();
                onBackground();
                SDEventManager.post(new EOnBackground());
            }
        }
        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityStopped(SDBaseActivity.this);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        dismissProgressDialog();
        SDActivityManager.getInstance().onDestroy(this);
        SDEventManager.unregister(this);
        super.onDestroy();

        activityLifecycleListenerManager.foreach(new SDIterateListener<Application.ActivityLifecycleCallbacks>()
        {
            @Override
            public boolean next(int i, Application.ActivityLifecycleCallbacks item, Iterator<Application.ActivityLifecycleCallbacks> it)
            {
                item.onActivityDestroyed(SDBaseActivity.this);
                return false;
            }
        });
    }


    @Override
    public void finish()
    {
        SDActivityManager.getInstance().onDestroy(this);
        super.finish();
    }

    /**
     * 进入后台时候回调
     */
    protected void onBackground()
    {

    }

    /**
     * 从后台回到前台回调
     */
    protected void onResumeFromBackground()
    {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        try
        {
            super.onSaveInstanceState(outState);
            if (outState != null)
            {
                outState.remove("android:support:fragments");
            }
        } catch (Exception e)
        {
            onSaveInstanceStateException(e);
        }
    }

    protected void onSaveInstanceStateException(Exception e)
    {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        try
        {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e)
        {
            onRestoreInstanceStateException(e);
        }
    }

    protected void onRestoreInstanceStateException(Exception e)
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        postOnActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void postOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        EOnActivityResult event = new EOnActivityResult();
        event.activity = this;
        event.data = data;
        event.requestCode = requestCode;
        event.resultCode = resultCode;
        SDEventManager.post(event);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev)
    {
        boolean notifyResult = dispatchTouchEventListenerManager.foreachReverse(new SDIterateListener<SDDispatchTouchEventListener>()
        {
            @Override
            public boolean next(int i, SDDispatchTouchEventListener item, Iterator<SDDispatchTouchEventListener> it)
            {
                return item.dispatchTouchEventActivity(ev);
            }
        });

        if (notifyResult)
        {
            return true;
        } else
        {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event)
    {
        boolean notifyResult = dispatchKeyEventListenerManager.foreachReverse(new SDIterateListener<SDDispatchKeyEventListener>()
        {
            @Override
            public boolean next(int i, SDDispatchKeyEventListener item, Iterator<SDDispatchKeyEventListener> it)
            {
                return item.dispatchKeyEventActivity(event);
            }
        });

        if (notifyResult)
        {
            return true;
        } else
        {
            return super.dispatchKeyEvent(event);
        }
    }

    public boolean isOrientationPortrait()
    {
        return Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation;
    }

    public boolean isOrientationLandscape()
    {
        return Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation;
    }

    public void setOrientationPortrait()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setOrientationLandscape()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void showProgressDialog(String msg)
    {
        if (progressDialog == null)
        {
            progressDialog = new SDDialogProgress(this);
        }
        progressDialog.setTextMsg(msg);

        if (!progressDialog.isShowing())
        {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog()
    {
        if (progressDialog != null)
        {
            try
            {
                progressDialog.dismiss();
            } catch (Exception e)
            {
            }
        }
    }

    public SDDialogProgress getProgressDialog()
    {
        return progressDialog;
    }

    public void removeView(View view)
    {
        SDViewUtil.removeViewFromParent(view);
    }

    public void replaceView(int parentId, View child)
    {
        SDViewUtil.replaceView(findViewById(parentId), child);
    }

    public void replaceView(int parentId, View child, ViewGroup.LayoutParams params)
    {
        SDViewUtil.replaceView(findViewById(parentId), child, params);
    }

    public void replaceView(ViewGroup parent, View child)
    {
        SDViewUtil.replaceView(parent, child);
    }

    public void replaceView(ViewGroup parent, View child, ViewGroup.LayoutParams params)
    {
        SDViewUtil.replaceView(parent, child, params);
    }

    public void addView(View view)
    {
        SDViewUtil.addView(flRootLayout, view);
    }

    public void addView(View view, FrameLayout.LayoutParams params)
    {
        SDViewUtil.addView(flRootLayout, view, params);
    }

    public void registerAppView(SDAppView view)
    {
        if (view != null)
        {
            dispatchKeyEventListenerManager.add(view);
            dispatchTouchEventListenerManager.add(view);
            activityLifecycleListenerManager.add(view);
        }
    }

    public void unregisterAppView(SDAppView view)
    {
        if (view != null)
        {
            dispatchKeyEventListenerManager.remove(view);
            dispatchTouchEventListenerManager.remove(view);
            activityLifecycleListenerManager.remove(view);
        }
    }

    public void setFullScreen()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setNotFullScreen()
    {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onEvent(SDBaseEvent event)
    {

    }

    @Override
    public void onEventMainThread(SDBaseEvent event)
    {

    }

    @Override
    public void onEventBackgroundThread(SDBaseEvent event)
    {

    }

    @Override
    public void onEventAsync(SDBaseEvent event)
    {

    }

    @Override
    public void onClick(View v)
    {

    }
}
