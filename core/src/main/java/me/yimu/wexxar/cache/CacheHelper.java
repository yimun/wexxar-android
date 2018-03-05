package me.yimu.wexxar.cache;

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
     * Just save html file
     *
     * @param url
     * @param bytes
     */
    public boolean saveFileCache(String url, byte[] bytes) {
        return true;
        /*if (TextUtils.isEmpty(url) || null == bytes || bytes.length == 0) {
            return false;
        }
        if (!checkUrl(url)) {
            return true;
        }
        if (!checkHtmlFile(url, bytes)) {
            LogUtils.i(TAG, "html file check fail : url: " + url + ", bytes md5: " + MD5Utils.getMd5(bytes));
            return false;
        }
        return mInternalHtmlCache.saveCache(url, bytes);*/
    }

    // 建议html文件的命名规则是：%filename%-%hash code%.html
    /*private boolean checkHtmlFile(String url, byte[] bytes) {
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
    }*/
}
