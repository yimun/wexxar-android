package me.yimu.wexxar.utils;

import android.util.Log;

import me.yimu.wexxar.Wexxar;

/**
 * Created by linwei on 2018/3/4.
 */

public class LogUtils {


    public static void i(String subTag, String message) {
        if (Wexxar.DEBUG) {
            Log.i(Wexxar.TAG, String.format("[%1$s] %2$s", subTag, message));
        }
    }

    public static void e(String subTag, String message) {
        if (Wexxar.DEBUG) {
            Log.e(Wexxar.TAG, String.format("[%1$s] %2$s", subTag, message));
        }
    }
}
