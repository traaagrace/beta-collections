
## exception 在代码设计中的使用原则
---
总的来说，受检查异常调用者必须要处理，通过调用者有处理方式能够解决问题，
非检查异常是代码中有错误，调用方无法处理，比如参数错误、数组越界、算术错误等等。

---

## 🎯 核心问题

**为什么 Java 要区分 Checked Exception 和 Unchecked Exception？**
因为它们解决的是两类完全不同的问题：

1. **Checked Exception（受检异常）**：

    * **业务上可能出错**，但调用方有办法处理。
    * 编译器强制你 `try-catch` 或者 `throws`。
    * 就像 API 在对你说：“小心，这里可能失败，你得考虑后果。”

2. **Unchecked Exception（运行时异常）**：

    * **程序写错了**，调用方无法补救。
    * 编译器不要求声明。
    * 就像系统在对你说：“哥们，这代码逻辑不对，修 bug 吧。”

---

## 📖 对比举例

### 场景一：文件读取

```java
String readFile(String path) throws IOException
```

* 可能的异常：文件不存在、没权限、磁盘坏了
* 调用者能做什么？

    * 重试
    * 提示用户“文件没找到，请重新选择”
    * 记录日志
      👉 所以这是 **Checked Exception** —— 调用者有“补救措施”。

---

### 场景二：数组越界

```java
int getValue(int[] arr, int i) {
    return arr[i]; // i 越界时抛出 ArrayIndexOutOfBoundsException
}
```

* 可能的异常：下标越界
* 调用者能做什么？

    * 除了修代码（保证 i < arr.length），啥也不能做
      👉 所以这是 **Unchecked Exception** —— 这是程序 bug，调用者没法恢复。

---

### 场景三：参数校验

```java
void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative");
    }
}
```

* 可能的异常：参数非法
* 这是谁的锅？调用者传错了！
* 调用者能做什么？修代码！
  👉 用 RuntimeException（Unchecked Exception）。

---

## 📌 设计原则（一句话版）

* **能恢复 → Checked**
  比如网络超时、文件丢失，调用者可以重试/换路径。

* **不能恢复 → Unchecked**
  比如 null 参数、数组越界、算术错误，调用者修不了，只能改代码。

---

## 🔑 类比生活

* **Checked Exception**：坐高铁，系统提示你“可能晚点”。你需要准备 Plan B，比如改签或退票。
* **Unchecked Exception**：你买票时身份证号写错了，直接被拒绝登车。这不是火车的问题，是你自己填错了。

---

👉 所以 Java 设计者强制 Checked Exception **必须声明**，是为了让调用方 **提前知道并处理那些可能“正常失败”的情况**。
而 RuntimeException 不强制声明，因为那都是 **编程错误**，写代码的人应该修，而不是调用者去处理。

---

要不要我给你画一张 **“判断异常类型的流程图”**？你一看就能知道某个异常该设计成 Checked 还是 Unchecked。
