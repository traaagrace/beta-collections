下面我按**原文顺序**给你做 **「逐段翻译 + 解释」**。
我会做到三点：

1. **忠实翻译**（不乱改含义）
2. **补充背景解释**（为什么要这么设计）
3. **结合常见 SQL 场景**（帮你和实际使用对上）

你可以把这份当成 **InnoDB 锁机制速读 + 面试/排障版注解**。

---

# 14.7.1 InnoDB Locking

## 14.7.1 InnoDB 锁机制

> This section describes lock types used by InnoDB.

**本节描述 InnoDB 使用的各种锁类型。**

---

## Shared and Exclusive Locks

## 共享锁与排他锁

> InnoDB implements standard row-level locking where there are two types of locks, shared (S) locks and exclusive (X) locks.

InnoDB 实现了**标准的行级锁机制**，包含两种锁：

* **共享锁（S Lock）**
* **排他锁（X Lock）**

---

> A shared (S) lock permits the transaction that holds the lock to read a row.

**共享锁（S）**：
👉 持有该锁的事务 **可以读取一行数据**

---

> An exclusive (X) lock permits the transaction that holds the lock to update or delete a row.

**排他锁（X）**：
👉 持有该锁的事务 **可以更新或删除一行数据**

---

### 锁冲突规则（非常重要）

> If transaction T1 holds a shared (S) lock on row r, then requests from some distinct transaction T2 for a lock on row r are handled as follows:

如果事务 **T1** 在某行 **r** 上持有 **S 锁**，那么事务 **T2** 请求该行锁时：

---

> A request by T2 for an S lock can be granted immediately.

T2 请求 **S 锁** → ✅ **可以立刻获得**

👉 多个事务可以同时读同一行

---

> A request by T2 for an X lock cannot be granted immediately.

T2 请求 **X 锁** → ❌ **必须等待**

👉 因为写操作会影响正在读的数据

---

> If a transaction T1 holds an exclusive (X) lock on row r, a request from some distinct transaction T2 for a lock of either type on r cannot be granted immediately.

如果 T1 持有 **X 锁**：

* T2 请求 **S 锁** ❌
* T2 请求 **X 锁** ❌

👉 **全部阻塞**

---

> Instead, transaction T2 has to wait for transaction T1 to release its lock on row r.

👉 T2 只能等 T1 提交或回滚释放锁

---

📌 **一句话总结**

| 当前锁 | 新请求 S | 新请求 X |
| --- | ----- | ----- |
| S   | ✅     | ❌     |
| X   | ❌     | ❌     |

---

## Intention Locks

## 意向锁（表级）

> InnoDB supports multiple granularity locking which permits coexistence of row locks and table locks.

InnoDB 支持 **多粒度锁**：

* 行锁
* 表锁
  可以 **共存**

---

> For example, a statement such as LOCK TABLES ... WRITE takes an exclusive lock (an X lock) on the specified table.

例如：

```sql
LOCK TABLE t WRITE;
```

👉 会对整张表加 **X 锁**

---

> To make locking at multiple granularity levels practical, InnoDB uses intention locks.

为了让 **行锁 + 表锁** 共存不乱套，
InnoDB 引入了 **意向锁（Intention Lock）**

---

### 意向锁的本质

> Intention locks are table-level locks that indicate which type of lock (shared or exclusive) a transaction requires later for a row in a table.

**意向锁是表级锁**，用来说明：

> “我接下来**打算**在表里的某些行上加什么锁”

---

### 两种意向锁

> An intention shared lock (IS) indicates that a transaction intends to set a shared lock on individual rows in a table.

* **IS（意向共享锁）**
* 表示：👉 *我将要在某些行上加 S 锁*

---

> An intention exclusive lock (IX) indicates that a transaction intends to set an exclusive lock on individual rows in a table.

* **IX（意向排他锁）**
* 表示：👉 *我将要在某些行上加 X 锁*

---

### 常见 SQL 对应关系

> SELECT ... LOCK IN SHARE MODE sets an IS lock
> SELECT ... FOR UPDATE sets an IX lock

| SQL                  | 表级意向锁 |
| -------------------- | ----- |
| `LOCK IN SHARE MODE` | IS    |
| `FOR UPDATE`         | IX    |

---

### 意向锁协议（规则）

> Before a transaction can acquire a shared lock on a row, it must first acquire an IS lock or stronger on the table.

👉 **先表级 IS，再行级 S**

---

