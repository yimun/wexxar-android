package me.yimu.wexxar.route;

/**
 * Created by linwei on 2018/3/4.
 */

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Route负责通过uri找到对应的html页面，一条Route包含一个uri的正则匹配规则和一个html地址。
 *
 * {@link #match(String)} 负责匹配uri;
 * {@link #getRemoteFile()} ()} 返回jsbundle地址
 *
 * Created by linwei on 2018/3/4.
 */
public class Route implements Serializable {

    private static final long serialVersionUID = 2l;

    @JSONField(name = "deploy_time")
    public String deployTime;
    @JSONField(name = "remote_file")
    public String remoteFile;
    @JSONField(name = "uri")
    public String uriRegex;

    public Route() {
    }

    /**
     * 匹配传入的uri，如果能匹配上则说明可以用这个html来显示
     *
     * @param url 匹配的uri
     * @return true: 能匹配上  false: 不能匹配上
     */
    public boolean match(String url) {
        try {
            Pattern pattern = Pattern.compile(uriRegex);
            Matcher matcher = pattern.matcher(url);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRemoteFile() {
        return remoteFile;
    }

    @Override
    public int hashCode() {
        return uriRegex.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (!(o instanceof Route)) {
            return false;
        }
        return TextUtils.equals(this.uriRegex, ((Route) o).uriRegex);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
