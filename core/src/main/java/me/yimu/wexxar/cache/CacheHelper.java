package me.yimu.wexxar.cache;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import me.yimu.wexxar.utils.LogUtils;
import me.yimu.wexxar.utils.MD5Utils;

/**
 * Created by linwei on 2018/3/4.
 */

public class CacheHelper implements ICache {

    public static final String TAG = CacheHelper.class.getSimpleName();

    private static CacheHelper sInstance;

    private CacheHelper() {
        if (null == mAssetCache) {
            mAssetCache = AssetCache.getInstance();
        }
        if (null == mFileCache) {
            mFileCache = new FileCache();
        }
    }

    public static CacheHelper getInstance() {
        if (null == sInstance) {
            synchronized (CacheHelper.class) {
                if (null == sInstance) {
                    sInstance = new CacheHelper();
                }
            }
        }
        return sInstance;
    }

    private AssetCache mAssetCache;
    private FileCache mFileCache;


    @Override
    public CacheEntry findCache(String url) {
        CacheEntry entry = null;
        entry = mFileCache.findCache(url);
        if (entry != null) {
            return entry;
        }
        entry = mAssetCache.findCache(url);
        return entry;
    }

    @Override
    public boolean removeCache(String url) {
        return mFileCache.removeCache(url);
    }

    /**
     * Just save bundle file
     *
     * @param url
     * @param bytes
     */
    public boolean saveFileCache(String url, byte[] bytes) {
        if (TextUtils.isEmpty(url) || null == bytes || bytes.length == 0) {
            return false;
        }
        if (!checkUrl(url)) {
            return true;
        }
        if (!checkBundleFile(url, bytes)) {
            LogUtils.i(TAG, "bundle file check fail : url: " + url + ", bytes md5: " + MD5Utils.getMd5(bytes));
            return false;
        }
        return mFileCache.saveCache(url, bytes);
    }

    // 建议bundle文件的命名规则是：%filename%-%hash code%.bundle
    private boolean checkBundleFile(String url, byte[] bytes) {
        String fileName = Uri.parse(url).getLastPathSegment();
        try {
            String hashCode = fileName.split("\\.")[0].split("-")[1];
            // 提取到hash code
            if (!TextUtils.isEmpty(hashCode)) {
                return MD5Utils.getMd5(bytes).startsWith(hashCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    public boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            LogUtils.i(TAG, "can not cache, url = " + url);
            return false;
        }

        // 获取文件名
        String fileName;
        if (!url.contains(File.separator)) {
            fileName = url;
        } else {
            fileName = Uri.parse(url)
                    .getLastPathSegment();
            if (TextUtils.isEmpty(fileName)) {
                fileName = Uri.parse(url)
                        .getHost();
            }
        }
        // 如果文件名为空，则不能缓存
        if (TextUtils.isEmpty(fileName)) {
            LogUtils.i(TAG, "can not cache, fileName is null, url = " + url);
            return false;
        }
        // 如果文件名不为空，且后缀为能够缓存的类型，则可以缓存
        if (fileName.endsWith("js")) {
            LogUtils.i(TAG, "can cache url = " + url);
            return true;
        }
        // 默认不能缓存
        LogUtils.i(TAG, "can not cache, extension not match, url = " + url);
        return false;
    }

}
