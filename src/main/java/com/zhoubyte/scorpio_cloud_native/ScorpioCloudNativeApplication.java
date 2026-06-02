package com.zhoubyte.scorpio_cloud_native;

import com.zhoubyte.scorpio_cloud_native.infra.config.common.ScorpioInfraConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ScorpioCloudNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScorpioCloudNativeApplication.class, args);
    }

}
