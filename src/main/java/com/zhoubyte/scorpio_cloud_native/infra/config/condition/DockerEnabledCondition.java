package com.zhoubyte.scorpio_cloud_native.infra.config.condition;

import com.zhoubyte.scorpio_cloud_native.infra.config.common.ScorpioInfraConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.List;
import java.util.Objects;

public class DockerEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ScorpioInfraConfig scorpioInfraConfig
                = Objects.requireNonNull(context.getBeanFactory()).getBean(ScorpioInfraConfig.class);

        List<String> types = scorpioInfraConfig.getTypes();
        if (types == null || types.isEmpty()) {
            return false;
        }
        
        return types.contains("docker");
    }
}