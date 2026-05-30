package com.zhoubyte.scorpio_cloud_native.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "scorpio-process")
@Configuration
@Data
public class ScorpioInfraConfig {

    private List<String> types;
    private Config config;

    @Data
    public static class Config {
        private DockerComponentConfig docker;
        private K8sComponentConfig k8s;
    }

}
