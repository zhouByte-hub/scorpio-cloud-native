# Scorpio Cloud Native

## 项目概述

本项目采用 **领域驱动设计（DDD）** 架构，统一封装 Docker 和 Kubernetes 的核心概念，通过一套领域模型同时支持两个平台的操作。

---

## 技术栈

| 技术 | 版本 | 作用 |
|---|---|---|
| Java | 21 | 运行环境 |
| Spring Boot | 4.0.6 | 应用框架，提供配置绑定、依赖注入等能力 |
| Lombok | - | 简化代码，自动生成 getter/setter/builder 等 |
| docker-java | 3.7.1 | Docker SDK，与 Docker daemon 交互 |
| kubernetes-client (fabric8) | 7.7.0 | Kubernetes SDK，与 K8s 集群交互 |

---

## 核心依赖详解

### 1. docker-java

**Maven 依赖**：
```xml
<dependency>
    <groupId>com.github.docker-java</groupId>
    <artifactId>docker-java-core</artifactId>
    <version>3.7.1</version>
</dependency>
<dependency>
    <groupId>com.github.docker-java</groupId>
    <artifactId>docker-java-transport-httpclient5</artifactId>
    <version>3.7.1</version>
</dependency>
```

**作用**：
- `docker-java-core`：核心 API，提供容器、镜像、网络、存储卷等操作接口
- `docker-java-transport-httpclient5`：HTTP 传输层，通过 HTTP/HTTPS 与 Docker daemon 通信

**主要功能**：
- 容器生命周期管理（创建、启动、停止、删除）
- 镜像管理（拉取、构建、删除）
- 网络管理（创建、删除、连接）
- 存储卷管理
- 日志获取
- 命令执行

### 2. kubernetes-client (fabric8)

**Maven 依赖**：
```xml
<dependency>
    <groupId>io.fabric8</groupId>
    <artifactId>kubernetes-client</artifactId>
    <version>7.7.0</version>
</dependency>
```

**作用**：
- 提供完整的 Kubernetes API 客户端
- 支持 Pod、Deployment、Service、ConfigMap、Secret、PV/PVC 等资源操作
- 支持 Watch 机制（实时监听资源变化）
- 支持多种认证方式（kubeconfig、ServiceAccount、Token）

**主要功能**：
- 工作负载管理（Pod、Deployment、StatefulSet、DaemonSet）
- 服务发现（Service、Ingress）
- 配置管理（ConfigMap、Secret）
- 存储管理（PV、PVC、StorageClass）
- 节点管理
- 自动扩缩容（HPA）

---

## 架构设计

### 分层架构

```
┌─────────────────────────────────────────────────────────┐
│                      Facade Layer                        │
│    controller / request / response / exception handler   │
│         ImageController / GlobalExceptionHandler         │
├─────────────────────────────────────────────────────────┤
│                   Application Layer                      │
│            service / support / exception                 │
│              ImageService / PlatformEnum                 │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                        │
│    image / workload / network / storage / node           │
│         entity (聚合根) / valobj (值对象) / repository   │
├─────────────────────────────────────────────────────────┤
│                  Infrastructure Layer                    │
│     infra/config / infra/docker / infra/k8s             │
│    DockerClientConfig / DockerContainerImageRepository   │
│              K8sContainerImageRepository                 │
└─────────────────────────────────────────────────────────┘
```

### 设计原则

1. **统一建模**：Docker 和 K8s 共用一套领域对象，通过 `PlatformEnum` 枚举区分平台来源
2. **DDD 分层**：领域层与基础设施层分离，领域层不依赖具体 SDK
3. **值对象复用**：`ResourceQuota`、`PortSpec`、`VolumeSpec` 等在两个平台语义下通用
4. **聚合根封装**：每个领域有一个聚合根，统一管理该领域的实体和值对象
5. **异常分层**：每层定义独立异常类型（`DomainException` / `ApplicationException` / `InfrastructureException`），由全局异常处理器统一处理

---

## API 接口

### 镜像查询

**请求**：
```
POST /cloud-native/image/list/{platform}:query
Content-Type: application/json
```

**路径参数**：

| 参数 | 说明 |
|---|---|
| `platform` | 平台类型，可选值：`DOCKER`、`K8S` |

