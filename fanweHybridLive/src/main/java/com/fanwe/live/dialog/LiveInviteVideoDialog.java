package com.fanwe.live.dialog;

import android.app.Activity;

import com.fanwe.library.dialog.SDDialogConfirm;

public class LiveInviteVideoDialog extends SDDialogConfirm
{
    private String userId;

    public LiveInviteVideoDialog(Activity activity, String userId)
    {
        super(activity);
        this.userId = userId;

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setTextContent("申请连麦中，等待对方应答...").setTextConfirm(null).setTextCancel("取消连麦");
    }
}
