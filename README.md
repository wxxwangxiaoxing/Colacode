<<<<<<< HEAD
# ColaCode

ColaCode 是一个基于 **Spring Cloud + Spring Boot 3** 的微服务项目，围绕题库、练习、面试、社区、认证、文件存储与微信集成等场景构建，同时提供独立的 AI 服务用于面试题生成与答案评分。

仓库当前同时包含：
- 后端 Maven 多模块工程
- 前端 `Vue 3 + Vite` 工程
- Nacos 配置示例
- SQL 初始化脚本
- Docker Compose 部署文件
- 各模块设计与联调文档

## 技术栈

### 后端
- Java 17
- Spring Boot 3.4.0
- Spring Cloud 2024.0.0
- Spring Cloud Alibaba / Nacos
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring AI
- MyBatis-Plus
- Sa-Token
- Redis
- MySQL
- RocketMQ
- Elasticsearch
- MinIO
- SpringDoc OpenAPI
- Sentinel

### 前端
- Vue 3
- Vite
- Pinia
- Vue Router
- Axios
- Ant Design Vue
- pnpm

## 仓库结构

```text
colacode/
├── colacode-gateway/      # 网关服务
├── colacode-auth/         # 认证与权限服务
├── colacode-subject/      # 题库服务
├── colacode-practice/     # 练习服务
├── colacode-interview/    # 面试服务
├── colacode-circle/       # 社区服务
├── colacode-oss/          # 对象存储服务
├── colacode-wx/           # 微信集成服务
├── colacode-ai/           # AI 服务
├── colacode-common/       # 公共模块
├── colacode-web/          # 前端工程
├── deploy/                # Docker Compose 部署文件
└── doc/                   # 文档、SQL、Nacos 配置示例
```

## 模块说明

| 模块 | 说明 | 默认端口 |
| --- | --- | --- |
| `colacode-gateway` | API 网关，负责统一入口、路由转发、鉴权放行、限流、聚合 OpenAPI | `5000` |
| `colacode-auth` | 用户、角色、权限、登录与 RBAC 能力 | `3011` |
| `colacode-subject` | 题库、分类、标签、搜索、点赞/同步等能力 | `3010` |
| `colacode-practice` | 练习与排行榜相关能力 | `3013` |
| `colacode-interview` | 面试流程、会话管理、答题评估、AI 面试接入 | `3015` |
| `colacode-circle` | 社区、动态、消息、敏感词等能力 | `3014` |
| `colacode-oss` | 文件上传下载与对象存储适配 | `4000` |
| `colacode-wx` | 微信相关集成能力 | `3012` |
| `colacode-ai` | AI 题目生成、答案评分服务 | `3020` |
| `colacode-common` | 公共返回体、上下文、拦截器、通用常量与基础组件 | - |
| `colacode-web` | 前端项目，提供 Web 端页面 | `5173` |

## 核心架构约定

### 1. Maven 多模块后端
根工程 `pom.xml` 是聚合工程，统一管理依赖版本与模块：
- Java 17
- Spring Boot 3.4.0
- Spring Cloud 2024.0.0
- Spring Cloud Alibaba 2023.0.1.0

### 2. Nacos 作为注册中心与配置中心
项目大量服务默认通过 Nacos 完成：
- 服务注册发现
- 共享配置加载
- 服务级配置加载

常见共享配置：
- `common-datasource.yaml`
- `common-redis.yaml`
- `common-rocketmq.yaml`

参考文档：`doc/nacos-config/README.md`

### 3. 通过网关统一访问后端服务
网关模块聚合了各业务服务，典型访问前缀包括：
- `/auth/**`
- `/subject/**`
- `/practice/**`
- `/circle/**`
- `/interview/**`
- `/oss/**`
- `/wx/**`

因此本地或部署环境中，通常优先从网关入口访问接口：
- `http://localhost:5000`

### 4. 面试服务可接入独立 AI 服务
`colacode-interview` 已支持通过配置接入 `colacode-ai`：
- AI 题目生成
- AI 答案评分
- 面试流程中的 AI 语义评估

