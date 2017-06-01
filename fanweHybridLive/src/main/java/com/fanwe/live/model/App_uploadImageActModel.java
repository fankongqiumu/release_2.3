package com.fanwe.live.model;

import com.fanwe.hybrid.model.BaseActModel;

/**
 * Created by Administrator on 2016/7/25.
 */
public class App_uploadImageActModel extends BaseActModel
{
    private String server_full_path;
    private String path;
    private UserModel user_info;

    public String getServer_full_path()
    {
        return server_full_path;
    }

    public void setServer_full_path(String server_full_path)
    {
        this.server_full_path = server_full_path;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public UserModel getUser_info()
    {
        return user_info;
    }

    public void setUser_info(UserModel user_info)
    {
        this.user_info = user_info;
    }
}
