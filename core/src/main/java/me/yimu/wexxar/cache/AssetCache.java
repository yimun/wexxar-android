package me.yimu.wexxar.cache;

import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import me.yimu.wexxar.Constants;
import me.yimu.wexxar.utils.AppContext;
import me.yimu.wexxar.utils.LogUtils;
import me.yimu.wexxar.utils.PathUtils;

/**
 *
 * 预置到asset中的只读cache
 *
 * Created by linwei on 2018/3/4.
 */

public class AssetCache implements ICache {

    public static final String TAG = "AssetCache";

    public static AssetCache getInstance(String filePath) {
        return new AssetCache(filePath);
    }

    public static AssetCache getInstance() {
        return new AssetCache(null);
    }

    private String mFilePath;

    private AssetCache(String filePath) {
        mFilePath = filePath;
        if (TextUtils.isEmpty(mFilePath)) {
            mFilePath = Constants.DEFAULT_ASSET_FILE_PATH;
        }
    }

    @Override
    public CacheEntry findCache(String url) {
        // url为空，返回空
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        // 不包含目录层级，返回空
        if (!url.contains(File.separator)) {
            return null;
        }

        StringBuilder pathStringBuilder = new StringBuilder();
        pathStringBuilder.append(mFilePath).append(File.separator)
                .append(PathUtils.url2FilePath(url));

        AssetManager assetManager = AppContext.getInstance()
                .getResources()
                .getAssets();
        try {
            InputStream inputStream = assetManager.open(pathStringBuilder.toString());
            CacheEntry cacheEntry = new CacheEntry(0, inputStream);
            LogUtils.i(TAG, "hit");
            return cacheEntry;
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public boolean removeCache(String url) {
        // do nothing
        return true;
    }
}