相关配置项示例：
- `INTERVIEW_AI_ENABLED`
- `INTERVIEW_AI_API_KEY`
- `INTERVIEW_AI_SERVICE_NAME`
- `INTERVIEW_AI_BASE_URL`

## 开发环境要求

建议准备如下环境：
- JDK 17
- Maven 3.9+
- Node.js 18+
- pnpm
- MySQL
- Redis
- Nacos

按需准备以下中间件：
- Elasticsearch
- RocketMQ
- MinIO

## 快速开始

### 方式一：使用 Docker Compose 启动基础环境与后端镜像

项目提供了部署文件：
- `deploy/docker-compose.yml`

可用于启动：
- gateway
- auth
- subject
- practice
- circle
- interview
- oss
- wx
- mysql
- redis
- nacos
- elasticsearch
- minio
- rocketmq

说明：当前 `deploy/docker-compose.yml` 主要覆盖后端服务与基础设施，不包含 `colacode-web` 前端，也未定义 `colacode-ai` 服务容器。

示例：

```bash
cd deploy
docker compose up -d
```

启动后可重点访问：
- 网关：`http://localhost:5000`
- Nacos：`http://localhost:8848/nacos`
- MinIO Console：`http://localhost:9001`
- Elasticsearch：`http://localhost:9200`

### 方式二：本地源码启动

### 1. 初始化数据库
SQL 文件位于：
- `doc/sql/colacode-init.sql`
- `doc/sql/20260410-add-subject-browse-count.sql`

也可以参考：
- `doc/colacode-db-optimized.sql`
- `doc/interview-phase1-schema.sql`

### 2. 准备 Nacos 配置
参考：
- `doc/nacos-config/README.md`
- `doc/nacos-config/common-datasource.yaml`
- `doc/nacos-config/common-redis.yaml`
- `doc/nacos-config/common-rocketmq.yaml`

常见环境变量：

```bash
set NACOS_SERVER_ADDR=127.0.0.1:8848
set NACOS_NAMESPACE=dev
set NACOS_GROUP=DEFAULT_GROUP
```

也兼容 Spring 标准写法：

```bash
set SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=127.0.0.1:8848
set SPRING_CLOUD_NACOS_CONFIG_SERVER_ADDR=127.0.0.1:8848
```

### 3. 启动后端模块
可在根目录执行：

```bash
mvn clean install
```

按需单独启动模块，例如：

```bash
mvn -pl colacode-gateway spring-boot:run
mvn -pl colacode-auth spring-boot:run
mvn -pl colacode-subject spring-boot:run
mvn -pl colacode-practice spring-boot:run
mvn -pl colacode-interview spring-boot:run
mvn -pl colacode-circle spring-boot:run
mvn -pl colacode-oss spring-boot:run
mvn -pl colacode-wx spring-boot:run
mvn -pl colacode-ai spring-boot:run
```

建议启动顺序：
1. MySQL / Redis / Nacos
2. Elasticsearch / RocketMQ / MinIO（按需）
3. `colacode-auth`
4. 其他业务服务
5. `colacode-gateway`
6. `colacode-web`

### 4. 启动前端
前端位于 `colacode-web`，使用 pnpm：

```bash
cd colacode-web
pnpm install
pnpm dev
```

默认前端地址：
- `http://localhost:5173`

其他命令：

```bash
pnpm build
pnpm preview
```

## 接口文档

网关已聚合 OpenAPI 文档，默认可通过以下地址查看：
- `http://localhost:5000/swagger-ui.html`

当前已聚合的服务文档包括：
- auth
- subject
- practice
- circle
- interview
- oss

## 重要配置说明

### Interview AI 配置
`colacode-interview` 默认支持通过环境变量或配置中心接入 AI 服务。

示例配置语义：

```yaml
interview:
  ai:
    enabled: ${INTERVIEW_AI_ENABLED:true}
    api-key: ${INTERVIEW_AI_API_KEY:}
    service-name: ${INTERVIEW_AI_SERVICE_NAME:colacode-ai}
    base-url: ${INTERVIEW_AI_BASE_URL:}
```

