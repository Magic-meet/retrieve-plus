# 学术文献智能解析与检索系统

这是一个面向 PDF 学术文献的解析、存储、索引与检索系统。系统支持文献 PDF 上传、源文件存储、GROBID 全文解析、结构化元数据入库、段落级索引、语义向量生成，以及关键词检索、语义检索和混合检索。

本仓库主要提供一套 Docker Compose 部署环境，包含前端、后端、Python 解析/向量服务，以及 MySQL、Kafka、Canal、Elasticsearch、GROBID、FastDFS 等依赖组件。

## 主要功能

- PDF 文献上传、列表查询、详情查询、源文件下载和删除。
- 基于 FastDFS 保存上传的原始 PDF 文件。
- 基于 Kafka 将大文件分块投递到异步解析流程。
- 基于 GROBID 将 PDF 解析为结构化文献 JSON。
- 基于 MySQL 保存论文、作者、段落、参考文献等结构化数据。
- 基于 Canal 监听 MySQL binlog，并将论文和段落变更同步到 Kafka。
- 基于 Elasticsearch 建立论文级和段落级索引，支持 IK 中文分词。
- 基于 Qwen3 Embedding 生成论文语义向量，支持语义检索。
- 基于 Spring Boot 提供 REST API 和 gRPC 客户端调用。
- 基于 Vue 3 + Vuetify 提供浏览器端文献管理与检索页面。

## 系统架构

核心数据流如下：

1. 用户通过前端或 API 上传 PDF。
2. 后端将源文件写入 FastDFS，同时在 MySQL 中记录上传状态。
3. 后端将 PDF 按 512 KB 分块发送到 Kafka 的 `pdf` topic。
4. Python PDF Worker 消费 `pdf` topic，调用 GROBID 解析 PDF，并将解析后的 JSON 发送到 `json` topic。
5. Spring Boot 消费 `json` topic，将论文、作者、段落、参考文献等数据写入 MySQL。
6. Canal 监听 MySQL 中 `paper`、`paragraph` 表的 binlog，将变更发送到 Kafka 的 `canal` topic。
7. Spring Boot 消费 `canal` topic，同步论文和段落文本到 Elasticsearch。
8. Python Vector Worker 消费 `canal` topic，为论文生成 Qwen3 Embedding，并发送到 `vector` topic。
9. Spring Boot 消费 `vector` topic，将向量写入 Elasticsearch 的 `paper` 索引。
10. 检索时，关键词检索走 Elasticsearch 文本索引；语义检索通过 gRPC 调用 Python RPC 服务生成查询向量，再在 Elasticsearch 中进行向量相似度检索；混合检索融合两种结果。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、Vuetify、Axios、Vue Router、Pinia |
| 后端 | Spring Boot 3.2.1、Java 21、Spring Data JPA、Spring Data Elasticsearch、Spring Kafka、gRPC Client |
| PDF 解析 | Python 3.11、GROBID、grobid2json |
| 向量模型 | Qwen/Qwen3-Embedding-0.6B、Transformers、PyTorch CPU |
| 数据库 | MySQL 8.0 |
| 消息队列 | Apache Kafka 3.9.2，KRaft 单节点模式 |
| 增量同步 | Canal Server 1.1.8 |
| 搜索引擎 | Elasticsearch 8.19.0 + IK Analyzer |
| 文件存储 | FastDFS + Nginx |
| 编排 | Docker Compose |

## 目录结构

```text
.
├── docker-compose.yml              # 完整部署：中间件 + Python 服务 + Java 后端 + Vue 前端
├── docker-compose-no.yml           # 不包含 Java 后端和 Vue 前端的部署文件
├── .env                            # Compose 环境变量
├── DOCKER_DEPLOY_NOTES.md          # 部署补充说明
├── mysql/
│   ├── conf.d/                     # MySQL 配置
│   └── init/                       # 初始化 SQL
├── kafka/
│   └── init/create-topics.sh       # 自动创建 pdf/json/canal/vector topic
├── canal/
│   └── conf/                       # Canal 配置
├── elasticsearch/
│   ├── Dockerfile                  # 构建带 IK Analyzer 的 ES 镜像
│   └── analysis-ik/                # IK 自定义词典配置
├── fastdfs/
│   ├── Dockerfile
│   └── entrypoint.sh
└── project/
    ├── retrieve-java/              # Spring Boot 后端
    ├── retrieve-python/            # PDF 解析、向量生成、gRPC 服务
    └── retrieve-vue/               # Vue 前端
```

## 环境要求

建议使用 Linux 或 macOS 环境运行。Windows 环境建议使用 WSL2。

基础要求：

- Docker Engine 24+。
- Docker Compose v2。
- 至少 8 GB 可用内存；如果同时运行 Elasticsearch、GROBID 和 Qwen3 Embedding，建议 16 GB 以上。
- 首次启动需要下载基础镜像、构建本地镜像并下载 Qwen3 Embedding 模型，网络环境需要能访问 Docker 镜像源、Maven 仓库、npm 仓库和 Hugging Face 或镜像站。

