package com.fanwe.live.activity.room;

/**
 * Created by Administrator on 2016/12/26.
 */

public interface ILiveChangeRoomListener
{
    /**
     * 切换房间动作完成回调
     */
    void onChangeRoomActionComplete();

    /**
     * 切换到新房间完成回调
     */
    void onChangeRoomComplete();

}