### OSS 配置
`colacode-oss` 支持本地存储与 MinIO：
- 本地模式：文件落在 `./uploads/`
- MinIO 模式：通过 `storage.minio.*` 配置接入

### 微信配置
`colacode-wx` 需要提供：
- `wx.mp.app-id`
- `wx.mp.secret`
- `wx.mp.token`
- `wx.mp.aes-key`

## 常用命令

### 后端编译

```bash
mvn clean compile
```

### 后端测试

```bash
mvn test
```

### 打包

```bash
mvn clean package
```

### 指定模块测试/启动

```bash
mvn -pl colacode-interview test
mvn -pl colacode-gateway spring-boot:run
```

## 文档目录

`doc/` 下已经包含较完整的模块说明和部署说明，可作为二次开发入口：

- `doc/01-Auth认证服务.md`
- `doc/02-Circle社区服务.md`
- `doc/03-RocketMQ详细使用.md`
- `doc/04-Elasticsearch详细使用.md`
- `doc/05-Subject题库服务.md`
- `doc/06-Practice练习服务.md`
- `doc/07-Interview面试服务.md`
- `doc/08-OSS对象存储.md`
- `doc/09-Wx微信模块.md`
- `doc/10-Nacos Docker部署.md`
- `doc/11-MySQL8 Docker部署.md`
- `doc/12-SpringAI Interview联调.md`

## 开发提示

- 仓库中的部分 `application.yml` 默认地址可能是示例值或历史环境值，实际开发请优先通过环境变量或 Nacos 覆盖。
- 后端要求 **Java 17**，请确认 `mvn -version` 使用的也是 JDK 17。
- 如果不使用 Nacos，本地可按 `doc/nacos-config/README.md` 中的说明关闭相关能力并使用本地配置兜底。
- 建议通过网关联调接口，而不是直接暴露每个业务服务给前端。

## 后续可扩展方向

- 补充统一的架构图与调用链说明
- 为每个模块补充独立启动说明
- 增加本地开发 `.env` / 配置模板
- 补充 CI/CD、镜像发布与回滚文档

---

如果你是第一次接手这个项目，推荐阅读顺序：
1. `doc/design-philosophy.md`
2. `doc/nacos-config/README.md`
3. `doc/01-Auth认证服务.md` ~ `doc/12-SpringAI Interview联调.md`
4. 根目录 `pom.xml`
5. `colacode-gateway/src/main/resources/application.yml`
=======
# ColaCode 项目

ColaCode 是一个在线教育/学习平台，提供用户认证、题库管理、练习、面试模拟、社区交流等功能。项目采用微服务架构，使用 Spring Cloud 生态系统构建，包含多个独立的服务模块。

## 项目结构

```
├── colacode-ai/          # AI服务模块 - 智能题目生成、答案评分
├── colacode-auth/        # 认证服务模块 - 用户认证、授权、角色管理
├── colacode-circle/      # 社区服务模块 - 社区交流、敏感词过滤
├── colacode-common/      # 公共模块 - 通用工具、异常处理、通用配置
├── colacode-gateway/     # 网关服务模块 - 路由管理、认证、限流
├── colacode-interview/   # 面试服务模块 - 面试模拟、评分、历史记录
├── colacode-oss/         # OSS服务模块 - 文件上传、下载、管理
├── colacode-practice/    # 练习服务模块 - 练习管理、排行榜
├── colacode-subject/     # 题库服务模块 - 题目管理、分类、搜索
├── colacode-web/         # 前端应用模块 - 前端界面、用户交互
├── colacode-wx/          # 微信模块 - 微信消息处理、事件响应
├── deploy/               # 部署相关配置
├── doc/                  # 文档目录
├── 原型图/               # 原型图目录
├── CODE_WIKI.md          # 代码Wiki文档
├── docker-compose.yml    # Docker Compose配置文件
├── pom.xml               # Maven父项目配置
└── README.md             # 项目说明文档
```

## 核心功能

