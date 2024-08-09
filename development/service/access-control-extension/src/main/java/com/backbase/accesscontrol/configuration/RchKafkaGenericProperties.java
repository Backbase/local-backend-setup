package com.backbase.accesscontrol.configuration;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("rch.kafka")
public class RchKafkaGenericProperties {

    @NotEmpty
    private String bootstrapServer;

    @NotEmpty
    private String groupId;

    @NotEmpty
    private String upsertDataGroupErrorTopicName;

    @NotEmpty
    private String upsertLegalEntitiesErrorTopicName;

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
