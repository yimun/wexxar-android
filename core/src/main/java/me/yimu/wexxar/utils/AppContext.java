package me.yimu.wexxar.utils;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by linwei on 2018/3/4.
 */

public class AppContext extends ContextWrapper {

    private static AppContext sInstance;

    public static void init(Context context) {
        if (null == context) {
            return;
        }
        sInstance = new AppContext(context.getApplicationContext());
    }

    public static AppContext getInstance() {
        if (null == sInstance) {
            throw new IllegalStateException("AppContext must be initialized first!");
        }
        return sInstance;
    }

    public AppContext(Context base) {
        super(base);
    }
}
