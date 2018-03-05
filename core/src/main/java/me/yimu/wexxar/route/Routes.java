package me.yimu.wexxar.route;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linwei on 2018/3/4.
 */

public class Routes {

    public int count;
    @JSONField(name = "deploy_time")
    public String deployTime;
    @JSONField(name = "items")
    public List<Route> items = new ArrayList<>();
    @JSONField(name = "partial_items")
    public List<Route> partialItems = new ArrayList<>();

    public Routes() {
    }

    /**
     * @return  Routes是否为空
     */
    public boolean isEmpty() {
        return (null == items || items.isEmpty()) && (null == partialItems || partialItems.isEmpty());
    }
}