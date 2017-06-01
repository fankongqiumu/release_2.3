package com.fanwe.live.model;

/**
 * 互动直播的创建数据
 */
public class CreateLiveData
{

    private int roomId; // 直播间房间号
    private int isClosedBack; //1：主播界面被强制关闭后回来
    private int videoType; //0-互动直播，1-直播

    public int getVideoType()
    {
        return videoType;
    }

    public void setVideoType(int videoType)
    {
        this.videoType = videoType;
    }

    public int getIsClosedBack()
    {
        return isClosedBack;
    }

    public void setIsClosedBack(int isClosedBack)
    {
        this.isClosedBack = isClosedBack;
    }

    public int getRoomId()
    {
        return roomId;
    }

    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("roomId:").append(roomId).append("\r\n");
        sb.append("isClosedBack:").append(isClosedBack).append("\r\n");
        return sb.toString();
    }
}