> Before a transaction can acquire an exclusive lock on a row, it must first acquire an IX lock on the table.

👉 **先表级 IX，再行级 X**

---

### 表锁兼容矩阵（非常重要）

|        | X | IX | S | IS |
| ------ | - | -- | - | -- |
| **X**  | ❌ | ❌  | ❌ | ❌  |
| **IX** | ❌ | ✅  | ❌ | ✅  |
| **S**  | ❌ | ❌  | ✅ | ✅  |
| **IS** | ❌ | ✅  | ✅ | ✅  |

---

> Intention locks do not block anything except full table requests

👉 **意向锁几乎不阻塞任何东西**
👉 **唯一目的：防止别人锁整张表**

---

> The main purpose of intention locks is to show that someone is locking a row

**核心作用一句话：**

> “这张表里有人在锁行，你别动整表”

---

## Record Locks

## 记录锁（行锁）

> A record lock is a lock on an index record.

**记录锁 = 加在索引记录上的锁**

---

> SELECT c1 FROM t WHERE c1 = 10 FOR UPDATE;

👉 会锁住：

```
c1 = 10 的索引记录
```

---

> prevents any other transaction from inserting, updating, or deleting rows where the value of t.c1 is 10.

👉 别的事务 **不能插入 / 修改 / 删除 c1=10 的行**

---

> Record locks always lock index records, even if a table is defined with no indexes.

**重要**：
即使表没有索引，InnoDB 也会：

> 自动创建 **隐藏聚簇索引**

---

## Gap Locks

## 间隙锁（防幻读的关键）

> A gap lock is a lock on a gap between index records

**间隙锁**：
👉 锁的是 **索引记录之间的“空隙”**

---

> SELECT c1 FROM t WHERE c1 BETWEEN 10 and 20 FOR UPDATE;

👉 会锁：

```
(10,20) 之间的“空隙”
```

---

> prevents other transactions from inserting a value of 15

👉 别的事务 **不能插入 15**

---

> whether or not there was already any such value

👉 不管之前有没有 15，都不能插

---

### Gap Lock 的特性（很反直觉）

> Gap locks can co-exist.

👉 多个事务 **可以同时持有同一个 gap 锁**

---

> There is no difference between shared and exclusive gap locks.

👉 Gap 锁 **没有 S/X 之分**

---

> Gap locking can be disabled explicitly.

在以下情况 **关闭 gap 锁**：

* `READ COMMITTED`
* `innodb_locks_unsafe_for_binlog`（已废弃）

---

## Next-Key Locks

## Next-Key 锁（Record + Gap）

> A next-key lock is a combination of a record lock and a gap lock.

**Next-Key Lock = 行锁 + 前间隙锁**

---

> By default, InnoDB operates in REPEATABLE READ

👉 默认隔离级别 **RR**

👉 使用 **Next-Key Lock 防止幻读**

---

### 示例索引值

```
10, 11, 13, 20
```

Next-Key 锁范围：

```
(-∞,10]
(10,11]
(11,13]
(13,20]
(20,+∞)
```

---

> The supremum pseudo-record

**supremum**：
👉 虚拟的“最大值记录”，用来锁最后一个 gap

---

## Insert Intention Locks

## 插入意向锁

> An insert intention lock is a type of gap lock set by INSERT operations

👉 插入前先加的一种 **特殊 gap 锁**

---

> multiple transactions inserting into the same index gap need not wait for each other

👉 多个事务 **插不同值**，可以并发插入

---

### 示例解释

已有：

```
4, 7
```

事务 A 插 5
事务 B 插 6

👉 **不互相阻塞**

---

## AUTO-INC Locks

## 自增锁

> An AUTO-INC lock is a special table-level lock

👉 针对 `AUTO_INCREMENT` 的 **表级锁**

---

> innodb_autoinc_lock_mode controls the algorithm

👉 通过参数控制：

* 连续性
* 并发性

---

## Predicate Locks for Spatial Indexes

## 空间索引的谓词锁

> next-key locking does not work well for SPATIAL indexes

👉 空间索引 **没有线性顺序**

---

> InnoDB uses predicate locks

👉 使用 **谓词锁**：

* 锁的是 **查询条件本身**
* 防止插入“符合该条件”的数据

---

# 总结一张速查表

