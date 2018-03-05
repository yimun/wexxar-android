package me.yimu.wexxar.route;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.InputStream;

import me.yimu.wexxar.Constants;
import me.yimu.wexxar.cache.FileDownloader;
import me.yimu.wexxar.utils.AppContext;
import me.yimu.wexxar.utils.FileUtils;
import me.yimu.wexxar.utils.LogUtils;

/**
 * 管理route文件
 *
 * Created by linwei on 2018/3/4.
 */

public class RouteManager implements FileDownloader.DownloadCallback {

    public static final String TAG = RouteManager.class.getSimpleName();

    public static class RouteConfig {
        public String routeApi;
        public String routeCacheFileName;

        public RouteConfig(String routeApi, String cacheFileName) {
            this.routeApi = routeApi;
            this.routeCacheFileName = cacheFileName;
        }
    }

    public interface RouteRefreshCallback {
        /**
         * @param data raw data
         */
        void onSuccess(String data);

        void onFail();
    }

    public interface UriHandleCallback {
        void onResult(boolean handle);
    }

    private static RouteManager sInstance;
    private static RouteConfig sRouteConfig;
    private HandlerThread mHT;
    private Handler mWorker;

    /**
     * @param asyncLoadRoute 异步加载route, 默认为true
     */
    private RouteManager(boolean asyncLoadRoute) {
        mHT = new HandlerThread("route_worker");
        mHT.start();
        mWorker = new Handler(mHT.getLooper());
        loadLocalRoutes(asyncLoadRoute);
    }

    /**
     * 缓存Route列表
     */
    private Routes mRoutes;

    // test
    private String mRouteSource = "null";

    /**
     * 待校验的route数据
     */
    private String mCheckingRouteString;

    /**
     * 等待route刷新的callback
     */
    private RouteRefreshCallback mRouteRefreshCallback;

    /**
     * 配置Route 策略
     *
     * @param routeConfig 策略
     */
    public static void config(RouteConfig routeConfig) {
        if (null != routeConfig) {
            sRouteConfig = routeConfig;
            if (!TextUtils.isEmpty(sRouteConfig.routeApi)) {
                RouteFetcher.setRouteApi(sRouteConfig.routeApi);
            }
        }
    }

    public static RouteManager getInstance() {
        return getInstance(true);
    }

    public static RouteManager getInstance(boolean asyncLoadRoute) {
        if (null == sInstance) {
            synchronized (RouteManager.class) {
                if (null == sInstance) {
                    sInstance = new RouteManager(asyncLoadRoute);
                }
            }
        }
        return sInstance;
    }

    public Routes getRoutes() {
        return mRoutes;
    }

    public String getRouteSource() {
        return mRouteSource;
    }

    /**
     * 设置获取routes地址
     */
    public void setRouteApi(String routeUrl) {
        if (!TextUtils.isEmpty(routeUrl)) {
            RouteFetcher.setRouteApi(routeUrl);
        }
    }

    /**
     * 加载本地的route
     * 1. 优先加载本地缓存；
     * 2. 如果没有本地缓存，则加载asset中预置的routes
     *
     * @param asyncLoadRoute 异步加载route, 默认为true
     */
    private void loadLocalRoutes(boolean asyncLoadRoute) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // load cached routes
                try {
                    String routeContent = readCachedRoutes();
                    if (!TextUtils.isEmpty(routeContent)) {
                        mRoutes = JSON.parseObject(routeContent, Routes.class);
                        mRouteSource = "cache: " + mRoutes.deployTime;
                        File cachedRouteFile = getCachedRoutesFile();
                        if (null != cachedRouteFile) {
                            mRouteSource += cachedRouteFile.getAbsolutePath();
                        }
                    }
                } catch (Exception e) {
                    LogUtils.i(TAG, e.getMessage());
                }

