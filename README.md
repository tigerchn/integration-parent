# integration-parent

企业级 Maven 多模块骨架（Spring Boot 3.3.x + JDK 21），公共能力通过多个 **Spring Boot 3 自动配置 Starter** 提供。

## 模块说明

| 模块 | 职责 |
|------|------|
| `integration-parent` | 聚合工程；继承 `spring-boot-starter-parent`；**同一 POM 内集中 `dependencyManagement`**（第三方 + 内部 artifact 版本）。 |
| `integration-common-core` | **无 Spring** 的共享内核：`ApiResult`、`ResultCode`、`BizException`、`TraceConstants`、`IntegrationAsserts`。 |
| `integration-common-starters` | Starter 聚合父工程（`packaging=pom`）。 |
| `integration-common-*-starter` | 各领域自动装配（见各子模块 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`）。 |
| `integration-modules` | 可部署模块聚合（`packaging=pom`）：应用 API、消费端、示例工程。 |
| `integration-api-modules` | HTTP API 应用（admin-api、client-api）。 |
| `integration-consumer-modules` | 消费端（job-consumer、mq-consumer）。 |
| `integration-example-modules` | 示例工程聚合；其下为可运行的 Demo 与安全样例。 |
| `integration-demo` | 示例 Web 服务（位于 `integration-modules/integration-example-modules` 下）。 |
| `integration-security-sample` | 安全 Starter 联调样例（同上路径下）。 |

### 关于独立的 `integration-dependencies` BOM

若在 **父 POM 中 `import` 同仓库内的 BOM 子模块**，同时 BOM 子模块又以该父 POM 为 `<parent>`，Maven 会解析出 **import 循环**。本项目已将 BOM 内容 **合并进根 `pom.xml` 的 `dependencyManagement`**。

若你希望保留独立 BOM 目录，可采用其一：

- **方案 A**：BOM 工程 `<parent>` 固定为 `spring-boot-starter-parent`（与根工程同版本），且 **不要** 作为 `integration-parent` 的 `import` 子模块参与同一 reactor（单独 `mvn install` BOM 后再被引用），或  
- **方案 B**：根工程不 `import` BOM，由各业务模块自行 `dependencyManagement` import BOM（重复配置较多）。

当前仓库采用 **根 POM 统一管理版本**（方案最简单、IDE 与 CI 最省心）。

## 构建与打包

在项目根目录执行：

```bash
mvn clean verify
```

仅打包可运行 Demo（根目录下请使用 **artifact 选择器** `-pl :模块名`，与目录嵌套无关）：

```bash
mvn clean package -pl :integration-demo -am
```

Demo 可执行 JAR：

```text
integration-modules/integration-example-modules/integration-demo/target/integration-demo-1.0.0-SNAPSHOT.jar
```

运行：

```bash
java -jar integration-modules/integration-modules/integration-example-modules/integration-demo/target/integration-demo-1.0.0-SNAPSHOT.jar
```

开发态：

```bash
mvn spring-boot:run -pl :integration-demo
```

### integration-security-sample（安全 Starter 联调）

与 `integration-demo` 并行存在：端口 **8081**，显式开启 `integration.security.enabled=true`，`/api/public/**` 匿名可访问，`/api/secure/**` 需认证（未带凭证为 **401**）。自动化测试中通过 `@WithMockUser` 模拟已登录用户。

```bash
mvn spring-boot:run -pl :integration-security-sample
curl http://localhost:8081/api/public/ping
curl -i http://localhost:8081/api/secure/ping
```

生产接入 JWT 时，在同一文件中增加：

```yaml
integration:
  security:
    oauth2:
      resource-server:
        jwt:
          issuer-uri: https://your-idp/realms/your-realm
```

并相应收窄 `permit-all-patterns`。

## Demo 说明

- HTTP：`http://localhost:8080/api/demo/ping`  
- Knife4j（若引入 `integration-common-knife4j-starter`）：默认 `http://localhost:{port}/doc.html`；`springdoc` / `knife4j` 平台默认项由 starter 提供，业务只需配置 `integration.openapi.title` / `version`。  
- 日志：`application.yml` 中 `logging.pattern.console` 已包含 MDC 占位符 `%X{traceId:-}`，与 `integration-common-log-starter` 中过滤器写入的 `traceId` 对齐；请求头可传 `X-Trace-Id` 透传链路 ID。
- 安全：`integration-common-security-starter` 仅在 **`integration.security.enabled=true`** 时才会注册 `SecurityFilterChain`；关闭或未配置时 **本 starter 不声明任何安全相关 Bean**，由业务工程自行编写 Spring Security 配置（或不引入该 starter）。若已引入 `spring-boot-starter-security` 却未定义任何 `SecurityFilterChain`，仍将受到 **Spring Boot 默认安全策略** 影响，需在业务侧处理。
- 链路：引入 `integration-common-tracing-starter` 后仍会做追踪上下文；默认 **不会** 连 Zipkin（Demo 已排除 `ZipkinAutoConfiguration`）。若要上报，本地启动 Zipkin 或 OTLP 收集端后删除该 `exclude`，并配置 `management.zipkin.tracing.endpoint`（或改用 Boot 3.4+ 的导出开关）。

## Starter / Core 一览

| 组件 | 作用 |
|------|------|
| `integration-common-core` | 统一返回体、错误码、`IntegrationException` / `BizException`、Trace 常量、断言 |
| `integration-common-tool-starter` | `JsonUtil`（基于 `ObjectMapper`），依赖 core |
| `integration-common-log-starter` | Servlet 环境下 Trace 过滤器 + MDC，依赖 core |
| `integration-common-web-starter` | `GlobalExceptionHandler`（依赖 core + log-starter） |
| `integration-common-security-starter` | 仅当 `integration.security.enabled=true` 时注册 `SecurityFilterChain`（JWT Resource Server 可选 + 匿名路径）；否则不参与装配，完全交给业务 |
| `integration-common-tracing-starter` | Brave + Zipkin reporter；行为由 `management.tracing.*` 等 Boot 原生项控制 |
| `integration-common-redis-starter` | 聚合 `spring-boot-starter-data-redis` 与连接池依赖 |
| `integration-common-mybatis-plus-starter` | 分页插件（含 `mybatis-plus-jsqlparser`） |
| `integration-common-mq-starter` | `Jackson2JsonMessageConverter` |
| `integration-common-mail-starter` | 邮件扩展配置占位 |
| `integration-common-knife4j-starter` | Knife4j + springdoc 平台默认；默认 `OpenAPI` bean |
| `integration-common-job-starter` | `@EnableScheduling` |
| `integration-common-websocket-starter` | `ServerEndpointExporter` |
| `integration-common-test-starter` | 聚合 `spring-boot-starter-test`（请 **`scope=test`** 引用） |

### 配置前缀示例

- `integration.security.*`：是否启用安全、匿名路径、JWT issuer（`oauth2.resource-server.jwt.issuer-uri`）；启用后链路为无 Session、关闭匿名身份，并对未认证请求返回 **401**（便于 REST / JWT 场景）
- 各 starter 领域异常应继承 `IntegrationException`（或 `BizException`），由 `integration-common-web-starter` 的 `GlobalExceptionHandler` 统一转为 `ApiResult` 与对应 HTTP 状态码
- `integration.mybatis-plus.*`：分页开关、`DbType`、`overflow`  
- `integration.openapi.*`：`enabled`（默认 `true`）、`title`、`version`；`enabled: false` 时关闭 api-docs 与 UI（生产可显式关闭）  
- `integration.mail.*`：如 `fromDisplayName`  
- `management.tracing.*`、`management.zipkin.tracing.*`：采样与导出  

## 微服务落地时的扩展建议

- **契约模块**：各服务抽取 `xxx-api`（Feign/DTO/枚举），避免 Web 层模型直连数据库实体。  
- **注册与配置**：生产引入 Nacos / Consul + Spring Cloud LoadBalancer；配置中心托管 `application-{profile}.yml`。  
- **观测**：Micrometer Tracing + Prometheus/Grafana；日志对接 ELK/OpenSearch。  
- **安全**：在网关或资源服务器上统一鉴权；方法级授权可再叠加 `@PreAuthorize` 等。  
- **Knife4j**：建议仅 `dev`/`local` profile 启用，减少生产暴露面。  
- **MQ**：当前示例以 RabbitMQ 为主；若统一 RocketMQ/Kafka，建议单独 starter 以免引入无用客户端。
