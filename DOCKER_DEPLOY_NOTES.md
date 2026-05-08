# Docker 部署说明

## 1. FastDFS

后端 FastDFS Tracker 地址已改为从 Spring 配置读取，默认值为：

```yaml
fastdfs.tracker-servers: ${FASTDFS_TRACKER_SERVERS:fastdfs:22122}
```

在 `docker-compose.yml` 中，`retrieve-java` 已默认注入：

```yaml
FASTDFS_TRACKER_SERVERS: fastdfs:22122
```

## 2. 前端 API 地址配置

前端不再把后端地址写死在代码里，而是从以下文件读取：

```text
project/retrieve-vue/public/app-config.js
```

内容示例：

```js
window.__APP_CONFIG__ = {
  apiBaseUrl: "http://127.0.0.1:8080"
};
```

你可以按部署场景修改为：

- 本机直连：`http://127.0.0.1:8080`
- 局域网访问：`http://你的宿主机IP:8080`
- 内网穿透：`https://你的公网域名`

`docker-compose.yml` 已将该文件直接挂载到前端容器内，因此修改后通常无需重建镜像，只需重启前端容器即可。

## 3. 已修复的硬编码地址

- Java FastDFS 客户端：`127.0.0.1:22122` -> 配置化
- Vue Axios 默认地址：`http://127.0.0.1:8080` -> `app-config.js`
- Vue 下载源文件按钮地址：`http://127.0.0.1:8080/...` -> `app-config.js`
- Vue 接口文档页 curl 示例地址：`http://127.0.0.1:8080/...` -> `app-config.js`
- Python Kafka 默认地址：`127.0.0.1:29092` -> `kafka:29092`
- Python GROBID 默认地址：`127.0.0.1` -> `grobid`
