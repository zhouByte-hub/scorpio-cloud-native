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
| `labels` | Map<String, Object> | 镜像标签过滤，value 为 null 时仅匹配 key 存在 |

**响应体**：
```json
{
  "images": [...],
  "total": 1,
  "platform": "DOCKER"
}
```

### 计算节点查询

**请求**：
```
GET /cloud-native/node/list/{platform}:query
```

**路径参数**：

| 参数 | 说明 |
|---|---|
| `platform` | 平台类型，可选值：`DOCKER`、`K8S` |

**响应体**：
```json
{
  "items": [
    {
      "id": "9a215a5a-60f7-4d57-ae62-c9745bca91df",
      "name": "docker-desktop",
      "status": {
        "state": "READY",
        "message": "Docker Engine running",
        "capacity": {
          "cpuCores": 10.0,
          "memoryMB": 7937
        },
        "allocatable": {
          "cpuCores": 10.0,
          "memoryMB": 7937
        },
        "conditions": {
          "osType": "linux",
          "architecture": "aarch64",
          "kernelVersion": "6.12.76-linuxkit",
          "serverVersion": "29.5.2",
          "cGroupVersion": "2",
          "driver": "overlayfs"
        }
      },
      "labels": [
        "com.docker.desktop.address=unix:///Users/zhoujianing/Library/Containers/com.docker.docker/Data/docker-cli.sock"
      ],
      "addresses": {
        "hostname": "docker-desktop",
        "dockerSocket": "unix:///Users/zhoujianing/Library/Containers/com.docker.docker/Data/docker-cli.sock"
      },
      "createdAt": "2026-06-03T06:55:57.867359625Z"
    }
  ],
  "total": 1,
  "platform": "DOCKER"
}
```

**说明**：
- Docker 平台返回 Docker Host 信息（单机模式）
- K8s 平台返回集群中所有 Node 信息

### 存储卷查询

**请求**：
```
POST /cloud-native/volume/list/{platform}:query
Content-Type: application/json
```

**路径参数**：

| 参数 | 说明 |
|---|---|
| `platform` | 平台类型，可选值：`DOCKER`、`K8S` |

**请求体**：
```json
{
  "name": "",
  "driver": ""
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `name` | String | 存储卷名称（精确匹配），为空时不过滤 |
| `driver` | String | 存储驱动类型（精确匹配），为空时不过滤 |

**响应体**：
```json
{
  "items": [
    {
      "id": "my-volume",
      "name": "my-volume",
      "volumeType": "LOCAL",
      "spec": {
        "sourcePath": "/var/lib/docker/volumes/my-volume/_data",
        "volumeType": "LOCAL"
      },
      "labels": {
        "com.docker.some.label": "value"
      }
    }
  ],
  "total": 1,
  "platform": "DOCKER"
}
```

**说明**：
- Docker 平台通过 `listVolumesCmd` 获取卷列表，支持按名称和驱动过滤
- K8s 平台暂未实现，返回空列表
- `namespace`、`status`、`createdAt` 等字段在 Docker 场景下无法获取，留空
- `VolumeSpec` 中 `mountPath`、`accessModes`、`capacityGB`、`storageClassName`、`readOnly` 为 K8s 专属概念，Docker 场景下留空

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
    SERVICE_UNAVAILABLE(50300, "Service unavailable")
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

### ComputeNode 聚合根

```java
@Getter
@Builder
public class ComputeNode {
    String id;              // 节点唯一标识，Docker 为 Host ID，K8s 为 Node UID
    String name;            // 节点名称
    NodeStatus status;      // 节点状态
    Map<String, String> labels; // 标签，用于节点分类和调度选择
    Map<String, String> addresses; // 地址映射，如 InternalIP/ExternalIP/Hostname
    Instant createdAt;      // 创建时间
}
```

**Docker 实现**：
- `id` → Docker Host ID（从 `docker info` 获取）
- `name` → Docker Host 名称（如 "docker-desktop"）
- `status` → 固定为 `READY`，包含 CPU/内存容量信息
- `labels` → Docker labels
- `addresses` → hostname + Docker Socket 地址

**K8s 实现**：
- `id` → Node UID
- `name` → Node Name
- `status` → 从 Node Status 获取，包含 Ready/NotReady 状态
- `labels` → Node Labels
- `addresses` → InternalIP/ExternalIP/Hostname

### Volume 聚合根

```java
@Data
@Builder
public class Volume {
    String id;              // 存储卷唯一标识，Docker 为 Volume Name，K8s 为 PV/PVC UID
    String name;            // 存储卷名称
    String namespace;       // K8s 命名空间，Docker 场景下可为空
    VolumeType volumeType;  // 存储类型：LOCAL/HOST_PATH/NFS/BLOCK/CLOUD
    VolumeSpec spec;        // 存储规格配置
    String status;          // 存储状态：Available/Bound/Released/Failed
    Map<String, String> labels; // 标签，用于资源分类
    Instant createdAt;      // 创建时间
}
```

**Docker 实现**：
- `id` / `name` → Volume Name（从 `listVolumesCmd` 获取）
- `volumeType` → 通过 `getDriver()` 映射为 `VolumeType` 枚举
- `spec.sourcePath` → Volume Mountpoint
- `labels` → Volume Labels
- `namespace`、`status`、`createdAt` → Docker API 不提供，留空

**K8s 实现**（待开发）：
- `id` → PV/PVC UID
- `name` → PV/PVC Name
- `namespace` → PVC Namespace
- `volumeType` → 从 PV Spec 映射
- `spec` → 包含 accessModes、capacityGB、storageClassName 等完整信息
- `status` → PV/PVC 状态

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
    List<ContainerImage> queryImageByCondition(String imageName, String imageId, Map<String, Object> labels);
}

public interface ComputeNodeRepository {
    List<ComputeNode> queryComputeNode();
}

public interface VolumeRepository {
    List<Volume> queryStorage(String name, String drive);
}
```

