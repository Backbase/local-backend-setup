package com.backbase.accesscontrol.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableAsync
public class AsyncRetryService {

    private final RetryTemplate retryTemplate;

    public AsyncRetryService(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    @Async
    public void retryAsync(Runnable task) {
        retryTemplate.execute(context -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Retry failed: {}", e.getMessage(), e);
                throw e; // This will trigger the retry mechanism
            }
            return null;
        });
    }
}