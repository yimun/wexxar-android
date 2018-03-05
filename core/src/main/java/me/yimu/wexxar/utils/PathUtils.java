package me.yimu.wexxar.utils;

import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * Created by linwei on 2018/3/4.
 */

public class PathUtils {

    /**
     * https://raw.githubusercontent.com/yimun/wexxar/master/dist-prod/views/HelloWorld-d8560a4457a0dc80c634.bundle.js
     * -> views/HelloWorld-d8560a4457a0dc80c634.bundle.js
     * @param url
     * @return
     */
    public static String url2FilePath(String url) {
        List<String> pathSegments = Uri.parse(url)
                .getPathSegments();
        // 没有path无法命中asset缓存
        if (null == pathSegments) {
            return null;
        }

        StringBuilder pathStringBuilder = new StringBuilder();
        int size = pathSegments.size();
        if (size > 1) {
            pathStringBuilder.append(pathSegments.get(size - 2)).append(File.separator);
        }
        pathStringBuilder.append(pathSegments.get(size - 1));
        return pathStringBuilder.toString();

    }
}
