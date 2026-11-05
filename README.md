# 八方互充 · 智能电车车主智慧服务平台（后端）

> 新能源车主的智慧充电服务平台（找桩 / 充电 / 论坛 / 客服 / 紧急求助）的 **后端服务**，采用 REST + WebSocket 双通道，覆盖管理后台与用户端全链路。

## 技术栈

| 类别 | 选型 |
|---|---|
| 框架 | Spring Boot 3.5.9（Java 17） |
| ORM | MyBatis + MyBatis-Plus 3.5.5 |
| 认证授权 | Spring Security + JWT（jjwt 0.12.3） |
| 实时通信 | Spring WebSocket |
| AI 能力 | Spring AI 1.0.0-M6 + DeepSeek |
| 地图服务 | 高德地图 API |
| 接口文档 | Knife4j 3.0.2 |
| 工具库 | Hutool / Gson / FastJSON / PageHelper |

## 模块结构

```
com.baffang
├── admin/     # 管理后台（认证/审核/AI决策/客服/紧急响应/论坛/通知/统计/私桩管理）
├── user/      # 用户端（找桩/充电/论坛/客服/预约/私桩）
├── common/    # 公共（entity/enums/result/util/constant）
└── config/    # Security / JWT / WebSocket / AI / 高德地图
```

## 快速开始

### 环境依赖

- JDK 17+
- MySQL 8.0（数据库名 `baffang`）
- Maven（编译用）

### 数据库初始化

执行 `document/sql_init/init.sql` 初始化表结构与测试数据。

### 本地启动

```bash
mvn clean package -DskipTests
java -jar target\Baffang-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

> 本地开发需激活 `local` profile，以读取 `application-local.properties` 中的真实数据库密码与 API Key（该文件已 gitignore，不提交）。

### 服务地址

- 应用主页：http://localhost:8080
- API 文档（Knife4j）：http://localhost:8080/doc.html

## 配置说明（敏感信息）

`application.properties` 中的敏感值已改为环境变量占位符，**不提交真实密钥**：

| 占位符 | 说明 |
|---|---|
| `${DB_USERNAME}` | 数据库用户名（默认 `root`） |
| `${DB_PASSWORD}` | 数据库密码 |
| `${JWT_SECRET}` | JWT 签名密钥（HS256，≥32 字节随机串；未配置时应用启动失败） |
| `${DEEPSEEK_API_KEY}` | DeepSeek API Key |
| `${AMAP_API_KEY}` | 高德地图 API Key |

> 安全整改说明：JWT 密钥已不再硬编码在代码中（由 `JwtKeyConfig` 启动时注入校验）；短信验证码只保存在服务端并强制校验（演示可设 `VERIFICATION_CODE_ENABLED=false` 退回旧行为）；管理端接口权限统一由 `AdminGuard` 按库校验。

本地真实值请写入 `src/main/resources/application-local.properties`（已 gitignore），或通过环境变量注入。

## 前端对接

前端项目仓库见「八方互充管理后台前端」，通过 Vite 代理将 `/api/*` 转发到本服务（默认 `http://localhost:8080`），接口前缀 `/api/v1/admin`。