**请求体**：
```json
{
  "imageName": "nginx",
  "imageId": "",
  "labels": {}
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `imageName` | String | 镜像名称（模糊匹配） |
| `imageId` | String | 镜像 ID（精确匹配） |
| `labels` | Map<String, String> | 镜像标签过滤 |

**响应体**：
```json
{
  "items": [...],
  "total": 1,
  "platform": "DOCKER"
}
```

---

### 网络查询

**请求**：
```
POST /cloud-native/network/list/{platform}:query
Content-Type: application/json
```

**路径参数**：

| 参数 | 说明 |
|---|---|
| `platform` | 平台类型，可选值：`DOCKER`、`K8S` |

**请求体**：
```json
{
  "name": "bridge",
  "networkType": "BRIDGE",
  "labels": {}
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `name` | String | 网络名称 |
| `networkType` | String | 网络类型：BRIDGE / HOST / OVERLAY / CLUSTER_IP / NODE_PORT / LOAD_BALANCER |
| `labels` | Map<String, String> | 网络标签过滤 |

**响应体**：
```json
{
  "items": [...],
  "total": 6,
  "platform": "DOCKER"
}
```

---

## 运行结果示例

### Docker 镜像查询结果

> 文件路径：`results/docker/images.json`

```json
{
  "items": [
    {
      "createdAt": "2026-05-30T11:15:22Z",
      "id": "sha256:7651d7f24aad83fe68a222f7f20eded10d325c96ebee285ca5bf8162eddcba64",
      "imageName": "bitnami/postgresql:latest",
      "labels": {
        "build-date": "20260214",
        "name": "Photon OS aarch64/5.0 Base Image",
        "org.opencontainers.image.base.name": "docker.io/library/photon:5.0",
        "org.opencontainers.image.created": "2026-05-30T11:10:25Z",
        "org.opencontainers.image.description": "Application packaged by Broadcom, Inc.",
        "org.opencontainers.image.title": "postgresql",
        "org.opencontainers.image.vendor": "Broadcom, Inc.",
        "org.opencontainers.image.version": "18.4.0",
        "vendor": "VMware"
      },
      "reference": {
        "registry": null,
        "repository": null,
        "tag": ["bitnami/postgresql:latest"],
        "digest": ["bitnami/postgresql@sha256:7651d7f24aad83fe68a222f7f20eded10d325c96ebee285ca5bf8162eddcba64"]
      },
      "sizeBytes": 547142989
    },
    {
      "createdAt": "2026-05-29T08:05:42Z",
      "id": "sha256:d6b3087bc2302c2b49c875e1c5ad8c916fc10696ceb355be56cffd715e178d81",
      "imageName": "nacos/nacos-server:latest",
      "labels": {
        "maintainer": "pader <huangmnlove@163.com>",
        "org.opencontainers.image.created": "2026-05-29T08:02:05.295Z",
        "org.opencontainers.image.description": "This project contains a Docker image meant to facilitate the deployment of Nacos .",
        "org.opencontainers.image.licenses": "Apache-2.0",
        "org.opencontainers.image.revision": "cc8cf73795128b8f096c13d21abf29868dfe4b55",
        "org.opencontainers.image.source": "https://github.com/nacos-group/nacos-docker",
        "org.opencontainers.image.title": "nacos-docker",
        "org.opencontainers.image.url": "https://github.com/nacos-group/nacos-docker",
        "org.opencontainers.image.version": "v3.2.2"
      },
      "reference": {
        "tag": ["nacos/nacos-server:latest"],
        "digest": ["nacos/nacos-server@sha256:d6b3087bc2302c2b49c875e1c5ad8c916fc10696ceb355be56cffd715e178d81"]
      },
      "sizeBytes": 1965428363
    },
    {
      "createdAt": "2026-05-26T20:08:24Z",
      "id": "sha256:aa049e689e141a4358ad1d4562dc49c88a89fbab711fd8fcc33f684c80b26301",
      "imageName": "redis:latest",
      "labels": {},
      "reference": {
        "tag": ["redis:latest"],
        "digest": ["redis@sha256:aa049e689e141a4358ad1d4562dc49c88a89fbab711fd8fcc33f684c80b26301"]
      },
      "sizeBytes": 229002388
    },
    {
      "createdAt": "2026-04-20T18:15:33Z",
      "id": "sha256:7cda86a33344160309fdb65146332e4da65db81a945614f2fe32e210803f6fd1",
      "imageName": "ghcr.io/kafbat/kafka-ui:latest",
      "labels": {},
      "reference": {
        "tag": ["ghcr.io/kafbat/kafka-ui:latest"],
        "digest": ["ghcr.io/kafbat/kafka-ui@sha256:7cda86a33344160309fdb65146332e4da65db81a945614f2fe32e210803f6fd1"]
      },
      "sizeBytes": 637425775
    },
    {
      "createdAt": "2020-04-30T19:49:47Z",
      "id": "sha256:9c2d321d367c582fc103ad36b7326a0edd5b558e0c987d0bea3b58bac008b20f",
      "imageName": "redis:6.0.0",
      "labels": {},
      "reference": {
        "tag": ["redis:6.0.0"],
        "digest": ["redis@sha256:9c2d321d367c582fc103ad36b7326a0edd5b558e0c987d0bea3b58bac008b20f"]
      },
      "sizeBytes": 145276902
    }
  ],
  "platform": "DOCKER",
  "total": 5
}
```

---

### Docker 网络查询结果

> 文件路径：`results/docker/network.json`

```json
{
  "items": [
    {
      "createdAt": "2025-06-06T10:43:20.918Z",
      "id": "47552fed24ccb1c24235138c79d07964f720097467ba758f474fb15696b996bd",
      "labels": {
        "com.docker.compose.config-hash": "15dcad421ebb3df3e2e53dc0cb00938a0f4cbeafec99e5c95e4143e67cb2ce91",
        "com.docker.compose.network": "default",
        "com.docker.compose.project": "build",
        "com.docker.compose.version": "2.34.0"
      },
      "name": "build_default",
      "namespace": null,
      "networkType": "BRIDGE",
      "ports": null,
      "selectors": null,
      "spec": {
        "subnets": [
          {
            "subnet": "172.19.0.0/16",
            "gateway": "172.19.0.1",
            "ipRange": null
          }
        ],
        "driver": "bridge",
        "internal": false,
        "enableIPv6": false
      }
    },
    {
      "createdAt": "2025-06-05T06:14:04.410Z",
      "id": "5b5d756cdc2ee69d8423d8d4b440d4190654ae952b2cc580dcc8c37eca043451",
      "labels": {
        "com.docker.compose.config-hash": "7360af61b15dc2dd8d514b7136986bd4080d93d8dd383a8578aac52ae14cf63a",
        "com.docker.compose.network": "default",
        "com.docker.compose.project": "redpanda",
        "com.docker.compose.version": "2.34.0"
      },
      "name": "redpanda_default",
      "namespace": null,
      "networkType": "BRIDGE",
      "spec": {
        "subnets": [
          {
            "subnet": "172.18.0.0/16",
            "gateway": "172.18.0.1"
          }
        ],
        "driver": "bridge",
        "internal": false,
        "enableIPv6": false
      }
    },
    {
      "createdAt": "2025-05-09T01:45:07.314Z",
      "id": "6b487408d26b07dc83a7d270544a9af674fad723d16a96cecb338f61a0404f9a",
      "labels": {},
      "name": "host",
      "networkType": "HOST",
      "spec": {
        "subnets": [],
        "driver": "host",
        "internal": false,
        "enableIPv6": false
      }
    },
    {
      "createdAt": "2026-06-03T03:44:54.896Z",
      "id": "a9de671365572a87b32cd24b0a42128114acda31aa35ebf3ec1f9795a221dc41",
      "labels": {},
      "name": "bridge",
      "networkType": "BRIDGE",
      "spec": {
        "subnets": [
          {
            "subnet": "172.17.0.0/16",
            "gateway": "172.17.0.1"
          }
        ],
        "driver": "bridge",
        "internal": false,
        "enableIPv6": false
      }
    },
    {
      "createdAt": "2026-06-01T06:29:29.435Z",
      "id": "a5031c3c15fa67cc42bbf81161b6d3e496dae21f19910f95c978fd90025a887a",
      "labels": {},
      "name": "postgresql-network",
      "networkType": "BRIDGE",
      "spec": {
        "subnets": [
          {
            "subnet": "172.20.0.0/16",
            "gateway": "172.20.0.1"
          }
        ],
        "driver": "bridge",
        "internal": false,
        "enableIPv6": false
      }
    },
    {
      "createdAt": "2025-05-09T01:45:07.312Z",
      "id": "ceb68b5be098cf6530fafa5160b99355f474cfd1441d8d1ceca5eeffb706011d",
      "labels": {},
      "name": "none",
      "networkType": null,
      "spec": {
        "subnets": [],
        "driver": "null",
        "internal": false,
        "enableIPv6": false
      }
    }
  ],
  "platform": "DOCKER",
  "total": 6
}
```

---

## 异常体系

### 异常分层

| 异常类 | 所属层 | HTTP 状态码 | 错误码 | 说明 |
|---|---|---|---|---|
| `DomainException` | Domain | 400 | 40003 | 领域规则违反 |
| `ApplicationException` | Application | 500 | 50001 | 应用层操作失败 |
| `InfrastructureException` | Infrastructure | 500 | 50002 | 基础设施操作失败 |
| `IllegalArgumentException` | - | 400 | 40002 | 非法参数 |

### 错误码枚举

```java
public enum ErrorCode {
    SUCCESS(0, "Success"),
    BAD_REQUEST(40000, "Bad request"),
    VALIDATION_ERROR(40001, "Request validation failed"),
    ILLEGAL_ARGUMENT(40002, "Invalid argument provided"),
    DOMAIN_ERROR(40003, "Domain rule violation"),
    NOT_FOUND(40400, "Resource not found"),
    INTERNAL_ERROR(50000, "Internal server error"),
    APP_ERROR(50001, "Application operation failed"),
    INFRA_ERROR(50002, "Infrastructure operation failed"),
    SERVICE_UNAVAILABLE(50300, "Service unavailable");
}
```

### 错误响应格式

```json
{
  "code": 40002,
  "message": "Invalid argument provided",
  "detail": "不支持的平台类型: UNKNOWN",
  "timestamp": "2026-06-02 10:30:00",
  "path": "/cloud-native/image/list/UNKNOWN:query"
}
```

### 全局异常处理

`GlobalExceptionHandler` 通过 `@RestControllerAdvice` 统一捕获各层异常，转换为标准 `ErrorResponse`：

- `DomainException` → 400 + `DOMAIN_ERROR`
- `ApplicationException` → 500 + `APP_ERROR`
- `InfrastructureException` → 500 + `INFRA_ERROR`
- `IllegalArgumentException` → 400 + `ILLEGAL_ARGUMENT`
- `MethodArgumentNotValidException` → 400 + `VALIDATION_ERROR`
- 其他 `Exception` → 500 + `INTERNAL_ERROR`

---

## 领域模型详解

### 聚合根与平台对应关系

| 聚合根 | Docker 对应 | K8s 对应 | 说明 |
|---|---|---|---|
| `ContainerImage` | `docker images` | Pod spec image | 容器镜像信息 |
| `Workload` | `docker container` | Pod / Container | 运行实例 |
| `Network` | `docker network` | Service | 网络配置 |
| `Volume` | `docker volume` | PV / PVC | 存储卷 |
| `ComputeNode` | Docker Host | Node | 计算节点 |

### PlatformEnum 枚举

```java
@Getter
public enum PlatformEnum {
    DOCKER,
    K8S,
}
```

**作用**：标识平台类型，用于 `ImageService` 中路由到对应平台的 Repository 实现。

### ContainerImage 聚合根

```java
@Getter
@Builder
public class ContainerImage {
    String id;              // 镜像唯一标识，Docker 为 Image ID，K8s 为镜像 Digest
    String imageName;       // 镜像名称
    ImageReference reference; // 镜像引用信息，包含 registry、repository、tag、digest
    long sizeBytes;         // 镜像大小（字节）
    Instant createdAt;      // 镜像创建时间
    Map<String, String> labels; // 镜像标签，键值对形式的元数据
}
```

### ImageReference 值对象

```java
public record ImageReference(String registry, String repository, String[] tag, String[] digest) {

    public static ImageReference of(String registry, String repository, String[] tag);
    public static ImageReference of(String repository, String[] tag);
    public static ImageReference withDigest(String registry, String repository, String[] digest);
    public static ImageReference of(String[] repoTags, String[] digest);
}
```

**设计说明**：使用 Java 21 `record` 实现，不可变值对象。`tag` 和 `digest` 为数组，因为一个镜像可以有多个 tag 和 digest。

### Workload 聚合根

```java
@Getter
@Builder
public class Workload {
    String id;
    String name;
    String namespace;           // K8s namespace，Docker 无此概念
    WorkloadType workloadType;  // CONTAINER / POD / DEPLOYMENT 等
    List<ContainerSpec> containers;  // 容器规格列表（Pod 可包含多容器）
    WorkloadStatus status;      // 运行状态
    Map<String, String> labels;
    Map<String, String> annotations;
    Instant createdAt;
    Instant updatedAt;
}
```

**设计说明**：
- `containers` 为列表，因为 K8s Pod 可包含多个容器，Docker Container 只有一个
- `namespace` 仅 K8s 有意义，Docker 场景下可为空

### ContainerSpec 值对象

```java
@Value
@Builder
public class ContainerSpec {
    String name;
    ImageReference image;       // 镜像引用
    String command;             // 入口命令
    List<String> args;          // 命令参数
    List<EnvVar> envVars;       // 环境变量
    List<PortSpec> ports;       // 端口规格
    List<VolumeSpec> volumes;   // 存储卷挂载
    ResourceQuota resourceLimits;   // 资源限制
    ResourceQuota resourceRequests; // 资源请求（K8s 特有）
    HealthCheck livenessProbe;  // 存活探针
    HealthCheck readinessProbe; // 就绪探针（K8s 特有）
    boolean privileged;         // 特权模式
    String serviceAccountName;  // K8s ServiceAccount
}
```

**设计说明**：
- `resourceRequests` 和 `readinessProbe` 主要用于 K8s，Docker 场景下可为空
- `ImageReference` 统一镜像引用格式：`registry/repository:tag` 或 `registry/repository@digest`

---

## 配置详解

### 配置结构

```yaml
scorpio-process:
  types:              # 启用的平台类型列表
    - docker
    - k8s
  config:             # 平台连接配置
    docker:           # Docker 配置
      host: ...
      tls-verify: ...
      cert-path: ...
      auth: ...
      http: ...
      ssl: ...
    k8s:              # Kubernetes 配置
      master: ...
      kube-config: ...
      auth: ...
      watch: ...
```

### 配置类映射

```java
@ConfigurationProperties(prefix = "scorpio-process")
public class ScorpioInfraConfig {
    private List<String> types;
    private Config config;

    @Data
    public static class Config {
        private DockerComponentConfig docker;
        private K8sComponentConfig k8s;
    }
}
```

**映射规则**：
- `scorpio-process.types` → `types` 字段
- `scorpio-process.config.docker` → `config.docker` 字段
- `scorpio-process.config.k8s` → `config.k8s` 字段
- YAML 中的 `tls-verify` 自动映射为 `tlsVerify`（Spring Boot 松散绑定）

### Docker 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `host` | String | - | Docker daemon 地址，如 `unix:///var/run/docker.sock` |
| `tls-verify` | Boolean | true | 是否启用 TLS 验证 |
| `cert-path` | String | /home/user/.docker | TLS 证书路径 |
| `auth.username` | String | - | Registry 用户名 |
| `auth.password` | String | - | Registry 密码 |
| `auth.email` | String | - | Registry 邮箱 |
| `auth.url` | String | - | Registry 地址，如 `https://index.docker.io/v1/` |
| `http.connection-timeout` | Long | 10000 | 连接超时时间（毫秒） |
| `http.response-timeout` | Long | 5000 | 响应超时时间（毫秒） |
| `http.max-connect` | int | 10 | 最大连接数 |
| `ssl.enabled` | Boolean | false | 是否启用自定义 SSL |
| `ssl.protocol` | String | TLSv1.2 | SSL 协议版本 |
| `ssl.verify-hostname` | Boolean | true | 是否验证主机名 |
| `ssl.key-store.path` | String | - | KeyStore 文件路径 |
| `ssl.key-store.password` | String | - | KeyStore 密码 |
| `ssl.key-store.type` | String | JKS | KeyStore 类型 |
| `ssl.trust-store.path` | String | - | TrustStore 文件路径 |
| `ssl.trust-store.password` | String | - | TrustStore 密码 |
| `ssl.trust-store.type` | String | JKS | TrustStore 类型 |

### Kubernetes 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `master` | String | - | K8s API Server 地址，如 `https://mymaster.com` |
| `disable-autoconfig` | Boolean | false | 是否禁用自动配置（从 kubeconfig 自动发现） |
| `api-version` | String | v1 | API 版本 |
| `trust-certificates` | Boolean | true | 是否信任证书 |
| `kube-config` | String | ~/.kube/config | kubeconfig 文件路径 |
| `connection-timeout` | Long | 10000 | 连接超时时间（毫秒），0 表示无限 |
| `request-timeout` | Long | 10000 | 请求超时时间（毫秒） |
| `retry-limit` | Long | 10 | 重试次数，-1 表示无限重试 |
| `retry-interval` | Long | 100 | 重试间隔（毫秒） |
| `auth.try-service-account` | Boolean | true | 是否尝试使用 ServiceAccount 认证 |
| `auth.try-kube-config` | Boolean | true | 是否尝试使用 kubeconfig 认证 |
| `watch.reconnect-interval` | Long | 1000 | Watch 重连间隔（毫秒） |
| `watch.reconnect-limit` | Long | -1 | Watch 重连次数，-1 表示无限 |

---

## 值对象详解

### ResourceQuota（资源配额）

```java
@Value
public class ResourceQuota {
    Double cpuCores;   // CPU 核数，如 0.5 表示 500m
    Long memoryMB;     // 内存大小（MB）
}
```

**Docker 对应**：`docker run --cpus=0.5 --memory=512m`
**K8s 对应**：`resources.limits.cpu` / `resources.limits.memory`

### PortSpec（端口规格）

```java
@Value
public class PortSpec {
    String name;        // 端口名称
    int containerPort;  // 容器端口
    Integer hostPort;   // 主机端口（可选）
    String protocol;    // 协议：TCP/UDP
}
```

**Docker 对应**：`docker run -p 8080:80`
**K8s 对应**：`containerPort` / `hostPort`

### VolumeSpec（存储卷规格）

```java
@Value
public class VolumeSpec {
    String name;        // 卷名称
    String sourcePath;  // 源路径
    String mountPath;   // 挂载路径
    boolean readOnly;   // 是否只读
    VolumeType type;    // 类型：HOST_PATH / EMPTY_DIR / PVC 等
}
```

**Docker 对应**：`docker run -v /host/path:/container/path`
**K8s 对应**：`volumeMounts` / `volumes`

### HealthCheck（健康检查）

```java
@Value
public class HealthCheck {
    HealthCheckType type;      // HTTP / TCP / COMMAND
    String command;            // 命令检查的命令
    String httpPath;           // HTTP 检查的路径
    int httpPort;              // HTTP 检查的端口
    long intervalSeconds;      // 检查间隔
    long timeoutSeconds;       // 超时时间
    long initialDelaySeconds;  // 初始延迟
    int failureThreshold;      // 失败阈值
}
```

**Docker 对应**：`HEALTHCHECK --interval=30s CMD curl -f http://localhost/`
**K8s 对应**：`livenessProbe` / `readinessProbe`

### EnvVar（环境变量）

```java
@Value
public class EnvVar {
    String key;       // 变量名
    String value;     // 变量值
    String valueFrom; // 值来源（K8s 可引用 ConfigMap/Secret）
}
```

**Docker 对应**：`docker run -e KEY=value`
**K8s 对应**：`env` / `envFrom`

---

## 仓储接口

每个聚合根对应一个仓储接口，定义操作方法：

```java
public interface ContainerImageRepository {
    List<ContainerImage> queryImageByCondition(String imageName, String imageId, Map<String, String> labels);
}

public interface NetworkRepository {
    List<CloudNativeNetwork> queryNetworkByCondition(String name, String subnet, String gateway, NetworkType networkType);
}
```

**设计说明**：
- 仓储接口定义在领域层，不依赖具体实现
- 实现类放在基础设施层，分别对接 docker-java 和 kubernetes-client
- 通过 `PlatformEnum` 在 `ImageService` 中路由到对应实现
- `ImageService` 通过构造函数注入所有 `ContainerImageRepository` 实现，按 `PlatformEnum.name()` 注册到 Map 中

---

## 项目结构

```
src/main/java/com/zhoubyte/scorpio_cloud_native/
├── facade/                                    # 接口层
│   ├── endpoint/
│   │   ├── ImageController.java               # 镜像查询接口
│   │   └── CloudNativeNetworkController.java  # 网络查询接口
│   ├── request/
│   │   ├── ImageRequest.java                  # 镜像查询请求
│   │   └── NetworkRequest.java                # 网络查询请求
│   ├── response/
│   │   ├── ListResponse.java                  # 通用列表响应（泛型）
│   │   └── ErrorResponse.java                 # 统一错误响应
│   ├── enums/
│   │   └── ErrorCode.java                     # 错误码枚举
│   └── exception/
│       └── GlobalExceptionHandler.java         # 全局异常处理器
│
├── application/                               # 应用层
│   ├── service/
│   │   ├── ImageService.java                  # 镜像查询服务
│   │   └── NetworkService.java                # 网络查询服务
│   ├── support/
│   │   └── PlatformEnum.java                  # 平台枚举
│   └── exception/
│       └── ApplicationException.java           # 应用层异常
│
├── domain/                                    # 领域层
│   ├── image/
│   │   ├── entity/ContainerImage.java         # 镜像聚合根
│   │   ├── repository/ContainerImageRepository.java  # 镜像仓储接口
│   │   └── valobj/ImageReference.java         # 镜像引用值对象
│   ├── workload/
│   │   ├── entity/Workload.java
│   │   ├── repository/WorkloadRepository.java
│   │   └── valobj/
│   │       ├── ContainerSpec.java
│   │       ├── ContainerState.java
│   │       ├── EnvVar.java
│   │       ├── HealthCheck.java
│   │       ├── PortSpec.java
│   │       ├── ResourceQuota.java
│   │       ├── VolumeSpec.java
│   │       ├── WorkloadStatus.java
│   │       └── WorkloadType.java
│   ├── network/
│   │   ├── entity/CloudNativeNetwork.java     # 网络聚合根
│   │   ├── repository/NetworkRepository.java  # 网络仓储接口
│   │   └── valobj/
│   │       ├── NetworkSpec.java
│   │       ├── NetworkType.java
│   │       └── ServicePort.java
│   ├── storage/
│   │   ├── entity/Volume.java
│   │   ├── repository/VolumeRepository.java
│   │   └── valobj/
│   │       ├── VolumeAccessMode.java
│   │       ├── VolumeSpec.java
│   │       └── VolumeType.java
│   ├── node/
│   │   ├── entity/ComputeNode.java
│   │   ├── repository/ComputeNodeRepository.java
│   │   └── valobj/
│   │       ├── NodeState.java
│   │       └── NodeStatus.java
│   └── exception/
│       └── DomainException.java                # 领域层异常
│
├── infra/                                     # 基础设施层
│   ├── config/
│   │   ├── DockerClientConfig.java            # Docker 客户端配置（含 SSL）
│   │   └── common/
│   │       ├── ScorpioInfraConfig.java        # 全局配置绑定
│   │       ├── DockerComponentConfig.java     # Docker 配置项
│   │       └── K8sComponentConfig.java        # K8s 配置项
│   ├── docker/
│   │   ├── DockerContainerImageRepository.java # Docker 镜像仓储实现
│   │   └── DockerNetworkRepository.java       # Docker 网络仓储实现
│   ├── k8s/
│   │   ├── K8sContainerImageRepository.java   # K8s 镜像仓储实现
│   │   └── K8sNetworkRepository.java          # K8s 网络仓储实现
│   └── exception/
│       └── InfrastructureException.java        # 基础设施层异常
│
└── ScorpioCloudNativeApplication.java
```
