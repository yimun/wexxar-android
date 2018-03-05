package me.yimu.wexxar.cache;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by linwei on 2018/3/4.
 */

public class CacheEntry {
    public InputStream inputStream;
    public long length;

    public CacheEntry(long length, InputStream inputStream) {
        this.length = length;
        this.inputStream = inputStream;
    }

    public boolean isValid() {
        return null != inputStream;
    }

    public void close() {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
