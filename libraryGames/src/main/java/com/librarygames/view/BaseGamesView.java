package com.librarygames.view;

import android.content.Context;
import android.util.AttributeSet;

import com.fanwe.library.view.SDAppView;

/**
 * Created by shibx on 2016/11/17.
 */

public class BaseGamesView extends SDAppView {

    public BaseGamesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BaseGamesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseGamesView(Context context) {
        super(context);
    }

    @Override
    public void setContentView(int layoutId) {
        super.setContentView(layoutId);
    }
}