| 锁类型              | 锁住什么    | 解决什么问题  |
| ---------------- | ------- | ------- |
| S / X            | 行       | 并发读写    |
| IS / IX          | 表       | 行锁与表锁协调 |
| Record           | 索引记录    | 精确锁行    |
| Gap              | 索引空隙    | 防插入     |
| Next-Key         | 记录 + 空隙 | 防幻读     |
| Insert Intention | 插入前 gap | 并发插入    |
| AUTO-INC         | 表       | 自增安全    |
| Predicate        | 空间条件    | 空间索引一致性 |

---

如果你愿意，下一步我可以帮你：

* 用 **一张图** 画清 Record / Gap / Next-Key
* 结合 **具体 SQL + RR / RC** 分析加什么锁
* 直接帮你 **总结成面试答案版**




----- 
## 意向锁、插入意向锁

非常好，这两个点**正是 InnoDB 锁设计里“最容易背公式、最难真正理解”的部分**。
我不再重复定义，而是从 **「设计动机 → 解决了什么问题 → 如果没有它会怎样 → 实际运行时到底起什么作用」** 四个层次来讲。

---

# 一、意向锁（Intention Lock）

## 1️⃣ 意向锁为什么被“发明”出来？

### 背景问题（没有意向锁会发生什么）

InnoDB 同时支持：

* **行锁**（高并发）
* **表锁**（某些 DDL / LOCK TABLE）

假设没有意向锁：

* 事务 A：锁了表 `t` 中的 **10000 行**
* 事务 B：执行

  ```sql
  LOCK TABLE t WRITE;
  ```

问题来了👇
👉 **B 怎么知道表里有没有被锁的行？**

### 如果没有意向锁，唯一办法是：

> 🔥 **扫描整张表的所有行锁**

这在并发系统里是 **不可接受的**

---

## 2️⃣ 意向锁的设计意图（核心）

> **用一个“表级轻量标记”，快速告诉系统：
> 这张表里是否存在行锁，以及是什么类型的行锁**

**一句话本质：**

> **意向锁 = 行锁的“目录 / 预告 / 标识”**

---

## 3️⃣ 意向锁到底“锁住”了什么？

⚠️ 非常重要的一点：

> **意向锁不锁数据，也不锁行**

它只是：

* 一个 **表级状态**
* 用于 **锁兼容性快速判断**

---

## 4️⃣ IS / IX 的真实含义（不是“共享/排他”那么简单）

| 锁  | 真正含义                   |
| -- | ---------------------- |
| IS | “我现在 / 即将对表中的某些行加 S 锁” |
| IX | “我现在 / 即将对表中的某些行加 X 锁” |

> ❗ 注意：
> **“意向” ≠ “已经加了”**
> 它只是一个 **承诺**

---

## 5️⃣ 意向锁的核心作用（只有一个）

### 👉 **防止表锁误闯**

当一个事务想加 **表级 X / S 锁** 时：

* 只需要看表上的 IS / IX
* **不用扫描行锁**

例如：

```sql
LOCK TABLE t WRITE;
```

* 如果表上存在 IS / IX
* ❌ 直接阻塞

---

## 6️⃣ 意向锁为什么“几乎不阻塞任何东西”？

因为它的目标不是并发控制，而是 **表锁协助**

所以：

* IS 和 IS：兼容
* IS 和 IX：兼容
* IX 和 IX：兼容

❌ **只和表级 S / X 冲突**

---

## 7️⃣ 为什么必须“先加意向锁，再加行锁”？

这是 InnoDB 的硬性协议：

```text
行锁之前，必须先拿到意向锁
```

原因：

> **避免死锁和状态不一致**

如果反过来：

* 行锁成功
* 表锁后来插入

👉 会产生不可预测的锁冲突

---

## 8️⃣ 总结一句话（意向锁）

> **意向锁不是为了并发，而是为了“让表锁活着而不拖垮系统”**

---

# 二、Insert Intention Lock（插入意向锁）

这个名字非常容易让人和 **Intention Lock（IS/IX）** 混在一起，但：

> ❗ **它们完全不是一类东西**

---

## 1️⃣ Insert Intention Lock 的设计背景

先看一个真实并发插入场景：

索引中已有：

```
4, 7
```

两个事务并发：

```text
T1: INSERT 5
T2: INSERT 6
```

这两个插入都发生在：

```
(4, 7) 这个 gap
```

---

## 2️⃣ 如果没有 Insert Intention Lock 会怎样？

如果只用 **普通 gap lock**：

* T1 插 5 → 锁住 (4,7)
* T2 插 6 → ❌ 被阻塞

