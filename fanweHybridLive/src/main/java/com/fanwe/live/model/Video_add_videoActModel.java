package com.fanwe.live.model;

import com.fanwe.hybrid.model.BaseActModel;

public class Video_add_videoActModel extends BaseActModel
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int room_id;
    private String private_share;
    private String share_type; // 分享类型
    private RoomShareModel share;
    private int video_type; // 0-腾讯云互动直播;1-腾讯云直播

    public int getVideo_type()
    {
        return video_type;
    }

    public void setVideo_type(int video_type)
    {
        this.video_type = video_type;
    }

    public String getPrivate_share()
    {
        return private_share;
    }

    public void setPrivate_share(String private_share)
    {
        this.private_share = private_share;
    }

    public String getShare_type()
    {
        return share_type;
    }

    public void setShare_type(String share_type)
    {
        this.share_type = share_type;
    }

    public int getRoom_id()
    {
        return room_id;
    }

    public void setRoom_id(int room_id)
    {
        this.room_id = room_id;
    }

    public RoomShareModel getShare()
    {
        return share;
    }

    public void setShare(RoomShareModel share)
    {
        this.share = share;
    }

}
