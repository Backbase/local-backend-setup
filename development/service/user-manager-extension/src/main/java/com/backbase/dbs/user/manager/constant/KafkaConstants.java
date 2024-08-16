package com.backbase.dbs.user.manager.constant;

public final class KafkaConstants {

    public static final String ERROR_CODE_HEADER = "error-code";
    public static final String ERROR_MESSAGE_HEADER = "error-message";
    public static final String ERROR_STACKTRACE_HEADER = "error-stacktrace";
    public static final int KAFKA_RETRY_PAUSE_COUNT = 1;

    private KafkaConstants() {
    }
}
