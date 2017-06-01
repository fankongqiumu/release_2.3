package com.fanwe.live;

import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.UserModel;

public class LiveInformation
{

    private static LiveInformation instance;

    private String createrId = "";
    private int roomId = 0;
    private String groupId = "";
    private int videoType; // 0-互动直播，1-直播
    private String currentChatPeer = "";//当前正在聊天peer
    /**
     * 是否私密直播
     */
    private boolean isPrivate = false;

    private int requestCount = 0;

    public static LiveInformation getInstance()
    {
        if (instance == null)
        {
            synchronized (LiveInformation.class)
            {
                if (instance == null)
                {
                    instance = new LiveInformation();
                }
            }
        }
        return instance;
    }

    public boolean isPrivate()
    {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }

    public void reduceRequestCount()
    {
        setRequestCount(requestCount - 1);
    }

    public void setRequestCount(int requestCount)
    {
        if (requestCount >= 0 && requestCount <= LiveConstant.VIDEO_VIEW_MAX)
        {
            this.requestCount = requestCount;
        }
    }

    // getter setter


    public String getCurrentChatPeer()
    {
        return currentChatPeer;
    }

    public void setCurrentChatPeer(String currentChatPeer)
    {
        this.currentChatPeer = currentChatPeer;
    }

    public int getRequestCount()
    {
        return requestCount;
    }

    public UserModel getUser()
    {
        return UserModelDao.query();
    }

    public int getVideoType()
    {
        return videoType;
    }

    public void setVideoType(int videoType)
    {
        this.videoType = videoType;
    }

    public boolean isCreater()
    {
        UserModel user = getUser();
        if (user != null)
        {
            return user.getUser_id().equals(createrId);
        } else
        {
            return false;
        }
    }

    public int getRoomId()
    {
        return roomId;
    }

    public String getCreaterId()
    {
        return createrId;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void enterRoom(int roomId, String groupId, String createrId)
    {
        this.roomId = roomId;
        this.groupId = groupId;
        this.createrId = createrId;
    }

    public void exitRoom()
    {
        createrId = "";
        roomId = 0;
        groupId = "";
        setPrivate(false);
    }

}
