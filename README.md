# 墨语智能写作后端

基于 Spring Boot 3 + MyBatis-Flex 的智能写作平台后端，提供用户、文章生成、支付、统计分析、头像上传和对象存储等能力。

## 技术栈

- Java 21
- Spring Boot 3.5
- MyBatis-Flex
- MySQL
- Redis + Spring Session
- Spring AI Alibaba DashScope
- AWS S3 SDK，适配 S3 协议对象存储
- Stripe Java SDK
- Knife4j / OpenAPI

## 功能概览

- 用户注册、登录、退出登录。
- 登录用户资料更新：昵称、头像、简介。
- 管理员用户管理：新增、查询、编辑、删除。
- 头像文件上传：上传到对象存储后返回可访问 URL。
- AI 文章创作：创建任务、标题确认、大纲确认、AI 修改大纲、正文生成、配图。
- SSE 文章生成进度推送。
- 文章列表、详情、删除。
- VIP 支付、退款、支付记录与 Stripe Webhook。
- 管理端统计分析。

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8+
- Redis 6+
- 可用的 DashScope API Key
- 可用的 S3 协议对象存储配置
- 如需支付功能，准备 Stripe API Key 与 Webhook Secret

## 快速开始

1. 创建并配置 MySQL 数据库，例如：

```text
ai_passage_creator
```

2. 启动 Redis：

```bash
redis-server
```

3. 配置本地环境。

默认激活 `local` profile。请在 `src/main/resources/application-local.yml` 中配置数据库、AI、对象存储和支付参数。不要提交真实密钥到仓库。

推荐用环境变量或本地私有配置管理敏感信息，例如：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_passage_creator
    username: your_mysql_user
    password: your_mysql_password
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}

os:
  config:
    accessKey: ${OS_ACCESS_KEY}
    secretKey: ${OS_SECRET_KEY}
    endpoint: your-s3-endpoint
    bucketName: your-bucket

stripe:
  api-key: ${STRIPE_API_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}
  success-url: http://localhost:5174/vip?success=true
  cancel-url: http://localhost:5174/vip?cancelled=true
```

4. 启动后端：

```bash
mvn spring-boot:run
```

服务默认运行在：

```text
http://localhost:8181/api
```

健康检查：

```text
GET http://localhost:8181/api/health/
```

## 常用命令

```bash
# 编译
mvn -DskipTests compile

# 运行测试
mvn test

# 启动服务
mvn spring-boot:run

# 打包
mvn -DskipTests package
```

## API 文档

启动服务后可访问 OpenAPI / Knife4j 文档：

```text
http://localhost:8181/api/v3/api-docs
http://localhost:8181/api/doc.html
```

前端的 `openapi2ts` 会读取：

```text
http://localhost:8181/api/v3/api-docs
```

## 核心接口

用户相关：

- `POST /user/register`：用户注册
- `POST /user/login`：用户登录
- `GET /user/get/login`：获取当前登录用户
- `POST /user/logout`：退出登录
- `POST /user/update/my`：当前用户更新个人资料
- `POST /user/upload/avatar`：上传头像文件并返回 URL
- `POST /user/list/page/vo`：管理员分页查询用户
- `POST /user/update`：管理员更新用户
- `POST /user/delete`：管理员删除用户

文章相关：

- `POST /article/create`：创建文章任务
- `GET /article/progress/{taskId}`：SSE 获取生成进度
- `POST /article/confirm-title`：确认标题
- `POST /article/confirm-outline`：确认大纲
- `POST /article/ai-modify-outline`：AI 修改大纲
- `GET /article/{taskId}`：获取文章详情
- `POST /article/list`：分页查询文章
- `POST /article/delete`：删除文章
- `GET /article/execution-logs/{taskId}`：获取任务执行统计与日志

支付与统计：

- `POST /payment/create-vip-session`：创建 VIP 支付会话
- `POST /payment/refund`：申请退款
- `GET /payment/records`：获取当前用户支付记录
- `GET /statistics/overview`：管理员获取系统统计数据

## 头像上传说明

接口：

```text
POST /user/upload/avatar
Content-Type: multipart/form-data
字段名: file
```

限制：

- 需要登录。
- 支持 `image/jpeg`、`image/png`、`image/gif`、`image/webp`。
- 文件大小不超过 `2MB`。
- 上传成功后返回对象存储 URL，前端再通过 `userAvatar` 字段保存到用户资料。

对象存储由 `CosService` 处理，当前通过 AWS S3 SDK 连接兼容 S3 协议的存储服务，头像默认上传到 `avatar/` 目录。

## 配置说明

`application.yml` 负责通用配置：

- 服务端口：`8181`
- Context path：`/api`
- Session 存储：Redis
- OpenAPI 扫描包
- MyBatis-Flex 驼峰映射配置

`application-local.yml` 负责本地环境配置：

- MySQL 数据源
- DashScope API Key
- Pexels API Key
- 对象存储配置
- Gemini 图片模型配置
- Stripe 支付配置

敏感信息应通过环境变量或本地私有配置注入，不要提交真实密钥。

## 权限说明

- 普通登录用户可以创建文章、查看自己的文章、编辑自己的资料、上传头像。
- VIP 和管理员不消耗普通创作配额。
- `/admin` 能力由 `@AuthCheck` 和用户角色控制。
- 用户登录态保存在 Session 中，Redis 用于 Session 持久化。

## 项目结构

```text
src/main/java/com/shuhang/
  annotation/      权限注解
  common/          通用响应、分页、删除请求
  config/          框架与第三方服务配置
  controller/      HTTP 接口
  exception/       异常与错误码
  manager/         业务管理器
  mapper/          MyBatis-Flex Mapper
  model/           实体、DTO、VO、枚举
  service/         业务服务
```

## 本地联调

前端默认请求：

```text
http://localhost:8181/api
```

后端默认允许基于 Session Cookie 识别登录态。前端 Axios 已开启 `withCredentials`，如跨域部署，需要同步检查后端 CORS 与 Cookie SameSite / Secure 配置。

## 注意事项

- 首次运行前请确认 MySQL 表结构已创建。
- 文章生成依赖 AI 服务和外部图片服务，相关 Key 未配置时创作流程可能失败。
- 支付功能依赖 Stripe 配置和 Webhook，开发环境建议使用 Stripe 测试模式。
- 对象存储 Bucket 需要具备可访问的公开读 URL，才能让前端头像正常回显。
