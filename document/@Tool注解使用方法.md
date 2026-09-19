# Spring AI 声明式工具实现指南

## 1. 核心概念

Spring AI 声明式工具实现是指通过注解标记普通 Java 方法作为 AI 可调用的工具，主要基于两个注解：`@Tool` 和 `@ToolParam`。声明式方式简化了工具的定义过程，使开发者能够快速创建 AI 可调用的功能。

## 2. @Tool 注解使用

### 2.1 基本用法

使用 `@Tool` 注解标记方法作为工具：

```java
@Tool(description = "获取用户时区的当前日期和时间")
public String getCurrentDateTime() {
    return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
}
```

### 2.2 参数说明

| 参数名            | 类型    | 默认值                               | 描述                                                 |
| ----------------- | ------- | ------------------------------------ | ---------------------------------------------------- |
| `name`            | String  | 空字符串                             | 工具名称，默认使用方法名                             |
| `description`     | String  | 空字符串                             | 工具功能描述，对 AI 理解如何调用工具至关重要         |
| `returnDirect`    | boolean | false                                | 是否将工具执行结果直接返回给调用方，无需经过 AI 处理 |
| `resultConverter` | Class   | DefaultToolCallResultConverter.class | 工具执行结果转换器，默认转换为 JSON 字符串           |

## 3. @ToolParam 注解使用

### 3.1 基本用法

使用 `@ToolParam` 注解标记工具方法的参数：

```java
@Tool(description = "设置指定时间的闹钟，时间格式为 ISO-8601")
public LocalDateTime setAlarm(@ToolParam(description = "闹钟时间，ISO-8601 格式") String time) {
    LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
    System.out.println("Alarm set for " + alarmTime);
    return alarmTime;
}
```

### 3.2 参数说明

| 参数名        | 类型    | 默认值   | 描述                           |
| ------------- | ------- | -------- | ------------------------------ |
| `required`    | boolean | true     | 参数是否为必填                 |
| `description` | String  | 空字符串 | 参数描述，帮助 AI 理解参数作用 |

### 3.3 其他相关注解

- `@Nullable`：标记参数为可选
- `@Schema`（Swagger）：用于生成更详细的 JSON Schema
- `@JsonProperty` (Jackson)：用于指定 JSON 序列化时的字段名

## 4. 方法定义规则与限制

### 4.1 支持的规则

- 支持静态方法或实例方法
- 支持任意可见性（public、protected、package-private、private）
- 参数个数任意，支持基本类型、POJOs、枚举、列表、数组、Map 等
- 返回值可以是 void 或其他可序列化类型

### 4.2 限制

- 参数和返回值不支持 `Optional` 类型
- 不支持异步类型（如 `CompletableFuture`、`Future`）
- 不支持响应式类型（如 `Flow`、`Flux`、`Mono`）
- 不支持函数类型（如 `Function`、`Supplier`、`Consumer`）
