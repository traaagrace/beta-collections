下面我会**系统、完整** explanation Java **泛型（Generics）**的**所有常见与高级用法**，从**为什么需要泛型 → 基础语法 → 约束 → 通配符 → 擦除 → 高级技巧 → 实战建议**，基本覆盖**你在 Java 中能遇到的全部泛型使用场景**。

---

## 一、什么是泛型 & 为什么要用

### 1️⃣ 泛型解决的问题

* **类型安全**（编译期检查，避免 ClassCastException）
* **消除强制类型转换**
* **提高代码复用性**

❌ 没有泛型：

```java
List list = new ArrayList();
list.add("abc");
String s = (String) list.get(0);
```

✅ 使用泛型：

```java
List<String> list = new ArrayList<>();
String s = list.get(0);
```

---

## 二、泛型的 5 种核心使用位置（非常重要）

Java 泛型一共可以用在 **5 个地方**：

| 位置    | 示例                        |
| ----- | ------------------------- |
| 泛型类   | `class Box<T>`            |
| 泛型接口  | `interface Repository<T>` |
| 泛型方法  | `<T> T method(T t)`       |
| 泛型参数  | `List<T>`                 |
| 泛型返回值 | `T get()`                 |

---

## 三、泛型类（Generic Class）

### 1️⃣ 基本定义

```java
class Box<T> {
    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
```

### 2️⃣ 使用

```java
Box<String> box = new Box<>();
box.set("hello");
```

### 3️⃣ 多个泛型参数

```java
class Pair<K, V> {
    private K key;
    private V value;
}
```

---

## 四、泛型接口（Generic Interface）

### 1️⃣ 定义

```java
interface Repository<T> {
    T findById(Long id);
}
```

### 2️⃣ 实现方式一：实现时指定类型

```java
class UserRepository implements Repository<User> {
    public User findById(Long id) { ... }
}
```

### 3️⃣ 实现方式二：实现类继续泛型化

```java
class BaseRepository<T> implements Repository<T> {
    public T findById(Long id) { ... }
}
```

---

## 五、泛型方法（Generic Method）

> **泛型方法与泛型类无关，最容易混淆**

### 1️⃣ 基本形式

```java
public static <T> T getFirst(T[] array) {
    return array[0];
}
```

### 2️⃣ 调用

```java
String s = getFirst(new String[]{"a", "b"});
```

### 3️⃣ 明确指定泛型类型（很少用）

```java
Integer i = Util.<Integer>getFirst(arr);
```

---

## 六、泛型的限定（Bounded Type）

### 1️⃣ 上界限定 `extends`

```java
class Box<T extends Number> {
}
```

✅ T 只能是 `Number` 或其子类

```java
Box<Integer> b1; // OK
Box<String> b2;  // 编译错误
```

---

### 2️⃣ 多重上界

```java
class Test<T extends Number & Comparable<T>> {
}
```

⚠️ 规则：

* **类只能放第一个**
* 接口可以放后面多个

---

## 七、通配符（Wildcard）—— 泛型最难点 ⭐⭐⭐

### 1️⃣ 无界通配符 `<?>`

```java
List<?> list;
```

* 表示**未知类型**
* 只能读取，不能写（除了 null）

```java
Object o = list.get(0);
list.add(null); // 唯一允许
```

---

### 2️⃣ 上界通配符 `<? extends T>`（生产者）

```java
List<? extends Number> list;
```

* 接受 `Number` 及其子类
* **只能读，不能写**

```java
Number n = list.get(0);
```

📌 记忆口诀：

> **Producer Extends（生产者用 extends）**

---

### 3️⃣ 下界通配符 `<? super T>`（消费者）

```java
List<? super Integer> list;
```

* 接受 `Integer` 及其父类
* **只能写 Integer 或子类**

```java
list.add(10);
Object o = list.get(0);
```

📌 记忆口诀：

> **Consumer Super（消费者用 super）**

---

## 八、泛型擦除（Type Erasure）⭐⭐⭐

### 1️⃣ 什么是擦除

> **Java 泛型是编译期的，运行期不存在**

```java
List<String> list1 = new ArrayList<>();
List<Integer> list2 = new ArrayList<>();

System.out.println(list1.getClass() == list2.getClass()); // true
```

---

### 2️⃣ 擦除规则

| 写法                   | 运行期类型  |
| -------------------- | ------ |
| `<T>`                | Object |
| `<T extends Number>` | Number |

---

### 3️⃣ 泛型无法做的事

❌ 不能：

* `new T()`
* `T.class`
* `instanceof T`
* `new T[]`

---

## 九、泛型与继承（常见误区）

❌ 错误理解：

```java
List<Integer> 不是 List<Number>
```

✅ 正确方式：

```java
List<? extends Number>
```

---

## 十、泛型数组（不推荐）

❌ 不能直接创建：

```java
T[] arr = new T[10]; // 编译错误
```

✅ 替代方案：

```java
List<T> list = new ArrayList<>();
```

---

## 十一、泛型在异常中的限制

❌ 泛型不能用于异常

```java
class MyException<T> extends Exception {} // 不允许
```

---

## 十二、泛型的桥接方法（编译器自动生成）

```java
class Parent<T> {
    T get() {}
}

class Child extends Parent<String> {
    String get() {}
}
```

➡ 编译器生成桥接方法保证多态

---

## 十三、真实项目中的典型用法（结合你学的内容）

### 1️⃣ DAO / Repository

```java
interface BaseMapper<T> {
    T selectById(Long id);
}
```

### 2️⃣ 排序策略（策略模式 + 泛型）

```java
interface SortStrategy<T> {
    int compare(T a, T b);
}
```

### 3️⃣ 通用返回结果

```java
class Result<T> {
    private int code;
    private T data;
}
```

---

## 十四、泛型最佳实践（面试 & 实战）

✅ **能用泛型就用**
✅ **优先使用通配符提高灵活性**
✅ **集合参数优先用 `? extends / ? super`**
❌ **不要滥用复杂泛型**

---

## 十五、泛型核心口诀（送你）

> **类/接口定义用 T
> 方法独立用 `<T>`
> 读用 extends
> 写用 super
> 运行期全被擦除**

---

如果你愿意，我可以下一步帮你：

* 🎯 用 **Spring / MyBatis / 排序模块** 场景讲泛型
* 🎯 专门用 **图 + 表格** 讲通配符
* 🎯 出 **面试题 + 易错题**

你想从哪一块继续？
