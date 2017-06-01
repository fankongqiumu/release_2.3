package com.fanwe.live.model;

import com.fanwe.hybrid.model.BaseActModel;

public class App_followActModel extends BaseActModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int has_focus; // 1-已关注，0-未关注

	public int getHas_focus()
	{
		return has_focus;
	}

	public void setHas_focus(int has_focus)
	{
		this.has_focus = has_focus;
	}

}
