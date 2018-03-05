package me.yimu.wexxar;

import android.content.Context;

import me.yimu.wexxar.route.RouteManager;
import me.yimu.wexxar.utils.AppContext;
import okhttp3.OkHttpClient;

/**
 * Created by linwei on 2018/3/4.
 */

public class Wexxar {

    public static final String TAG = Wexxar.class.getSimpleName();
    public static boolean DEBUG = false;

    /**
     * 可以通过设置OkHttpClient的方式实现共用
     */
    private static OkHttpClient mOkHttpClient;

    public static void initialize(final Context context, boolean asyncLoadRoute, OkHttpClient okHttpClient, RouteManager.RouteConfig config) {
        AppContext.init(context);
        RouteManager.config(config);
        setOkHttpClient(okHttpClient);
        RouteManager.getInstance(asyncLoadRoute);
    }

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        if (null != okHttpClient) {
            mOkHttpClient = okHttpClient;
        }
    }

    public static OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(false)
                    .build();
        }
        return mOkHttpClient;
    }
}
