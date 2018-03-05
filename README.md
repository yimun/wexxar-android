# wexxar-android

基于 [weex](https://github.com/apache/incubator-weex) 的页面级混合开发方案，通过路由表控制客户端缓存，无需发版即可实现热更新


## 关联项目：
- [wexxar](https://github.com/yimun/wexxar) wexxar的前端工程，主要增加了路由表生成和jsbundle部署


## 路由表实例
```json
{
  "deploy_time": "Fri, 02 Mar 2018 10:09:48 GMT",
  "items": [
    {
      "deploy_time": "Fri, 02 Mar 2018 10:09:48 GMT",
      "remote_file": "https://raw.githubusercontent.com/yimun/wexxar/master/dist-prod/views/HelloWorld-d8560a4457a0dc80c634.bundle.js",
      "uri": "wexxar://wexxar.com/main"
    },
    {
      "deploy_time": "Fri, 02 Mar 2018 09:40:26 GMT",
      "remote_file": "https://raw.githubusercontent.com/yimun/wexxar/master/dist-prod/views/Profile-972dd16eb4b7476478e1.bundle.js",
      "uri": "wexxar://wexxar.com/profile"
    }
  ]
}
```
路由表以页面为单位，其中`remote_file`为jsbundle资源CDN路径，`uri`为页面对应的deeplink

## 动态更新实现
1. Android工程内置submodule项目wexxar，每次编译时自动把`wexxar/dist-prod`下的最新资源文件打包到项目`assets/wexxar`文件夹下，作为应用初始缓存
2. 每次启动应用时刷新最新的路由表文件，比对发现页面资源有更新时，在后台自动下载最新jsbundle到文件缓存中，当所有资源下载成功后，更新路由表
3. 文件缓存优先级大于assets缓存，路由表`routes.json`文件同理
4. 应用内存中保存最新的路由表文件，内外部应用可通过uri调起页面，如果该uri匹配到路由，则读取jsbundle跳转对应页面


WIP。。。

## LICENSE
Wexxar is released under the MIT license. See [LICENSE](LICENSE) for details











