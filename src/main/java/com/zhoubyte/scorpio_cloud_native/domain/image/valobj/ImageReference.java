package com.zhoubyte.scorpio_cloud_native.domain.image.valobj;

import lombok.Value;

/**
 * 镜像引用值对象，统一承载 Docker 镜像名和 K8s 镜像引用
 */
@Value
public class ImageReference {

    /** 镜像仓库地址，如 docker.io、registry.k8s.io */
    String registry;

    /** 镜像仓库路径，如 library/nginx、kube-apiserver */
    String repository;

    /** 镜像标签，如 latest、v1.0.0 */
    String tag;

    /** 镜像摘要，如 sha256:xxx，用于精确标识镜像版本 */
    String digest;

    public static ImageReference of(String registry, String repository, String tag) {
        return new ImageReference(registry, repository, tag, null);
    }

    public static ImageReference of(String repository, String tag) {
        return new ImageReference(null, repository, tag, null);
    }

    public static ImageReference withDigest(String registry, String repository, String digest) {
        return new ImageReference(registry, repository, null, digest);
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (registry != null && !registry.isEmpty()) {
            sb.append(registry).append("/");
        }
        sb.append(repository);
        if (tag != null && !tag.isEmpty()) {
            sb.append(":").append(tag);
        }
        if (digest != null && !digest.isEmpty()) {
            sb.append("@").append(digest);
        }
        return sb.toString();
    }

}