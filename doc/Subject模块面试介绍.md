# Subject 题库模块面试介绍

## 模块定位

Subject 题库服务是 ColaCode 的核心业务模块，负责面试题目的管理和搜索。

## 核心功能

- 题目 CRUD（单选/多选/判断/简答）
- 分类树管理（二级分类 + 标签）
- Elasticsearch 全文搜索
- RocketMQ 异步点赞
- 错题记录 / 收藏管理

## 技术栈

- Spring Boot 3.4 + Spring Cloud
- MyBatis-Plus
- Elasticsearch 7.14
- RocketMQ
- XXL-Job
- Sa-Token 鉴权

## 数据库设计（9张表）

| 表名 | 说明 | 题型 |
|------|------|------|
| subject_info | 题目主表 | - |
| subject_category | 题目分类 | - |
| subject_label | 题目标签 | - |
| subject_mapping | 题目-分类-标签关联 | - |
| subject_radio | 单选题选项 | type=1 |
| subject_multiple | 多选题选项 | type=2 |
| subject_judge | 判断题答案 | type=3 |
| subject_brief | 简答题答案 | type=4 |
| subject_liked | 点赞记录 | - |

## 设计亮点

### 1. 策略模式处理不同题型

4 种题型数据结构完全不同，如果用 if-else 代码会难以维护。

```java
public interface SubjectTypeHandler {
    Integer getHandlerType();
    void add(SubjectInfoBO bo);
    void update(SubjectInfoBO bo);
    SubjectInfoBO query(Long id);
}
```

- `RadioTypeHandler` - 处理单选题
- `MultipleTypeHandler` - 处理多选题
- `JudgeTypeHandler` - 处理判断题
- `BriefTypeHandler` - 处理简答题

通过 `SubjectTypeHandlerFactory` 工厂类根据 subjectType 获取对应处理器。

**优势**：新增题型只需添加新 Handler，不修改现有代码（开闭原则）

### 2. RocketMQ 异步点赞

用户点赞请求不直接写数据库，而是发送消息到 MQ，由消费者异步写入。

```java
// 生产者
subjectLikedProducer.sendLikedMessage(message);

// 消费者
@RocketMQMessageListener(topic = "subject-liked-topic")
public class SubjectLikedConsumer implements RocketMQListener<String> {
    public void onMessage(String message) {
        subjectLikedMapper.insert(liked);
    }
}
```

**优势**：缓解数据库压力，削峰填谷

### 3. Elasticsearch 全文搜索

题目新增/更新后通过 RocketMQ 异步同步到 ES，支持关键词搜索。

```java
// XXL-Job 定时任务检查 ES 数据一致性
@XxlJob("subjectSyncJob")
public void execute() {
    subjectEsService.syncToEs();
}
```

## API 接口

| 接口 | 说明 |
|------|------|
| POST /subject/info/add | 新增题目 |
| POST /subject/info/update | 更新题目 |
| GET /subject/info/query | 查询题目 |
| GET /subject/info/page | 分页查询 |
| POST /subject/info/delete | 删除题目 |
| GET /subject/search/query | ES 全文搜索 |
| GET /subject/category/queryTree | 分类树 |
| POST /subject/wrong/record | 记录错题 |
| POST /subject/favorite/add | 收藏题目 |
| POST /subject/liked/doLike | 点赞/取消点赞 |

## 数据流转

```
新增题目请求
    ↓
SubjectInfoController
    ↓
SubjectDomainService
    ↓ 保存 subject_info + subject_mapping
    ↓ 通过工厂获取策略处理器
    ↓ 保存题型数据 (radio/multiple/judge/brief)
    ↓ 发送 MQ 消息同步到 ES
Elasticsearch
```

## 项目结构

```
colacode-subject/
├── domain/
│   ├── service/        # 领域服务
│   ├── bo/           # 业务对象
│   ├── converter/    # 转换器
│   └── strategy/    # 策略模式
├── application/
│   ├── controller/ # 7个Controller
│   ├── dto/       # DTO
│   └── mq/        # MQ
└── infra/
    ├── mapper/     # MyBatisMapper
    ├── entity/     # 实体
    └── es/          # ES服务
```

## 面试可能会问的问题

1. **为什么用策略模式？**
   - 4 种题型数据结构不同，if-else 会导致代码难以维护
   - 符合开闭原则，新增题型不修改现有代码

2. **MQ 异步点赞的优势？**
   - 削峰填谷，缓解数据库压力
   - 保证最终一致性

3. **ES 数据如何保证一致性？**
   - MQ 异步同步 + XXL-Job 定时检查

4. **如何处理分类树的层级？**
   - parent_id 递归查询，转换为树形结构