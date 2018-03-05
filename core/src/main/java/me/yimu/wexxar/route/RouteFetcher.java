package me.yimu.wexxar.route;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import me.yimu.wexxar.Wexxar;
import me.yimu.wexxar.utils.FileUtils;
import me.yimu.wexxar.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * 根据route地址，请求route
 * <p>
 * 返回route的原始内容
 * </p>
 *
 * Created by linwei on 2018/3/4.
 */

public class RouteFetcher {


    public static final String TAG = RouteFetcher.class.getSimpleName();

    /**
     * 更新Routes的远程地址
     */
    private static String sRouteApi;

    /**
     * 设置获取routes地址
     */
    public static void setRouteApi(String routeUrl) {
        sRouteApi = routeUrl;
    }

    /**
     * 请求route file
     *
     * @param callback
     */
    public static void fetchRoutes(final RouteManager.RouteRefreshCallback callback) {
        // 如果url为空，fail
        if (TextUtils.isEmpty(sRouteApi)) {
            callback.onFail();
            return;
        }
        Uri uri = Uri.parse(sRouteApi);
        if (TextUtils.equals(uri.getScheme(), "file")) {
            // file协议
            localFile(callback);
        } else {
            // http协议
            remoteFile(callback);
        }
    }

    /**
     * file协议
     *
     * @param callback
     */
    private static void localFile(final RouteManager.RouteRefreshCallback callback) {
        try {
            File file = new File(sRouteApi);
            if (!file.exists()) {
                // 文件不存在，fail
                notifyFail(callback);
                return;
            }
            String data = FileUtils.readFileToString(file);
            if (TextUtils.isEmpty(data)) {
                // 如果内容为空，fail
                notifyFail(callback);
            } else {
                notifySuccess(callback, data);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            notifyFail(callback);
        }
    }

    /**
     * http协议
     *
     * @param callback
     */
    private static void remoteFile(final RouteManager.RouteRefreshCallback callback) {
        try {
            Request.Builder builder = new Request.Builder().url(sRouteApi);
            // user-agent
            Wexxar.getOkHttpClient().newCall(builder.build())
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtils.i(TAG, e.getMessage());
                            notifyFail(callback);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                String data = new String(response.body().bytes());
                                notifySuccess(callback, data);
                            } else {
                                notifyFail(callback);
                            }
                        }
                    });
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            notifyFail(callback);
        }
    }

    private static void notifyFail(RouteManager.RouteRefreshCallback callback) {
        if (null != callback) {
            callback.onFail();
        }
    }

    private static void notifySuccess(RouteManager.RouteRefreshCallback callback, String data) {
        if (null != callback) {
            callback.onSuccess(data);
        }
    }

}