**设计说明**：
- 仓储接口定义在领域层，不依赖具体实现
- 实现类放在基础设施层，分别对接 docker-java 和 kubernetes-client
- 通过 `PlatformEnum` 在 `ImageService` / `ComputerNodeService` 中路由到对应实现
- Service 通过构造函数注入所有 Repository 实现，按 `PlatformEnum.name()` 注册到 Map 中

---

## 项目结构

```
src/main/java/com/zhoubyte/scorpio_cloud_native/
├── facade/                                    # 接口层
│   ├── endpoint/
│   │   ├── ImageController.java               # 镜像查询接口
│   │   ├── ComputerNodeController.java        # 计算节点查询接口
│   │   └── VolumeController.java              # 存储卷查询接口
│   ├── request/
│   │   ├── ImageRequest.java                  # 镜像查询请求
│   │   └── VolumeRequest.java                 # 存储卷查询请求
│   ├── response/
│   │   ├── ImageListResponse.java             # 镜像列表响应
│   │   ├── ListResponse.java                  # 通用列表响应
│   │   └── ErrorResponse.java                 # 统一错误响应
│   ├── enums/
│   │   └── ErrorCode.java                     # 错误码枚举
│   └── exception/
│       └── GlobalExceptionHandler.java         # 全局异常处理器
│
├── application/                               # 应用层
│   ├── service/
│   │   ├── ImageService.java                  # 镜像查询服务
│   │   ├── ComputerNodeService.java           # 计算节点查询服务
│   │   └── VolumeService.java                 # 存储卷查询服务
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
│   │   ├── entity/Network.java
│   │   ├── repository/NetworkRepository.java
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
│   │   ├── entity/ComputeNode.java            # 计算节点聚合根
│   │   ├── repository/ComputeNodeRepository.java  # 计算节点仓储接口
│   │   └── valobj/
│   │       ├── NodeState.java                 # 节点状态枚举
│   │       └── NodeStatus.java                # 节点状态值对象
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
│   │   ├── DockerComputerNodeRepository.java   # Docker 计算节点仓储实现
│   │   └── DockerVolumeRepository.java         # Docker 存储卷仓储实现
│   ├── k8s/
│   │   ├── K8sContainerImageRepository.java   # K8s 镜像仓储实现
│   │   ├── K8sComputerNodeRepository.java     # K8s 计算节点仓储实现
│   │   ├── K8sNetworkRepository.java          # K8s 网络仓储实现
│   │   └── K8sVolumeRepository.java           # K8s 存储卷仓储实现
│   └── exception/
│       └── InfrastructureException.java        # 基础设施层异常
│
└── ScorpioCloudNativeApplication.java
```
