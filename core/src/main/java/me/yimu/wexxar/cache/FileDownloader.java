package me.yimu.wexxar.cache;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.yimu.wexxar.Wexxar;
import me.yimu.wexxar.route.Route;
import me.yimu.wexxar.route.Routes;
import me.yimu.wexxar.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linwei on 2018/3/4.
 */

public class FileDownloader {

    public static final String TAG = FileDownloader.class.getSimpleName();
    public static final List<String> mDownloadingProcess = new ArrayList<>();

    /**
     * 下载html文件
     *
     * @param url
     * @param callback
     */
    private static void doDownloadHtmlFile(String url, Callback callback) {
        LogUtils.i(TAG, "url = " + url);
        Request request = new Request.Builder().url(url)
                .build();
        Wexxar.getOkHttpClient().newCall(request)
                .enqueue(callback);
    }

    /**
     * 下载html文件，然后缓存
     *
     * @param url
     * @param callback
     */
    public static void prepareHtmlFile(final String url, final Callback callback) {
        FileDownloader.doDownloadHtmlFile(url, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        // 1. 存储到本地
                        boolean result = CacheHelper.getInstance().saveFileCache(url, response.body().bytes());
                        // 存储失败，则失败
                        if (!result) {
                            onFailure(call, new IOException("file save fail!"));
                        } else {
                            if (null != callback) {
                                callback.onResponse(call, response);
                            }
                        }
                    } else {
                        onFailure(call, new IOException(String.valueOf(response.code())));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(call, new IOException("file save fail!"));
                    LogUtils.i(TAG, "prepare html fail");
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (null != callback) {
                    callback.onFailure(call, e);
                }
            }
        });
    }

    /**
     * 空闲时间下载html文件
     * // FIXME 考虑并发问题
     */
    public static void prepareHtmlFiles(Routes routes) {
        if (null == routes || routes.isEmpty()) {
            return;
        }
        ArrayList<Route> validRoutes = new ArrayList<>();
        validRoutes.addAll(routes.items);
        validRoutes.addAll(routes.partialItems);
        // 重新下载
        mDownloadingProcess.clear();
        int totalSize = validRoutes.size();
        // 需要下载的route数量
        int newRouteCount = 0;
        LogUtils.i(TAG, "routes:" + JSON.toJSONString(routes));
        LogUtils.i(TAG, "download total count:" + totalSize);
        for (int i = 0; i < totalSize ; i ++) {
            final Route tempRoute = validRoutes.get(i);
            if (CacheHelper.getInstance().findCache(tempRoute.getRemoteFile()) == null) {
                newRouteCount ++;
                if (!mDownloadingProcess.contains(tempRoute.getRemoteFile())) {
                    mDownloadingProcess.add(tempRoute.getRemoteFile());
                    FileDownloader.prepareHtmlFile(tempRoute.getRemoteFile(), new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // 如果下载失败，则不移除
                            LogUtils.i(TAG, "download html failed" + tempRoute.getRemoteFile() + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            mDownloadingProcess.remove(tempRoute.getRemoteFile());
                            LogUtils.i(TAG, "download html success " + tempRoute.getRemoteFile());
                            // 如果全部文件下载成功，则发送校验成功事件
                            if (mDownloadingProcess.isEmpty()) {
                                LogUtils.i(TAG, "download html complete");
//                                BusProvider.getInstance().post(new BusProvider.BusEvent(Constants.BUS_EVENT_ROUTE_CHECK_VALID, null));
                            }
                        }
                    });
                }
            } else {
                LogUtils.i(TAG, "download exist " + tempRoute.getRemoteFile());
                // 如果所有html文件都已经缓存了,也可以更新route
                if (newRouteCount == 0 && i == totalSize - 1) {
//                    BusProvider.getInstance().post(new BusProvider.BusEvent(Constants.BUS_EVENT_ROUTE_CHECK_VALID, null));
                }
            }
        }
        LogUtils.i(TAG, "download new count:" + newRouteCount);
    }
}
