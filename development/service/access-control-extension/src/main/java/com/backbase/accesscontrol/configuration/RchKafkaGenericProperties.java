package com.backbase.accesscontrol.configuration;

import jakarta.validation.constraints.NotEmpty;
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
    private String upsertDataGroupErrorTopicName;

    @NotEmpty
    private String upsertLegalEntitiesErrorTopicName;
}
