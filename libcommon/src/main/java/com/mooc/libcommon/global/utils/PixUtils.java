package com.mooc.libcommon.global.utils;

import android.util.DisplayMetrics;

import com.mooc.libcommon.global.AppGlobals;

public class PixUtils {
    public static int dp2px(int dpValue){
        DisplayMetrics metrics= AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density*dpValue+0.5f);
    }

    //获得屏幕的宽和高

    public static int getScreenWidth() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