- **用户认证与授权**：用户注册、登录、角色管理、权限管理
- **题库管理**：题目管理、分类管理、标签管理、题目搜索
- **练习系统**：练习集管理、练习提交与评分、练习排行榜
- **面试模拟**：关键词分析、面试模拟、答案评分、面试历史记录
- **社区交流**：消息管理、敏感词过滤、WebSocket实时通信
- **文件管理**：文件上传、下载、管理
- **AI集成**：智能题目生成、答案评分

## 技术栈

### 后端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 后端开发语言 |
| Spring Boot | 3.4.0 | 应用框架 |
| Spring Cloud | 2024.0.0 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | 微服务生态 |
| MyBatis Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0.33 | 关系型数据库 |
| Redis | 7.0+ | 缓存、会话管理 |
| Nacos | 2.2.0 | 服务发现、配置管理 |
| RocketMQ | 5.1.0 | 消息队列 |
| Elasticsearch | 7.14.0 | 搜索引擎 |
| MinIO | 最新版 | 对象存储 |
| Spring AI | 1.1.2 | AI 集成 |
| Sa-Token | 1.39.0 | 认证授权 |
| Sentinel | 1.8.8 | 服务限流、熔断 |

### 前端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 3.0+ | 前端框架 |
| Vite | 最新版 | 构建工具 |
| Pinia | 最新版 | 状态管理 |
| Vue Router | 4.0+ | 路由管理 |

## 运行方式

### 开发环境

1. **环境准备**：
   - JDK 17+
   - Maven 3.6+
   - MySQL 8.0+
   - Redis 7.0+
   - Nacos 2.2.0+
   - Elasticsearch 7.14.0+
   - RocketMQ 5.1.0+
   - MinIO 最新版

2. **启动顺序**：
   1. 启动 Nacos：`sh startup.sh -m standalone`
   2. 启动 MySQL：`systemctl start mysql`
   3. 启动 Redis：`systemctl start redis`
   4. 启动 Elasticsearch：`systemctl start elasticsearch`
   5. 启动 RocketMQ：`sh bin/mqnamesrv.sh start && sh bin/mqbroker.sh start`
   6. 启动 MinIO：`minio server /data`
   7. 启动各个微服务：`mvn spring-boot:run`

3. **配置管理**：
   - 服务配置：通过 Nacos 配置中心管理
   - 数据源配置：`common-datasource.yaml`
   - Redis 配置：`common-redis.yaml`
   - RocketMQ 配置：`common-rocketmq.yaml`

### Docker 部署

1. **环境准备**：
   - Docker
   - Docker Compose

2. **部署步骤**：
   ```bash
   # 克隆代码
   git clone <repository-url>
   cd colacode
   
   # 构建镜像并启动服务
   docker-compose up -d
   ```

3. **环境变量**：
   - `OPENAI_API_KEY`：OpenAI API 密钥
   - `OPENAI_MODEL`：OpenAI 模型名称，默认 gpt-4o-mini

4. **服务访问**：
   - 网关服务：http://localhost:5000
   - Nacos：http://localhost:8848
   - MinIO：http://localhost:9001
   - Elasticsearch：http://localhost:9200

## 模块说明

### 网关服务 (colacode-gateway)
- **核心功能**：请求路由与转发、用户认证与授权、接口限流、统一异常处理
- **关键类**：`GatewayApplication`、`LoginFilter`、`LoginUserHeaderFilter`、`GatewayRouteConfig`、`SentinelGatewayConfig`

### 认证服务 (colacode-auth)
- **核心功能**：用户注册、登录、注销、密码管理、角色与权限管理、用户状态管理
- **关键类**：`AuthApplication`、`UserDomainService`、`RoleDomainService`、`PermissionDomainService`、`LoginSecurityService`

### 题库服务 (colacode-subject)
- **核心功能**：题目管理（增删改查）、题目分类、标签管理、题目搜索（Elasticsearch）、题目贡献统计
- **关键类**：`SubjectApplication`、`SubjectDomainService`、`CategoryDomainService`、`SubjectTypeHandlerFactory`、`SubjectEsService`

