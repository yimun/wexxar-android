package me.yimu.wexxar.cache;

import android.content.Context;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import me.yimu.wexxar.Constants;
import me.yimu.wexxar.utils.AppContext;
import me.yimu.wexxar.utils.FileUtils;
import me.yimu.wexxar.utils.LogUtils;
import me.yimu.wexxar.utils.PathUtils;

/**
 * Created by linwei on 2018/3/4.
 */

public class FileCache implements ICache {
    
    public static final String TAG = FileCache.class.getSimpleName();

    public FileCache() {
    }

    @Override
    public CacheEntry findCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        File file = file(url);
        if (file.exists() && file.canRead()) {
            byte[] bytes = FileUtils.readFileToBytes(file);
            CacheEntry cacheEntry = new CacheEntry(file.length(), new ByteArrayInputStream(bytes));
            LogUtils.i(TAG, "hit");
            return cacheEntry;
        }
        return null;
    }

    @Override
    public boolean removeCache(String url) {
        LogUtils.i(TAG, "remove cache  : url " + url);
        File file = file(url);
        return file.exists() && file.delete();
    }

    /**
     * 保存文件缓存
     *
     * @param url         jsBundle的url
     * @param bytes jsBundle数据
     */
    public boolean saveCache(String url, byte[] bytes) {
        if (TextUtils.isEmpty(url) || null == bytes || bytes.length == 0) {
            return false;
        }
        File fileDir = fileDir();
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return false;
            }
        }
        // 如果存在，则先删掉之前的缓存
        removeCache(url);
        File saveFile = null;
        try {
            saveFile = file(url);
            OutputStream outputStream = new FileOutputStream(saveFile);
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != saveFile && saveFile.exists()) {
                saveFile.delete();
            }
        }
        return false;
    }

    /**
     * 清除jsBundle缓存
     *
     * @return whether clear cache successfully
     */
    public boolean clear() {
        File jsBundleDir = fileDir();
        if (!jsBundleDir.exists()) {
            return true;
        }
        File[] jsBundleFiles = jsBundleDir.listFiles();
        if (null == jsBundleFiles) {
            return true;
        }
        boolean processed = true;
        for (File file : jsBundleFiles) {
            if (!file.delete()) {
                processed = false;
            }
        }
        return processed;
    }

    /**
     * jsBundle存储目录
     *
     * @return jsBundle存储目录
     */
    public static File fileDir() {
        return AppContext.getInstance().getDir(Constants.CACHE_HOME_DIR,
                Context.MODE_PRIVATE);
    }

    /**
     * 单个jsBundle存储文件路径
     *
     * @param url jsBundle路径
     * @return jsBundle对应的存储文件
     */
    public static File file(String url) {
        String fileName = PathUtils.url2FilePath(url);
        return new File(fileDir(), fileName);
    }
}
