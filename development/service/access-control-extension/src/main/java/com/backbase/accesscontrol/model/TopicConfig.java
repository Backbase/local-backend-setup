package com.backbase.accesscontrol.model;

import org.springframework.util.backoff.BackOff;

public class TopicConfig {
    private final String dlqTopic;
    private final BackOff backOff;

    public TopicConfig(String dlqTopic, BackOff backOff) {
        this.dlqTopic = dlqTopic;
        this.backOff = backOff;
    }

    public String getDlqTopic() {
        return dlqTopic;
    }

    public BackOff getBackOff() {
        return backOff;
    }
}