👉 **并发插入被严重限制**

---

## 3️⃣ Insert Intention Lock 的设计目标

> **同一个 gap 内，只要插入位置不同，就允许并发**

Insert Intention Lock 的本质是：

> **一种“声明我要插入”的 gap 锁**

---

## 4️⃣ Insert Intention Lock 锁住了什么？

⚠️ 关键点：

* 它锁的是 **gap**
* 但锁语义是：**“我将插入某个点”**

多个 Insert Intention Lock：

* **可以共存**
* 不互相阻塞

---

## 5️⃣ Insert Intention Lock 与普通 Gap Lock 的区别

| 对比项    | Gap Lock | Insert Intention Lock |
| ------ | -------- | --------------------- |
| 目的     | 防插入      | 协调插入                  |
| 是否互斥   | ❌        | ❌                     |
| 是否阻塞插入 | ✅        | ❌                     |
| 使用场景   | 查询       | 插入                    |

---

## 6️⃣ Insert Intention Lock 的工作流程

一次 INSERT 实际是：

```
1. 申请 Insert Intention Lock（gap）
2. 等待 gap 被允许
3. 真正插入行
4. 对新记录加 Record X Lock
```

---

## 7️⃣ Insert Intention Lock 什么时候会被阻塞？

当 gap 上存在：

* Next-Key Lock
* 普通 Gap Lock（来自 SELECT ... FOR UPDATE / UPDATE）

👉 插入必须等

---

## 8️⃣ 为什么 Insert Intention Lock 不会引发幻读？

因为：

* 幻读是 **读一致性问题**
* Insert Intention Lock 只服务于 **写并发**

---

# 三、意向锁 vs 插入意向锁（非常重要对比）

| 维度          | 意向锁（IS / IX） | Insert Intention Lock |
| ----------- | ------------ | --------------------- |
| 锁级别         | 表级           | 索引 gap                |
| 目的          | 协调表锁 & 行锁    | 提高并发插入                |
| 是否锁数据       | ❌            | ❌                     |
| 是否影响 SELECT | ❌            | ❌                     |
| 名字容易混淆      | ✅            | ✅                     |

---

# 四、用一句话“真正记住它们”

### 🔹 意向锁

> **“我表里有人在锁行，别动整张表”**

### 🔹 Insert Intention Lock

> **“我只是要往这个 gap 里插，不是要霸占它”**

---

# 五、为什么这些设计是“工程级”的？

因为 InnoDB 的目标不是：

* 理论最简单
  而是：
* **高并发真实场景下最少阻塞**

这两种锁，本质上都是：

> **为了减少“不必要的等待”**

---

如果你愿意，我可以下一步：

* 用 **一条 INSERT + SELECT FOR UPDATE** 画出完整锁序列
* 帮你 **分析真实死锁日志里的 Insert Intention**
* 或直接帮你 **总结成面试可背但不空洞的版本**

你想继续哪一个？


插入意向锁服务的是插入，目的是让多个事务并发插入。
插入是




这是一个**问到点子上的问题**，因为它正好卡在
**“文档表述” vs “实际锁实现”** 的边界上。

我先给你**结论**，然后用**图 + 对比 + 反例**把它彻底讲清楚。

---

## 先给结论（非常明确）

> **是的，从“锁的区间语义”来说，
> Insert Intention Lock 只作用在“开区间（gap）”，
> 👉 不包含任何现有记录（不包含左右端点）。**

但 ⚠️ 还要补一句非常重要的补充：

> **Insert Intention Lock 不会锁住“整个 gap 的插入权”，
> 它只是声明“我要在这个开区间里的某个点插入”。**

---

## 一、什么叫“只影响开区间”？

假设索引中已有值：

```
10 -------- 20
```

gap 是：

```
(10, 20)
```

### 执行：

```sql
INSERT INTO t(id) VALUES (15);
```

### InnoDB 的锁语义是：

```
Insert Intention Lock on (10,20)
Record X Lock on [15]
```

* `(10,20)` → **开区间**
* `10` 和 `20` 这两条记录：

  * ❌ 不会被 Insert Intention Lock 锁
  * 只可能被 Record / Next-Key 锁影响

---

## 二、为什么 Insert Intention Lock 必须是“开区间”？

这是**设计必然性**。

### 如果它包含端点会怎样？

假设 Insert Intention Lock 锁的是：

```
[10,20]
```

那么：

