package com.fengjr.simpledatepicker.util;

import android.content.Context;

/**
 * Created by zengyong on 2018/3/29
 */

public class DPUtil {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
