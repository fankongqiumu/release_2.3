package com.fanwe.live.model;


import com.fanwe.hybrid.model.BaseActModel;

/**
 * Created by Administrator on 2016/7/6.
 */
public class App_do_loginActModel extends BaseActModel
{
    private UserModel user_info;
    private int is_lack;//是否缺失信息

    private int first_login;
    private int new_level;

    public int getFirst_login() {
        return first_login;
    }

    public void setFirst_login(int first_login) {
        this.first_login = first_login;
    }

    public int getNew_level() {
        return new_level;
    }

    public void setNew_level(int new_level) {
        this.new_level = new_level;
    }

    public UserModel getUser_info()
    {
        return user_info;
    }

    public void setUser_info(UserModel user_info)
    {
        this.user_info = user_info;
    }

    public int getIs_lack()
    {
        return is_lack;
    }

    public void setIs_lack(int is_lack)
    {
        this.is_lack = is_lack;
    }

}
