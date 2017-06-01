package com.fanwe.live.appview.room;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.utils.SDAnimationUtil;
import com.fanwe.library.utils.SDRunnableIntercepter;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.view.SDRecyclerView;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveContActivity;
import com.fanwe.live.adapter.LiveViewerListRecyclerAdapter;
import com.fanwe.live.appview.RoomSdkInfoView;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.LiveUserInfoDialog;
import com.fanwe.live.event.ERequestFollowSuccess;
import com.fanwe.live.model.App_followActModel;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.RoomUserModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.utils.GlideUtil;

import java.util.List;

/**
 * 直播间顶部view
 */
public class RoomInfoView extends RoomView
{

    public RoomInfoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public RoomInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RoomInfoView(Context context)
    {
        super(context);
        init();
    }

    private View ll_click_creater;
    private ImageView iv_head_image;
    private ImageView iv_level;
    protected TextView tv_video_type;
    private TextView tv_viewer_number;
    private View view_operate_viewer;
    private View view_add_viewer;
    private View view_minus_viewer;
    private SDRecyclerView hlv_viewer;
    private LinearLayout ll_ticket;
    private TextView tv_ticket;
    private TextView tv_user_number;
    private LinearLayout ll_follow;
    private TextView tv_creater_leave;
    private RoomSdkInfoView view_sdk_info;

    private LiveViewerListRecyclerAdapter adapter;
    private int hasFollow;
    private SDRunnableIntercepter runnableIntercepter;
    private ClickListener clickListener;


    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    protected int onCreateContentView()
    {
        return R.layout.frag_live_room_info;
    }

    @Override
    protected void init()
    {
        super.init();
        ll_click_creater = find(R.id.ll_click_creater);
        iv_head_image = find(R.id.iv_head_image);
        iv_level = find(R.id.iv_level);
        tv_video_type = find(R.id.tv_video_type);
        tv_viewer_number = find(R.id.tv_viewer_number);
        view_operate_viewer = find(R.id.view_operate_viewer);
        view_add_viewer = find(R.id.view_add_viewer);
        view_minus_viewer = find(R.id.view_minus_viewer);
        hlv_viewer = find(R.id.hlv_viewer);
        ll_ticket = find(R.id.ll_ticket);
        tv_ticket = find(R.id.tv_ticket);
        tv_user_number = find(R.id.tv_user_number);
        ll_follow = find(R.id.ll_follow);
        tv_creater_leave = find(R.id.tv_creater_leave);
        view_sdk_info = find(R.id.view_sdk_info);

        view_add_viewer.setOnClickListener(this);
        view_minus_viewer.setOnClickListener(this);
        ll_follow.setOnClickListener(this);
        ll_ticket.setOnClickListener(this);
        ll_click_creater.setOnClickListener(this);

        runnableIntercepter = new SDRunnableIntercepter();
        runnableIntercepter.setMaxIntercepteCount(30);

        hlv_viewer.setLinearHorizontal();
        adapter = new LiveViewerListRecyclerAdapter(getActivity());
        hlv_viewer.setAdapter(adapter);
    }

    public void setTextVideoType(String text)
    {
        tv_video_type.setText(text);
    }

