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
│                    Application Layer                     │
│              ScorpioCloudNativeApplication               │
├─────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                  │
│                         infra/config                     │
│    ScorpioInfraConfig / DockerComponentConfig /          │
│                   K8sComponentConfig                     │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                        │
│  image / workload / network / storage / node / platform │
│         entity (聚合根) / valobj (值对象) / repository   │
└─────────────────────────────────────────────────────────┘
```

### 设计原则

1. **统一建模**：Docker 和 K8s 共用一套领域对象，通过 `PlatformType` 枚举区分平台来源
2. **DDD 分层**：领域层与基础设施层分离，领域层不依赖具体 SDK
3. **值对象复用**：`ResourceQuota`、`PortSpec`、`VolumeSpec` 等在两个平台语义下通用
4. **聚合根封装**：每个领域有一个聚合根，统一管理该领域的实体和值对象

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
| `Platform` | Docker daemon 连接 | K8s cluster 连接 | 平台连接配置 |

### PlatformType 枚举

```java
public enum PlatformType {
    DOCKER("docker"),
    K8S("k8s");
}
```

**作用**：标识领域对象的来源平台，用于运行时判断和平台特定逻辑处理。

### Workload 聚合根

```java
@Getter
@Builder
public class Workload {
    String id;
    String name;
    String namespace;           // K8s namespace，Docker 无此概念
    PlatformType platformType;  // 来源平台
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
- 通过 `isDockerWorkload()` / `isK8sWorkload()` 方法判断平台来源

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
      auth: ...
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
| `host` | String | - | Docker daemon 地址，如 `http://localhost:2376` |
| `tls-verify` | Boolean | true | 是否启用 TLS 验证 |
| `auth.username` | String | - | Registry 用户名 |
| `auth.password` | String | - | Registry 密码 |
| `auth.email` | String | - | Registry 邮箱 |
| `auth.url` | String | - | Registry 地址，如 `https://index.docker.io/v1/` |

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

每个聚合根对应一个仓储接口，定义基本的 CRUD 操作：

```java
public interface WorkloadRepository {
    Workload save(Workload workload);
    Optional<Workload> findById(String id);
    List<Workload> findAll();
    void deleteById(String id);
}
```

**设计说明**：
- 仓储接口定义在领域层，不依赖具体实现
- 实现类放在基础设施层，分别对接 docker-java 和 kubernetes-client
- 通过 `PlatformType` 判断使用哪个实现

---

## 项目结构

```
src/main/java/com/zhoubyte/scorpio_cloud_native/
├── domain/
│   ├── image/
│   │   ├── entity/ContainerImage.java
│   │   ├── repository/ContainerImageRepository.java
│   │   └── valobj/ImageReference.java
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
│   │   ├── entity/ComputeNode.java
│   │   ├── repository/ComputeNodeRepository.java
│   │   └── valobj/
│   │       ├── NodeState.java
│   │       └── NodeStatus.java
│   ├── platform/
│   │   ├── entity/Platform.java
│   │   ├── repository/PlatformRepository.java
│   │   └── valobj/
│   │       ├── AuthCredential.java
│   │       ├── ConnectionEndpoint.java
│   │       └── PlatformType.java
│
├── infra/
│   └── config/
│       ├── ScorpioInfraConfig.java
│       ├── DockerComponentConfig.java
│       └── K8sComponentConfig.java
│
└── ScorpioCloudNativeApplication.java
```