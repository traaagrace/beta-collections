好，这一节信息量很大，我帮你**按原文结构逐行翻译，并在关键地方穿插“为什么这么设计 / 实际效果”**，而不是只做生硬直译。
你可以把下面这份当成 **《InnoDB 隔离级别 + 加锁行为对照注解版》**。

---

# 14.7.2.1 事务隔离级别（Transaction Isolation Levels）

---

## 原文

> Transaction isolation is one of the foundations of database processing.

**翻译**
事务隔离是数据库处理的基础之一。

**解释**
并发事务并不是“有没有锁”这么简单，而是**读到什么数据、什么时候读、能不能看到别人未提交的数据**。

---

## 原文

> Isolation is the I in the acronym ACID; the isolation level is the setting that fine-tunes the balance between performance and reliability, consistency, and reproducibility of results when multiple transactions are making changes and performing queries at the same time.

**翻译**
隔离性是 ACID 中的 I。隔离级别用于在多事务并发读写时，在**性能**与**可靠性 / 一致性 / 可重复性**之间做权衡。

**解释（很重要）**
👉 隔离级别不是“越高越好”，而是**代价换一致性**：

* 隔离越高 → 锁越多 → 并发越差
* 隔离越低 → 锁越少 → 并发越好，但结果不稳定

---

## 原文

> InnoDB offers all four transaction isolation levels described by the SQL:1992 standard...

**翻译**
InnoDB 支持 SQL:1992 标准定义的四种事务隔离级别：

* READ UNCOMMITTED
* READ COMMITTED
* REPEATABLE READ
* SERIALIZABLE

默认是 **REPEATABLE READ**。

---

## 原文

> A user can change the isolation level for a single session or for all subsequent connections with the SET TRANSACTION statement.

**翻译**
用户可以使用 `SET TRANSACTION` 为**当前会话**或**之后的连接**设置隔离级别。

---

## 原文

> InnoDB supports each of the transaction isolation levels described here using different locking strategies.

**翻译**
InnoDB 通过**不同的加锁策略**来实现不同的隔离级别。

**解释（核心）**
👉 **隔离级别 ≠ MVCC alone**
👉 **隔离级别 = MVCC + 锁策略（Record / Gap / Next-Key）**

---

# 一、REPEATABLE READ（默认）

---

## 原文

> This is the default isolation level for InnoDB.

**翻译**
这是 InnoDB 的默认隔离级别。

---

## 原文

> Consistent reads within the same transaction read the snapshot established by the first read.

**翻译**
在同一个事务中，所有一致性读（普通 SELECT）都读取**第一次读时创建的快照**。

**解释**
👉 这是 **MVCC 的“可重复读”**
👉 多次 SELECT 结果一致

---

## 原文

> For locking reads (SELECT with FOR UPDATE or LOCK IN SHARE MODE), UPDATE, and DELETE statements, locking depends on whether the statement uses a unique index with a unique search condition or a range-type search condition.

**翻译**
对于**加锁读（FOR UPDATE / LOCK IN SHARE MODE）**、UPDATE、DELETE，加锁行为取决于：

* 是否使用 **唯一索引 + 等值条件**
* 还是 **范围查询**

---

### ✅ 唯一索引 + 等值条件

> For a unique index with a unique search condition, InnoDB locks only the index record found, not the gap before it.

**翻译**
如果使用唯一索引并且是唯一查找条件，InnoDB **只锁记录本身，不锁 gap**。

**解释（你前面刚问过的）**

```sql
SELECT * FROM t WHERE id = 10 FOR UPDATE;
```

* 锁：[10]
* ❌ 没有 Next-Key
* ❌ 没有 Gap Lock

---

### ❌ 其他情况（范围 / 普通索引）

> For other search conditions, InnoDB locks the index range scanned, using gap locks or next-key locks...

**翻译**
对于其他查询条件，InnoDB 会锁住扫描到的索引范围，使用 gap lock 或 next-key lock，防止其他事务在该范围内插入。

**解释**
👉 这是 **防幻读的关键机制**

---

## 原文（重要提醒）

