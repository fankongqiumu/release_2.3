package com.fanwe.live.utils;

import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;

import java.text.DecimalFormat;

public class LiveUtils
{

    public static String formatUnreadNumber(long number)
    {
        if (number > 99)
        {
            return "99+";
        } else
        {
            return String.valueOf(number);
        }
    }

    public static int getLevelImageResId(int level)
    {
        int resId = 0;
        try
        {
            String imageName = String.valueOf("rank_" + level);
            resId = SDViewUtil.getIdentifierDrawable(imageName);
        } catch (Exception e)
        {
            resId = R.drawable.nopic_expression;
        }
        return resId;
    }

    public static int getSexImageResId(int sex)
    {
        int resId = 0;
        switch (sex)
        {
            case 0:

                break;
            case 1:
                resId = R.drawable.ic_global_male;
                break;
            case 2:
                resId = R.drawable.ic_global_female;
                break;
            default:
                break;
        }
        return resId;
    }

    public static String getFormatNumber(int number)
    {
        double result;
        DecimalFormat format = new DecimalFormat("#.00");
        if (number >= 10000)
        {
            result = number;
            result = result / 10000;
            return format.format(result) + "万";
        }
        return String.valueOf(number);
    }


    public static String getFormatNumber(long number)
    {
        double result;
        DecimalFormat format = new DecimalFormat("#.00");
        if (number >= 10000)
        {
            result = number;
            result = result / 10000;
            return format.format(result) + "万";
        }
        return String.valueOf(number);
    }
}