    public RoomSdkInfoView getSdkInfoView()
    {
        return view_sdk_info;
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == view_add_viewer)
        {
            if (clickListener != null)
            {
                clickListener.onClickAddViewer(v);
            }
        } else if (v == view_minus_viewer)
        {
            if (clickListener != null)
            {
                clickListener.onClickMinusViewer(v);
            }
        } else if (v == ll_follow)
        {
            clickFollow();
            ll_follow.setOnClickListener(null);
        } else if (v == ll_ticket)
        {
            clickTicket();
        } else if (v == ll_click_creater)
        {
            String id = getLiveInfo().getCreaterId();
            clickHeadImage(id);
        }
    }

    /**
     * 显示隐藏私密直播的邀请观众view
     *
     * @param show
     */
    public void showOperateViewerView(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(view_operate_viewer);
        } else
        {
            SDViewUtil.hide(view_operate_viewer);
        }
    }

    /**
     * 绑定数据
     *
     * @param actModel
     */
    public void bindData(App_get_videoActModel actModel)
    {
        if (actModel == null)
        {
            return;
        }

        if (isPlayback())
        {
            setTextVideoType("精彩回放");
        } else
        {
            if (actModel.isPushMode())
            {
                setTextVideoType("直播Live");
            } else
            {
                setTextVideoType("互动直播");
            }
        }

        RoomUserModel createrModel = actModel.getPodcast();
        if (createrModel != null)
        {
            UserModel creater = createrModel.getUser();
            if (!creater.equals(UserModelDao.query()))
            {
                bindHasFollow(createrModel.getHas_focus(), false);
            } else
            {
                SDViewUtil.hide(ll_follow);
            }
        }
    }

    protected void clickFollow()
    {
        CommonInterface.requestFollow(getLiveInfo().getCreaterId(), getLiveInfo().getRoomId(), new AppRequestCallback<App_followActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    bindHasFollow(actModel.getHas_focus(), true);
                }
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                ll_follow.setOnClickListener(RoomInfoView.this);
            }
        });
    }

    protected void clickTicket()
    {
        Intent intent = new Intent(getActivity(), LiveContActivity.class);
        intent.putExtra(RoomContributionView.EXTRA_ROOM_ID, getLiveInfo().getRoomId());
        intent.putExtra(RoomContributionView.EXTRA_USER_ID, getLiveInfo().getCreaterId());
        getActivity().startActivity(intent);
    }

    /**
     * 绑定主播数据
     *
     * @param user
     */
    public void bindCreaterData(UserModel user)
    {
        GlideUtil.loadHeadImage(user.getHead_image()).into(iv_head_image);
        if (TextUtils.isEmpty(user.getV_icon()))
        {
            SDViewUtil.hide(iv_level);
        } else
        {
            SDViewUtil.show(iv_level);
            GlideUtil.load(user.getV_icon()).into(iv_level);
        }
        SDViewBinder.setTextView(tv_user_number, String.valueOf(user.getShowId()));
        updateTicket(user.getTicket());
    }

    /**
     * 更新印票数量
     *
     * @param ticket
     */
    public void updateTicket(long ticket)
    {
        if (ticket < 0)
        {
            ticket = 0;
        }
        SDViewBinder.setTextView(tv_ticket, String.valueOf(ticket));
    }

    /**
     * 更新观众列表
     *
     * @param listModel
     */
    public void onLiveRefreshViewerList(List<UserModel> listModel)
    {
        adapter.updateData(listModel);
    }

    /**
     * 移除观众
     *
     * @param model
     */
    public void onLiveRemoveViewer(UserModel model)
    {
        adapter.removeData(model);
    }

    /**
     * 插入观众
     *
     * @param position
     * @param model
     */
    public void onLiveInsertViewer(int position, UserModel model)
    {
        adapter.insertData(position, model);
        if (position == 0 && hlv_viewer.isIdle())
        {
            runnableIntercepter.post(scrollToStartRunnable, 1000);
        }
    }

    private Runnable scrollToStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            hlv_viewer.scrollToStart();
        }
    };

    /**
     * 更新观众数量
     *
     * @param viewerNumber
     */
    public void updateViewerNumber(int viewerNumber)
    {
        if (viewerNumber < 0)
        {
            viewerNumber = 0;
        }
        SDViewBinder.setTextView(tv_viewer_number, String.valueOf(viewerNumber));
    }

    protected void clickHeadImage(String to_userid)
    {
        // 显示用户信息窗口
        LiveUserInfoDialog dialog = new LiveUserInfoDialog(getActivity(), to_userid);
        dialog.show();
    }

    /**
     * 显示隐藏主播离开
     *
     * @param show
     */
    public void showCreaterLeave(boolean show)
    {
        if (show)
        {
            SDViewUtil.show(tv_creater_leave);
        } else
        {
            SDViewUtil.hide(tv_creater_leave);
        }
    }

    public void onEventMainThread(ERequestFollowSuccess event)
    {
        if (event.userId.equals(getLiveInfo().getCreaterId()))
        {
            bindHasFollow(event.actModel.getHas_focus(), true);
        }
    }

    public int getHasFollow()
    {
        return hasFollow;
    }

    private void bindHasFollow(int hasFollow, boolean anim)
    {
        this.hasFollow = hasFollow;
        if (hasFollow == 1)
        {
            ll_follow.setOnClickListener(null);
            if (anim)
            {
                AnimatorSet animatorSet = SDAnimationUtil.scale(ll_follow, 1.0f, 0.0f);
                animatorSet.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        SDViewUtil.resetView(ll_follow);
                        SDViewUtil.hide(ll_follow);
                    }
                });
                animatorSet.start();
            } else
            {
                SDViewUtil.hide(ll_follow);
            }
        } else
        {
            ll_follow.setOnClickListener(this);
            if (anim)
            {
                AnimatorSet animatorSet = SDAnimationUtil.scale(ll_follow, 0.0f, 1.0f);
                animatorSet.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        SDViewUtil.show(ll_follow);
                    }
                });
                animatorSet.start();
            } else
            {
                SDViewUtil.show(ll_follow);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        runnableIntercepter.onDestroy();
    }

    public interface ClickListener
    {
        void onClickAddViewer(View v);

        void onClickMinusViewer(View v);
    }

}
