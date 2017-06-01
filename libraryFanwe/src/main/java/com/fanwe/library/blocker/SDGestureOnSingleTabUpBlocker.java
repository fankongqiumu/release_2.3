package com.fanwe.library.blocker;

import android.content.Context;
import android.view.MotionEvent;

public class SDGestureOnSingleTabUpBlocker extends SDGestureBlocker
{

	public SDGestureOnSingleTabUpBlocker(Context context)
	{
		this(DEFAULT_BLOCK_TIME, context);
	}

	public SDGestureOnSingleTabUpBlocker(long blockDuration, Context context)
	{
		super(blockDuration, context);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		if (blocker.block())
		{
			return true;
		} else
		{
			return super.onSingleTapUp(e);
		}
	}

}
