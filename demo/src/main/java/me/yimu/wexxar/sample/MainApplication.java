package me.yimu.wexxar.sample;

import android.app.Application;

import com.taobao.weex.InitConfig;
import com.taobao.weex.WXSDKEngine;

import me.yimu.wexxar.BuildConfig;
import me.yimu.wexxar.Wexxar;
import me.yimu.wexxar.route.RouteManager;
import okhttp3.OkHttpClient;

/**
 * Created by linwei on 2018/3/5.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitConfig config=new InitConfig.Builder().setImgAdapter(new ImageAdapter()).build();
        WXSDKEngine.initialize(this, config);
        Wexxar.initialize(this, true, new OkHttpClient(),
                new RouteManager.RouteConfig(
                        "https://raw.githubusercontent.com/yimun/wexxar/master/dist-prod/routes.json",
                        "routes.json"
                ));
        Wexxar.setDebug(BuildConfig.DEBUG);
    }

}