### 练习服务 (colacode-practice)
- **核心功能**：练习管理、练习集管理、练习提交与评分、练习排行榜
- **关键类**：`PracticeApplication`、`PracticeDomainService`

### 面试服务 (colacode-interview)
- **核心功能**：面试模拟、题目分析、答案评分、面试历史记录、面试报告生成
- **关键类**：`InterviewApplication`、`InterviewDomainService`、`InterviewEngine`、`AiInterviewEngine`、`DatabaseInterviewEngine`

### 社区服务 (colacode-circle)
- **核心功能**：社区交流、敏感词过滤、WebSocket 实时通信、消息管理
- **关键类**：`CircleApplication`、`CircleDomainService`、`DFAFilter`、`ChickenSocketHandler`

### AI服务 (colacode-ai)
- **核心功能**：智能题目生成、答案评分、AI 模型管理
- **关键类**：`AiApplication`、`AiService`、`RealAiService`、`MockAiService`

### OSS服务 (colacode-oss)
- **核心功能**：文件上传、文件下载、文件管理、存储适配器管理
- **关键类**：`OssApplication`、`OssController`、`StorageAdapter`、`LocalStorageAdapter`、`MinioStorageAdapter`

### 微信模块 (colacode-wx)
- **核心功能**：微信消息处理、微信事件响应、微信消息加密与验证
- **关键类**：`WxApplication`、`WxController`、`WxMessageHandler`、`WxMessageHandlerFactory`

### 公共模块 (colacode-common)
- **核心功能**：通用工具类、统一异常处理、通用响应格式、跨服务调用配置
- **关键类**：`Result`、`BusinessException`、`CommonGlobalExceptionHandler`、`LoginUserContext`、`CommonFeignAutoConfiguration`

### 前端应用 (colacode-web)
- **核心功能**：前端界面、用户交互
- **技术栈**：Vue.js 3.0+、Vite、Pinia、Vue Router

## 开发指南

### 代码规范
- 遵循 Java 代码规范
- 使用 Lombok 简化代码
- 使用 MapStruct 处理对象转换
- 使用 Sa-Token 处理认证授权
- 使用 MyBatis Plus 处理数据库操作

### 模块开发流程
1. **创建模块**：在根目录下创建新模块，添加 pom.xml
2. **配置依赖**：在 pom.xml 中添加必要的依赖
3. **实现功能**：按照领域驱动设计的思想，实现核心功能
4. **编写测试**：为核心功能编写单元测试
5. **配置服务**：在 Nacos 中配置服务相关配置
6. **注册路由**：在网关服务中注册路由

### 数据库操作
- 使用 MyBatis Plus 进行数据库操作
- 遵循 RESTful API 设计规范
- 使用事务管理确保数据一致性
- 使用索引优化查询性能

### 微服务通信
- 使用 OpenFeign 进行服务间调用
- 使用 RocketMQ 进行异步消息处理
- 使用 Redis 进行缓存共享
- 使用 Nacos 进行服务发现和配置管理

### 异常处理
- 使用 BusinessException 处理业务异常
- 使用 CommonGlobalExceptionHandler 统一处理异常
- 使用 Result 统一响应格式

## 监控与维护

### 监控
- 使用 Spring Boot Actuator 监控服务状态
- 使用 Sentinel 监控服务流量和熔断
- 使用 ELK stack 收集和分析日志

### 日志
- 使用 SLF4J + Logback 进行日志管理
- 统一日志格式和级别
- 关键操作记录详细日志

### 故障排查
- 查看服务日志
- 检查服务状态
- 检查数据库连接
- 检查 Redis 连接
- 检查 Nacos 服务状态

## 许可证

本项目采用 MIT 许可证。详情请参阅 LICENSE 文件。

## 贡献

欢迎贡献代码、报告问题或提出建议。请通过 GitHub Issues 或 Pull Requests 参与项目。

## 联系方式

如有问题或建议，请联系项目维护者。
>>>>>>> fa81996aaa295609d1de34b3cb12a500d99d0a3f