> It is not recommended to mix locking statements with non-locking SELECT statements in a single REPEATABLE READ transaction...

**翻译**
不建议在同一个 REPEATABLE READ 事务中，混用：

* 加锁语句（UPDATE / DELETE / SELECT FOR UPDATE）
* 非加锁 SELECT

**解释（非常关键）**

* 普通 SELECT → **快照读**
* UPDATE / FOR UPDATE → **当前读**

👉 看到的是**两个不同世界的数据**

如果你想“逻辑上统一”，应该用 **SERIALIZABLE**

---

# 二、READ COMMITTED

---

## 原文

> Each consistent read, even within the same transaction, sets and reads its own fresh snapshot.

**翻译**
在 READ COMMITTED 下，即使在同一个事务中，每一次一致性读都会读取**新的快照**。

**解释**
👉 不保证“可重复读”
👉 两次 SELECT 结果可能不同

---

## 原文（加锁差异，重点）

> For locking reads..., InnoDB locks only index records, not the gaps before them.

**翻译**
对于加锁读、UPDATE、DELETE，InnoDB **只锁记录，不锁 gap**。

👉 **Gap Lock 默认关闭**

---

## 原文

> Because gap locking is disabled, phantom row problems may occur...

**翻译**
因为 gap lock 被禁用，可能会出现幻读。

**解释**
👉 READ COMMITTED 是 **用并发换一致性**

---

## 原文

> For UPDATE or DELETE statements, InnoDB holds locks only for rows that it updates or deletes.

**翻译**
UPDATE / DELETE 中，InnoDB **只保留真正被修改的行锁**。

**解释**
👉 这是 READ COMMITTED **死锁少的重要原因**

---

## 示例解释（你一定要理解）

### 表无索引

```sql
UPDATE t SET b = 5 WHERE b = 3;
```

### 在 REPEATABLE READ

* 扫描到的所有行 → 全部加 X 锁
* 不管是否修改 → 都不释放

👉 **并发极差**

---

### 在 READ COMMITTED

* 读到一行 → 加锁
* 不匹配 WHERE → **立即释放**
* 匹配 → 保留锁

👉 **高并发的关键设计**

---

## “半一致读（semi-consistent read）”

**一句话解释**：

> READ COMMITTED 下，如果行被锁了，InnoDB 会先用“最新已提交版本”判断 WHERE 是否匹配，再决定要不要等锁。

这是 **性能换逻辑复杂度** 的典型设计。

---

# 三、READ UNCOMMITTED

---

## 原文

> SELECT statements are performed in a nonlocking fashion, but a possible earlier version of a row might be used.

**翻译**
SELECT 不加锁，可能读到未提交的数据（脏读）。

👉 **几乎不用**

---

# 四、SERIALIZABLE

---

## 原文

> This level is like REPEATABLE READ, but InnoDB implicitly converts all plain SELECT statements to SELECT ... LOCK IN SHARE MODE...

**翻译**
SERIALIZABLE 类似 REPEATABLE READ，但：

* 所有普通 SELECT 都会被隐式转成 `LOCK IN SHARE MODE`

**解释**

👉 **彻底禁止幻读**
👉 **代价是极低并发**

---

# 最后给你一张“工程视角总结表”

| 隔离级别             | 快照  | Gap Lock | 幻读 | 并发    |
| ---------------- | --- | -------- | -- | ----- |
| READ UNCOMMITTED | ❌   | ❌        | ❌  | ⭐⭐⭐⭐⭐ |
| READ COMMITTED   | 每次新 | ❌        | ✅  | ⭐⭐⭐⭐  |
| REPEATABLE READ  | 事务级 | ✅        | ❌  | ⭐⭐⭐   |
| SERIALIZABLE     | 锁读  | ✅        | ❌  | ⭐     |

---

如果你愿意，下一步我可以帮你：

* 把 **隔离级别 × 锁类型 × SQL** 做成一张“面试/实战速查表”
* 用 **一条 SQL 在不同隔离级别下的真实锁差异** 来演示
* 结合你常用的业务场景，告诉你 **该不该用 READ COMMITTED**

你想从哪个继续？
