package com.zhoubyte.scorpio_cloud_native.domain.image.valobj;

import lombok.Value;

/**
 * 镜像引用值对象，统一承载 Docker 镜像名和 K8s 镜像引用
 */
@Value
public class ImageReference {

    String registry;
    String repository;
    String tag;
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
