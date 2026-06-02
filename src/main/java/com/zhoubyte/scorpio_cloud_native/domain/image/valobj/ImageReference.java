package com.zhoubyte.scorpio_cloud_native.domain.image.valobj;

/**
 * 镜像引用值对象，统一承载 Docker 镜像名和 K8s 镜像引用
 *
 * @param registry   镜像仓库地址，如 docker.io、registry.k8s.io
 * @param repository 镜像仓库路径，如 library/nginx、kube-apiserver
 * @param tag        镜像标签，如 latest、v1.0.0
 * @param digest     镜像摘要，如 sha256:xxx，用于精确标识镜像版本
 */
public record ImageReference(String registry, String repository, String[] tag, String[] digest) {

    public static ImageReference of(String registry, String repository, String[] tag) {
        return new ImageReference(registry, repository, tag, null);
    }

    public static ImageReference of(String repository, String[] tag) {
        return new ImageReference(null, repository, tag, null);
    }

    public static ImageReference withDigest(String registry, String repository, String[] digest) {
        return new ImageReference(registry, repository, null, digest);
    }

    public static ImageReference of(String[] repoTags, String[] digest) {
        return new ImageReference(null, null, repoTags, digest);
    }

}