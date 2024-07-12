package com.backbase.accesscontrol.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    protected RchKafkaGenericProperties rchKafkaGenericProperties;

    @Bean
    public AdminClient adminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, rchKafkaGenericProperties.getBootstrapServer());
        return AdminClient.create(props);
    }
}
