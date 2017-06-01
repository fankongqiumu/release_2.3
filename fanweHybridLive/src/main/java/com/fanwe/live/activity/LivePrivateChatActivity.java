package com.fanwe.live.activity;

import android.os.Bundle;

import com.fanwe.hybrid.activity.BaseActivity;
import com.fanwe.live.R;
import com.fanwe.live.appview.LivePrivateChatView;
import com.fanwe.live.appview.LivePrivateChatView.ClickListener;

import org.xutils.view.annotation.ViewInject;

public class LivePrivateChatActivity extends BaseActivity
{

    /**
     * 聊天对象user_id(String)
     */
    public static final String EXTRA_USER_ID = "extra_user_id";

    @ViewInject(R.id.view_private_chat)
    private LivePrivateChatView view_private_chat;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_private_chat;
    }

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        String user_id = getIntent().getStringExtra(EXTRA_USER_ID);

        view_private_chat.setLockHeightEnable(true);
        view_private_chat.setClickListener(new ClickListener()
        {

            @Override
            public void onClickBack()
            {
                finish();
            }
        });
        view_private_chat.setUserId(user_id);
    }

    @Override
    protected void onKeyboardVisibilityChange(boolean visible, int height)
    {
        if (visible)
        {
            if (view_private_chat.getLockHeight() <= 0)
            {
                int totalHeight = view_private_chat.getViewFirstHeight();
                int titleHeight = view_private_chat.getTitleViewHeight();
                int chatBarHeight = view_private_chat.getChatBarHeight();
                int lockHeight = totalHeight - titleHeight - chatBarHeight - height;
                view_private_chat.setLockHeight(lockHeight);
            }
            view_private_chat.setShouldUnlockContentWhenKeyboardHide(true);
        } else
        {
            if (view_private_chat.shouldUnlockContentWhenKeyboardHide())
            {
                view_private_chat.unLockContent();
            }
        }
    }
}
