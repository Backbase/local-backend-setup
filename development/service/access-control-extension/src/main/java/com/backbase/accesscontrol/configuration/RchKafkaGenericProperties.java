package com.backbase.accesscontrol.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
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
    private String upsertDataGroupTopicName;

    @NotEmpty
    private String upsertLegalEntityTopicName;

    @NotEmpty
    private String upsertDataGroupErrorTopicName;

    @NotEmpty
    private String upsertLegalEntitiesErrorTopicName;

    @NotNull
    private long upsertDataGroupBackOffDelay;

    @NotNull
    private long upsertLegalEntityBackOffDelay;

    @NotNull
    private long upsertDataGroupRetryAttempts;

    @NotNull
    private long upsertLegalEntityRetryAttempts;

    @NotEmpty
    private String defaultDlqTopicName;

    @NotNull
    private long defaultBackOffDelay;

    @NotNull
    private long defaultRetryAttempts;

}