                // load preset routes
                if (null == mRoutes || mRoutes.isEmpty()) {
                    try {
                        String routeContent = readPresetRoutes();
                        if (!TextUtils.isEmpty(routeContent)) {
                            mRoutes = JSON.parseObject(routeContent, Routes.class);
                            mRouteSource = "preset" + ":" + mRoutes.deployTime;
                        }
                    } catch (Exception e) {
                        LogUtils.i(TAG, e.getMessage());
                    }
                }
            }
        };
        // 支持异步加载和同步加载
        if (asyncLoadRoute) {
            mWorker.post(runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * 以string方式返回Route列表, 如果Route为空则返回null
     *
     */
    public String getRoutesString() {
        if (null == mRoutes) {
            return null;
        }
        return JSON.toJSONString(mRoutes);
    }

    /**
     * 找到能够解析uri的Route
     *
     * @param uri 需要处理的uri
     * @return 能够处理uri的Route，如果没有则为null
     */
    public Route findRoute(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        if (null == mRoutes) {
            return null;
        }
        if (null == mRoutes.items || mRoutes.items.size() == 0) {
            return null;
        }
        for (Route route : mRoutes.items) {
            if (route.match(uri)) {
                return route;
            }
        }
        return null;
    }

    /**
     * 刷新路由,检查bundle有效之后route再生效
     */
    public void refreshRoute(final RouteRefreshCallback callback) {
        mRouteRefreshCallback = callback;
        RouteFetcher.fetchRoutes(new RouteRefreshCallback() {
            @Override
            public void onSuccess(String data) {
                mCheckingRouteString = data;
                // prepare bundle files
                try {
                    Routes routes = JSON.parseObject(mCheckingRouteString, Routes.class);
                    FileDownloader.prepareBundleFiles(routes, RouteManager.this);
                } catch (Exception e) {
                    // FIXME: 解析失败，不会更新
                    LogUtils.e(TAG, e.getMessage());
                    if (null != callback) {
                        callback.onFail();
                    }
                }
            }

            @Override
            public void onFail() {
                // FIXME: 请求失败，不会更新
                if (null != callback) {
                    callback.onFail();
                }
            }
        });
    }

    /**
     * 不校验bundle文件是否存在, 直接跟新route,避免route更新不及时引入的问题
     *
     * @param callback
     */
    public void refreshRouteFast(final RouteRefreshCallback callback) {
        mRouteRefreshCallback = callback;
        RouteFetcher.fetchRoutes(new RouteRefreshCallback() {
            @Override
            public void onSuccess(String data) {
                try {
                    mCheckingRouteString = null;
                    saveCachedRoutes(data);
                    mRoutes = JSON.parseObject(data, Routes.class);
                    if (null != callback) {
                        callback.onSuccess(data);
                    }
                    // prepare html files
                    FileDownloader.prepareBundleFiles(mRoutes, null);
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    if (null != callback) {
                        callback.onFail();
                    }
                }
            }

            @Override
            public void onFail() {
                if (null != callback) {
                    callback.onFail();
                }
            }
        });
    }

    /**
     * 删除缓存的Routes
     */
    public boolean deleteCachedRoutes() {
        File file = getCachedRoutesFile();
        if (null == file) {
            return false;
        }
        boolean result = file.exists() && file.delete();
        if (result) {
            loadLocalRoutes(true);
        }
        return result;
    }

    /**
     * 存储缓存的Routes
     * @param content route内容
     */
    private void saveCachedRoutes(final String content) {
        mWorker.post(new Runnable() {
            @Override
            public void run() {
                File file = getCachedRoutesFile();
                if (null == file) {
                    return;
                }
                if (file.exists()) {
                    file.delete();
                }
                if (TextUtils.isEmpty(content)) {
                    // 如果内容为空，则只删除文件
                    return;
                }
                FileUtils.writeStringToFile(file, content);
            }
        });
    }

    /**
     * @return 读取缓存的route
     */
    private String readCachedRoutes() {
        File file = getCachedRoutesFile();
        if (null == file || !file.exists()) {
            return null;
        }
        return FileUtils.readFileToString(file);
    }

    /**
     * @return 读取preset routes
     */
    private String readPresetRoutes() {
        try {
            AssetManager assetManager = AppContext.getInstance()
                    .getAssets();
            InputStream inputStream = assetManager.open(Constants.PRESET_ROUTE_FILE_PATH);
            return FileUtils.streamToString(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 存储文件路径
     *
     * @return
     */
    private File getCachedRoutesFile() {
        File fileDir = AppContext.getInstance().getDir(Constants.CACHE_HOME_DIR,
                Context.MODE_PRIVATE);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String cacheFileName = (null != sRouteConfig && !TextUtils.isEmpty(sRouteConfig.routeCacheFileName)) ? sRouteConfig.routeCacheFileName : null;
        if (TextUtils.isEmpty(cacheFileName)) {
            return null;
        }
        File file = new File(fileDir, cacheFileName);
        LogUtils.i(TAG, file.getAbsolutePath());
        return file;
    }

    /**
     * 通过本地的Routes能否处理uri
     *
     * @return
     */
    public boolean handleByNative(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return false;
        }
        return findRoute(uri) != null;
    }

    /**
     * 如果本地的Routes不能处理uri，会尝试更新Routes来处理
     *
     * @return
     */
    public void handleRemote(final String uri, final UriHandleCallback callback) {
        if (null == callback) {
            return;
        }
        RouteManager.getInstance().refreshRoute(new RouteManager.RouteRefreshCallback() {
            @Override
            public void onSuccess(String data) {
                callback.onResult(handleByNative(uri));
            }

            @Override
            public void onFail() {
                callback.onResult(false);
            }
        });
    }

    @Override
    public void onDownloadSuccess() {
        saveCachedRoutes(mCheckingRouteString);
        try {
            mRoutes = JSON.parseObject(mCheckingRouteString, Routes.class);
            mRouteSource = "refresh" + ":" + mRoutes.deployTime;
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        }
        if (null != mRouteRefreshCallback) {
            mRouteRefreshCallback.onSuccess(mCheckingRouteString);
        }
        LogUtils.i(TAG, "new route effective");
    }
}
