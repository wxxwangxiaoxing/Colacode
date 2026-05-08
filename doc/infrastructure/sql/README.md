# SQL 使用说明

## 1. 完整初始化

如果你是全新本地库，先执行：

```bash
mysql -u root -p colacode < doc/infrastructure/sql/colacode-init.sql
```

这会创建 ColaCode 当前需要的基础表，包括 OJ 相关表：

- `subject_code`
- `subject_code_case`
- `practice_submission`
- `practice_submission_case`

## 2. OJ 最小联调数据

如果你只想快速跑通一题 Judge0 联调，再执行：

```bash
mysql -u root -p colacode < doc/infrastructure/sql/seed-oj-minimal-problem.sql
```

脚本特性：

- 可重复执行
- 会先清理同名旧种子数据
- 会插入一条“`两数之和（OJ联调题）`”
- 自带 2 个样例用例和 3 个隐藏用例

## 3. 执行后验证

脚本执行后至少确认：

```sql
SELECT id, subject_name, subject_type FROM subject_info WHERE subject_name = '两数之和（OJ联调题）';
SELECT subject_id, case_no, is_sample FROM subject_code_case WHERE subject_id = <subject_id> ORDER BY case_no;
```

然后可用这道题继续验证：

- `/subject/info/query?id=<subject_id>`
- `/subject/code/judgeDetail?id=<subject_id>`
- `/practice/judge/run`
- `/practice/judge/submit`

## 4. 注意事项

- `seed-oj-minimal-problem.sql` 默认不会强绑分类映射
- 如果你依赖分类树展示，请按脚本注释补一条 `subject_mapping`
- `practice_submission` 和 `practice_submission_case` 是提交记录表，不需要手动插测试数据
