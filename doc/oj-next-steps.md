# ColaCode OJ 当前状态与后续事项

## 1. 当前状态

后端主链路已经完成并可编译通过：

- `subject` 已具备编程题模型与测试用例存储能力
- `practice` 已具备 Judge0 提交、详情、列表与异步判题能力
- `practice` 已支持判题完成后异步调用 `colacode-ai`
- `colacode-ai` 已提供 `POST /ai/judge/analyse`
- 初始化 SQL 已包含：
  - `subject_code`
  - `subject_code_case`
  - `practice_submission`
  - `practice_submission_case`
  - `practice_submission.ai_status`
  - `practice_submission.ai_feedback`

本地配置也已经补齐：

- `feign.subject.url`
- `feign.ai.url`
- `judge.ai.*`
- `colacode.gateway.routes.definitions`

## 2. 已完成的后端能力

### Subject

- 编程题配置表与测试用例表
- 判题详情接口 `/subject/code/judgeDetail`
- 题型分发到 `CODE` 处理器

### Practice

- 提交接口 `/practice/judge/submit`
- 样例运行接口 `/practice/judge/run`
- 提交详情接口 `/practice/judge/submission/detail`
- 提交列表接口 `/practice/judge/submission/list`
- Judge0 调用与轮询
- 状态映射：`PENDING / RUNNING / AC / WA / TLE / RE / CE / SYSTEM_ERROR`
- 提交频率限制与代码长度限制
- 样例运行独立限流与冷却时间控制
- 判题完成后的异步 AI 反馈回填
- AI 分析失败后的有限次重试

### AI

- 面试题生成 `/ai/interview/question`
- 面试评分 `/ai/interview/score`
- 判题分析 `/ai/judge/analyse`
- `mock / openai` 两种实际可运行模式

## 3. 已完成的工程收尾

- 根 `README.md` 合并冲突已清理
- `doc/services/01-Gateway/README.md` 已回写当前网关配置
- `doc/services/09-AI/README.md` 已回写当前 AI 服务状态
- `doc/local-development-guide.md` 已补充 Judge AI 相关环境变量
- `doc/infrastructure/sql/README.md` 已补充数据库初始化与 OJ 种子导入说明

## 4. 已验证内容

已使用 `JDK 17` 验证以下命令通过：

```bash
mvn -pl colacode-ai,colacode-practice -am -DskipTests compile
mvn -pl colacode-ai,colacode-practice -am test
```

当前自动化测试已覆盖：

- `JudgeOutputComparator`
- `JudgeSampleRunService`
- `JudgeAiAnalysisService` 重试逻辑
- `MockAiService`
- `SwitchableAiService`

## 5. 当前仍未完成的事项

当前明确未完成的只剩前端范围。后端侧还可以继续做的内容属于进一步优化，而不是功能缺口。

### 前端范围

- OJ 题目页
- 代码编辑器
- 提交历史与 AI 反馈展示页面

### 后端进一步优化（可选）

- 接真实 MySQL / Redis / Judge0 的端到端集成测试
- 更丰富的 AI 提示词调优
- AI 失败重试后的告警与监控

## 6. 本地联调建议

推荐最小联调顺序：

1. MySQL
2. Redis
3. Judge0
4. `colacode-subject`
5. `colacode-practice`
6. `colacode-ai`
7. `colacode-gateway`

如果要启用判题 AI 分析，至少设置：

```bash
set BOOT3_AI_URL=http://127.0.0.1:3020
set BOOT3_JUDGE_AI_ENABLED=true
```

如果要启用真实模型，再补：

```bash
set COLACODE_AI_DEFAULT_MODEL=openai
set COLACODE_AI_OPENAI_ENABLED=true
set OPENAI_API_KEY=your_key
```

## 7. 关键代码位置

### Subject

- `colacode-subject/src/main/java/com/colacode/subject/domain/service/SubjectDomainService.java`
- `colacode-subject/src/main/java/com/colacode/subject/domain/service/SubjectCodeDomainService.java`
- `colacode-subject/src/main/java/com/colacode/subject/domain/strategy/CodeTypeHandler.java`

### Practice

- `colacode-practice/src/main/java/com/colacode/practice/application/controller/JudgeController.java`
- `colacode-practice/src/main/java/com/colacode/practice/domain/service/JudgeSubmissionDomainService.java`
- `colacode-practice/src/main/java/com/colacode/practice/domain/service/JudgeSubmissionExecutionService.java`
- `colacode-practice/src/main/java/com/colacode/practice/domain/service/JudgeAiAnalysisService.java`
- `colacode-practice/src/main/java/com/colacode/practice/infra/judge/Judge0Client.java`

### AI

- `colacode-ai/src/main/java/com/colacode/ai/controller/AiJudgeController.java`
- `colacode-ai/src/main/java/com/colacode/ai/service/SwitchableAiService.java`
- `colacode-ai/src/main/java/com/colacode/ai/service/RealAiService.java`

### SQL

- `doc/infrastructure/sql/colacode-init.sql`
- `doc/infrastructure/sql/seed-oj-minimal-problem.sql`
