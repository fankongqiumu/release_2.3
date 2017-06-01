package com.fanwe.live.fragment;

import android.content.Intent;

import com.fanwe.hybrid.fragment.BaseFragment;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LivePrivateChatActivity;
import com.fanwe.live.appview.LiveChatC2CNewView;
import com.fanwe.live.model.LiveConversationListModel;

/**
 * Created by 1363655717 on 2017-06-01.
 */

public class LiveTabNoticeFragment extends BaseFragment{

    @Override
    protected int onCreateContentView() {
        return R.layout.act_live_chat_c2c;
    }
    @Override
    protected void init()
    {
        super.init();
        LiveChatC2CNewView view = new LiveChatC2CNewView(getActivity());
       view.setRl_back();

        view.setOnChatItemClickListener(new LiveChatC2CNewView.OnChatItemClickListener()
        {
            @Override
            public void onChatItemClickListener(LiveConversationListModel itemLiveChatListModel)
            {
                Intent intent = new Intent(getActivity(), LivePrivateChatActivity.class);
                intent.putExtra(LivePrivateChatActivity.EXTRA_USER_ID, itemLiveChatListModel.getPeer());
                startActivity(intent);
            }
        });
        SDViewUtil.replaceView(findViewById(R.id.ll_content), view);
        //传入数据
        view.requestData();
    }
}