Elasticsearch 在 Linux 上通常需要调大 `vm.max_map_count`：

```bash
sudo sysctl -w vm.max_map_count=262144
```

如需永久生效，可写入 `/etc/sysctl.conf`：

```bash
echo 'vm.max_map_count=262144' | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

## 快速启动

进入项目根目录：

```bash
cd retrieve_docker
```

确认关键脚本存在并具有可执行权限：

```bash
chmod +x kafka/init/create-topics.sh
chmod +x fastdfs/entrypoint.sh
chmod +x project/retrieve-python/docker/*.sh
```

首次部署前建议修改 `.env` 中的默认密码、端口和模型缓存路径，尤其是 `MYSQL_ROOT_PASSWORD`、`HF_HOME`、`HF_ENDPOINT` 等变量。不要将包含真实密码的 `.env` 提交到公开仓库。

构建并启动完整系统：

```bash
docker compose up -d --build
```

查看容器状态：

```bash
docker compose ps
```

查看关键日志：

```bash
docker compose logs -f qwen3-embedding-init retrieve-python-rpc retrieve-python-pdf retrieve-python-vector retrieve-java retrieve-vue
```

首次启动时，`qwen3-embedding-init` 会下载默认的 Qwen3 Embedding 模型。模型下载完成后，`retrieve-python-rpc`、`retrieve-python-pdf`、`retrieve-python-vector` 等服务才会继续启动。

## 访问地址

默认端口来自 `.env`：

| 服务 | 地址 |
| --- | --- |
| 前端页面 | `http://127.0.0.1:5173` |
| 后端 API | `http://127.0.0.1:8080` |
| Elasticsearch | `http://127.0.0.1:9200` |
| GROBID API | `http://127.0.0.1:8070` |
| FastDFS HTTP | `http://127.0.0.1:8888` |
| Kafka 外部端口 | `127.0.0.1:9092` |
| gRPC Embedding 服务 | `127.0.0.1:50051` |

如果通过局域网或内网穿透访问前端，需要修改：

```text
project/retrieve-vue/public/app-config.js
```

例如：

```js
window.__APP_CONFIG__ = {
  apiBaseUrl: "http://你的宿主机IP:8080"
};
```

修改后通常不需要重建前端镜像，只需重启前端容器：

```bash
docker compose restart retrieve-vue
```

## API 示例

### 上传 PDF

```bash
curl -F "file=@example.pdf" \
  http://127.0.0.1:8080/api/v1/documents
```

### 查询文献列表

```bash
curl http://127.0.0.1:8080/api/v1/documents
```

### 查询单篇文献详情

```bash
curl http://127.0.0.1:8080/api/v1/documents/1
```

### 查询文献段落

```bash
curl http://127.0.0.1:8080/api/v1/documents/1/chunks
```

### 下载源文件

```bash
curl -OJ http://127.0.0.1:8080/api/v1/documents/1/source
```

### 删除文献

```bash
curl -X DELETE http://127.0.0.1:8080/api/v1/documents/1
```

### 关键词检索

```bash
curl -X POST http://127.0.0.1:8080/api/v1/retrieval/keyword \
  -H "Content-Type: application/json" \
  -d '{"query":"retrieval augmented generation","topK":5}'
```

### 语义检索

```bash
curl -X POST http://127.0.0.1:8080/api/v1/retrieval/semantic \
  -H "Content-Type: application/json" \
  -d '{"query":"efficient document retrieval with embeddings","topK":5}'
```

### 混合检索

```bash
curl -X POST http://127.0.0.1:8080/api/v1/retrieval/hybrid \
  -H "Content-Type: application/json" \
  -d '{"query":"large language model retrieval system","topK":5}'
```

检索请求字段说明：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `query` | string | 是 | 检索查询文本 |
| `topK` | integer | 否 | 返回结果数量，默认 5 |
| `documentId` | long | 否 | 限定在指定上传文献内检索 |

## Kafka Topic

启动时 `kafka-init` 会自动创建以下 topic：

| Topic | 作用 |
| --- | --- |
| `pdf` | 后端上传 PDF 后，将文件分块写入该 topic |
| `json` | Python PDF Worker 将 GROBID 解析结果写入该 topic |
| `canal` | Canal 将 MySQL binlog 变更写入该 topic |
| `vector` | Python Vector Worker 将论文 embedding 写入该 topic |

查看 topic：

```bash
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:29092 \
  --list
```

## 常用运维命令

查看所有服务状态：

```bash
docker compose ps
```

查看某个服务日志：

```bash
docker compose logs -f retrieve-java
```

重启某个服务：

```bash
docker compose restart retrieve-java
```

停止全部服务：

```bash
docker compose down
```

停止并删除本地构建镜像：

```bash
docker compose down --rmi local
```

清理持久化数据需要谨慎操作。以下目录包含运行时数据，删除后数据不可恢复：

```text
mysql/data/
kafka/data/
elasticsearch/data/
fastdfs/data/
canal/logs/
huggingface/
```

## 分模块开发

### 后端 retrieve-java

后端位于：

```text
project/retrieve-java
```

主要配置文件：

```text
project/retrieve-java/src/main/resources/application.yml
```

主要接口入口：

```text
project/retrieve-java/src/main/java/edu/njucm/retrievejava/controller/DocumentController.java
```

如需本地运行，需要提前启动 MySQL、Kafka、Elasticsearch、FastDFS 和 Python gRPC 服务，并配置对应环境变量。Docker 环境下由 `docker-compose.yml` 自动注入这些变量。

### Python 服务 retrieve-python

Python 模块位于：

```text
project/retrieve-python
```

包含三类运行入口：

| 服务 | 启动脚本 | 作用 |
| --- | --- | --- |
| `retrieve-python-rpc` | `docker/start-rpc.sh` | 提供 gRPC 查询向量生成服务 |
| `retrieve-python-pdf` | `docker/start-pdf-worker.sh` | 消费 PDF 分块，调用 GROBID 解析 |
| `retrieve-python-vector` | `docker/start-vector-worker.sh` | 消费 Canal 变更，生成论文 embedding |

默认模型为：

```text
Qwen/Qwen3-Embedding-0.6B
```

如果要替换 embedding 模型，需要同时确认以下内容：

1. `.env` 中的 `QWEN3_EMBEDDING_MODEL`。
2. `project/retrieve-python/docker/download_qwen3_embedding.py` 中初始化下载的模型名。
3. Elasticsearch 中 `PaperES.embedding` 的向量维度配置。当前代码中维度为 `1024`。

### 前端 retrieve-vue

前端位于：

```text
project/retrieve-vue
```

本地开发：

```bash
cd project/retrieve-vue
npm install
npm run dev
```

生产构建：

```bash
npm run build-only
```

前端 API 地址由以下文件控制：

```text
project/retrieve-vue/public/app-config.js
```

## 常见问题

### 1. Elasticsearch 启动失败

先检查宿主机参数：

```bash
sudo sysctl -w vm.max_map_count=262144
```

再检查数据目录权限。如果 `elasticsearch/data` 是从其他机器拷贝过来的，可能存在权限问题。可以在确认不需要旧数据后清空该目录，或者调整目录权限。

### 2. Kafka topic 没有创建

检查初始化脚本是否存在且不是目录：

```bash
ls -l kafka/init/create-topics.sh
```

如果该路径被误创建成目录，需要删除目录并恢复脚本文件。正常情况下 `kafka-init` 容器会在 Kafka 健康后自动创建 `pdf`、`json`、`canal`、`vector` 四个 topic。

### 3. 前端能打开，但请求仍然访问 `127.0.0.1:8080`

修改：

```text
project/retrieve-vue/public/app-config.js
```

将 `apiBaseUrl` 改为浏览器实际可访问的后端地址。例如局域网访问时应使用宿主机局域网 IP，而不是容器名或 `127.0.0.1`。

修改后重启前端容器：

```bash
docker compose restart retrieve-vue
```

### 4. 上传后长时间处于解析中

按顺序检查：

```bash
docker compose logs -f retrieve-java
docker compose logs -f retrieve-python-pdf
docker compose logs -f grobid
docker compose logs -f kafka
```

重点确认：

- `pdf` topic 是否收到文件分块。
- `retrieve-python-pdf` 是否成功连接 GROBID。
- GROBID 是否健康。
- `json` topic 是否有解析结果。
- Spring Boot 是否成功消费 `json` topic 并写入 MySQL。

### 5. 语义检索无结果或 embedding 为空

检查：

```bash
docker compose logs -f qwen3-embedding-init retrieve-python-rpc retrieve-python-vector retrieve-java
```

重点确认：

- 模型是否下载完成。
- `retrieve-python-rpc` 是否启动成功。
- `retrieve-python-vector` 是否消费到 `canal` topic。
- `vector` topic 是否有消息。
- Elasticsearch `paper` 索引中的 `embedding` 字段是否已写入。

### 6. FastDFS 无法下载源文件

检查 FastDFS 服务：

```bash
docker compose logs -f fastdfs
```

并确认后端环境变量中 Tracker 地址为：

```text
FASTDFS_TRACKER_SERVERS=fastdfs:22122
```

在 Docker Compose 网络内，应使用服务名 `fastdfs`，不要使用 `127.0.0.1`。

## 部署注意事项

- 当前 Compose 更适合单机开发、实验和课程项目部署，不建议直接作为生产环境配置。
- `.env` 中的密码和端口应按实际环境修改。
- Elasticsearch 关闭了 xpack security，默认不启用认证；公网部署时必须放在内网或增加网关鉴权。
- MySQL、Kafka、Elasticsearch、FastDFS 的数据目录是持久化目录，迁移或清理前应先备份。
- `docker-compose-no.yml` 只启动中间件和 Python 相关服务，不包含 Java 后端和 Vue 前端，适合只调试基础设施或单独在宿主机运行后端/前端。


