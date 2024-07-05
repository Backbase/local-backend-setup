package com.backbase.accesscontrol.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeJsonDeserializer implements Deserializer<Object> {
    private static final Logger log = LoggerFactory.getLogger(SafeJsonDeserializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            // Deserialize byte[] data to Object
            return objectMapper.readValue(data, Object.class);
        } catch (Exception e) {
            log.error("Failed to deserialize message from topic {}: {}", topic, e.getMessage());
            throw new SerializationException("Error deserializing JSON message", e);
        }
    }
}