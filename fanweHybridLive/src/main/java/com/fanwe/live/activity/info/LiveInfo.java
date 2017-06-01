package com.fanwe.live.activity.info;

import com.fanwe.live.model.App_get_videoActModel;

public interface LiveInfo
{
    /**
     * 获得直播间聊天组id
     */
    String getGroupId();

    /**
     * 获得直播间主播id
     */
    String getCreaterId();

    /**
     * 获得直播间id
     */
    int getRoomId();

    /**
     * 获得直播间信息实体
     *
     * @return
     */
    App_get_videoActModel getRoomInfo();

    /**
     * 是否私密直播
     *
     * @return
     */
    boolean isPrivate();

    /**
     * 是否是主播
     *
     * @return
     */
    boolean isCreater();

    /**
     * 是否正在竞拍
     */
    boolean isAuctioning();

    /**
     * 打开直播间输入框
     *
     * @param content
     */
    void openSendMsg(String content);
}