* 插入 15 会锁住 10 和 20
* 其他事务：

  * UPDATE 10 ❌
  * DELETE 20 ❌

👉 并发直接崩掉，**完全违背插入意向锁的设计目标**

---

## 三、和 Next-Key Lock 的本质区别就在这里

这是最容易混的地方。

### Next-Key Lock

```
(10,20]   或   (prev, current]
```

* **半开半闭**
* 包含记录
* 目的：**防幻读**

---

### Insert Intention Lock

```
(10,20)
```

* **纯开区间**
* 不包含任何记录
* 目的：**提高并发插入**

---

## 四、极端边界情况也仍然是“开区间”

### 1️⃣ 插入最小值

已有：

```
10, 20
```

插入：

```sql
INSERT id = 5;
```

锁范围：

```
(-∞,10)
```

👉 `10` 不被锁

---

### 2️⃣ 插入最大值（supremum）

已有：

```
10, 20
```

插入：

```sql
INSERT id = 30;
```

锁范围：

```
(20,+∞)
```

👉 `20` 不被锁
👉 `supremum` 只是一个逻辑边界，不是记录

---

## 五、那为什么死锁日志里看起来像“锁了记录之前的 gap”？

你可能在 `SHOW ENGINE INNODB STATUS` 里见过：

```
locks gap before rec insert intention
```

这行信息**非常容易误导人**。

它的真实含义是：

> **“在该记录之前的 gap 上，存在一个插入意向锁”**

而不是：

> ❌ “这个记录被锁了”

---

## 六、Insert Intention Lock 能不能和 Record Lock 冲突？

### 不会直接冲突

* Insert Intention Lock：锁 gap
* Record Lock：锁记录

### 但会被谁挡住？

| 锁类型                   | 是否阻塞 Insert Intention |
| --------------------- | --------------------- |
| Next-Key Lock         | ✅                     |
| 普通 Gap Lock           | ✅                     |
| 表级 X Lock             | ✅                     |
| Record Lock（不同 key）   | ❌                     |
| Insert Intention Lock | ❌                     |

---

## 七、一个非常重要的“反直觉点”

> **Insert Intention Lock 并不是“锁住 gap 的插入权”**

它只是：

> **“我即将插入一个点，请在插入前帮我占个位”**

所以：

* 多个 Insert Intention Lock 可以共存
* 不会阻止彼此
* 也不会锁端点

---

## 八、一句话帮你“定型记忆”

> **Insert Intention Lock 只作用在 gap 的“开区间”，
> 不锁任何已有记录，也不独占 gap。**

---

如果你愿意，下一步我可以：

* 用 **死锁日志逐字解析 Insert Intention**
* 把 **Insert / Next-Key / Gap** 用一张图彻底区分
* 或直接结合你线上 **INSERT 被阻塞的案例**帮你定位

你想继续哪一个？


这个问题**已经进入 InnoDB 锁理解的“分水岭”了**。
我按你说的重点，**只围绕 Next-Key Lock**，把：

* **唯一索引 vs 普通（二级）索引**
* **等值查询 vs 区间查询**
* **锁范围到底哪里不同、为什么不同**

一层一层讲清楚。

---

# 一、先给一张“总对比表”（先有全局感）

| 场景   | 索引类型     | SQL                                | 实际锁类型                 | 锁范围           |
| ---- | -------- | ---------------------------------- | --------------------- | ------------- |
| 等值查询 | **唯一索引** | `id = 10 FOR UPDATE`               | **Record Lock**       | `[10]`        |
| 等值查询 | **普通索引** | `idx = 10 FOR UPDATE`              | **Next-Key Lock**     | `(prev,10]`   |
| 区间查询 | 唯一索引     | `id BETWEEN 10 AND 20 FOR UPDATE`  | **Next-Key Lock**     | `(prev,20]`   |
| 区间查询 | 普通索引     | `idx BETWEEN 10 AND 20 FOR UPDATE` | **Next-Key Lock（多段）** | 多个 `(prev,x]` |

> **核心记忆点**
> 👉 唯一索引 + 等值 = 能精确定位 → 不需要 gap → 不幻读
> 👉 其他情况，InnoDB 一律保守 → Next-Key

---

# 二、为什么“唯一索引等值查询”可以不用 Next-Key？

这是理解差异的**核心设计原因**。

## 1️⃣ 唯一索引的“能力”

唯一索引能保证：

* 这个 key **最多只有一行**
* 插入同样的 key **必然失败**

