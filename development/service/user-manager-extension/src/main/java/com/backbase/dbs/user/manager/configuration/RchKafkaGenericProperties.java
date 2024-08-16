package com.backbase.dbs.user.manager.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@ConfigurationProperties("rch.kafka")
@Validated
public class RchKafkaGenericProperties {

    @NotEmpty
    private String bootstrapServer;

    @NotEmpty
    private String groupId;

    @NotEmpty
    private String upsertUserErrorTopicName;

    @Value("${rch.kafka.poll-duration:5000}")
    private long pollDuration;

    @Value("${rch.kafka.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${rch.kafka.backoff.initial-interval:5000}")
    private long backOffInitialInterval;

    @Value("${rch.kafka.backoff.multiplier:2.0}")
    private double backOffMultiplier;

    @Value("${rch.kafka.backoff.max-interval:30000}")
    private long backOffMaxInterval;
}