---

## 2️⃣ 看这个 SQL

```sql
SELECT * FROM t WHERE id = 10 FOR UPDATE;
```

假设：

* `id` 是 **PRIMARY KEY**

### InnoDB 的判断是：

> “我已经锁住 id=10 这一行
> 👉 不可能再插入一个 id=10
> 👉 不会产生幻读”

所以：

```
只加 Record Lock：[10]
```

❌ 不需要锁 `(9,10)` 或 `(10,11)`

---

## 3️⃣ 这是“精确定位”的好处

> **索引越精确，加锁范围越小**

这就是为什么：

* 业务设计中强调 **唯一索引**
* 不只是为了性能，更是为了 **锁粒度**

---

# 三、普通索引等值查询：为什么一定会有 Next-Key？

现在看普通索引。

```sql
SELECT * FROM t WHERE age = 10 FOR UPDATE;
```

假设：

* `age` 是 **普通索引**
* 表里现在只有一条 `age=10`

---

## 1️⃣ InnoDB 的担心

虽然现在只有一条 `age=10`，但：

* 另一个事务 **完全可以再插一条 age=10**
* 那就会产生幻读

---

## 2️⃣ 所以必须锁“范围”

InnoDB 只能保守处理：

```
(prev_age, 10]
```

也就是：

* 锁住已有的 10
* 锁住它前面的 gap

👉 这就是 **Next-Key Lock**

---

## 3️⃣ 结果

```
Next-Key Lock on (prev,10]
```

* 不允许插新的 `age=10`
* 也不允许插 `age < 10 && > prev`

---

# 四、区间查询：唯一索引 vs 普通索引

这是更容易混的地方。

---

## 场景一：唯一索引区间查询

```sql
SELECT * FROM t WHERE id BETWEEN 10 AND 20 FOR UPDATE;
```

### InnoDB 行为：

* 会扫描索引：10 → 20
* 对**每个命中的记录**加 Next-Key Lock

锁范围最终是：

```
(9,10] (10,11] (11,12] ... (19,20]
```

👉 合并后等价于：

```
(9,20]
```

⚠️ 注意：
即使是唯一索引，只要是 **区间查询**，
**仍然会使用 Next-Key Lock**

---

## 场景二：普通索引区间查询

```sql
SELECT * FROM t WHERE age BETWEEN 10 AND 20 FOR UPDATE;
```

### 锁得更“广”

* 每一条 age=10 / 11 / … / 20
* 都是 Next-Key Lock
* **gap + record 全覆盖**

最终：

```
(prev_10,10] (10,11] ... (20,next)
```

---

# 五、为什么“区间查询”一定要 Next-Key？

因为区间查询的语义是：

> “我关心的是一个范围内 **所有可能存在的行**”

如果不锁 gap：

* 事务 B 可以在 `(10,20)` 插入新行
* 事务 A 再查 → 多出数据（幻读）

---

# 六、一个最容易踩坑的对比例子（强烈建议记住）

### 表结构

```sql
CREATE TABLE t (
  id INT PRIMARY KEY,
  age INT,
  KEY idx_age(age)
);
```

---

### SQL 1（安全）

```sql
SELECT * FROM t WHERE id = 10 FOR UPDATE;
```

* 锁：[10]
* 插入 id=11 ✅
* 插入 id=10 ❌

---

### SQL 2（容易被忽略）

```sql
SELECT * FROM t WHERE age = 10 FOR UPDATE;
```

* 锁：(prev,10]
* 插入 age=10 ❌
* 插入 age=9.5 ❌

---

# 七、再给你一个“边界场景”

```sql
SELECT * FROM t WHERE id > 20 FOR UPDATE;
```

即使 `id` 是唯一索引：

* 这是 **范围查询**
* 会锁：

```
(20,+∞)  ← 包含 supremum
```

👉 所有大于 20 的插入都会被阻塞

---

# 八、总结一句“工程级记忆法”

> **唯一索引只在“等值查询”时才能缩成 Record Lock；
> 一旦涉及“范围”，不管唯一还是普通索引，都会退化成 Next-Key Lock。**

---

如果你愿意，下一步我可以：

* 用 **真实 B+Tree 图**画出锁覆盖范围
* 给你一个 **“如何通过建索引减少 Next-Key Lock” 的实战清单**
* 或结合你当前业务 SQL，直接帮你判断 **哪里会被 gap 锁拖慢**

你想继续哪一个？
