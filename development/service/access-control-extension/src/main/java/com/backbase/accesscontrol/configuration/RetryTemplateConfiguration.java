package com.backbase.accesscontrol.configuration;

import com.backbase.accesscontrol.exception.PayloadParsingException;
import java.util.Collections;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


@Configuration
public class RetryTemplateConfiguration {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        ExceptionClassifierRetryPolicy retryPolicyClassifier = new ExceptionClassifierRetryPolicy();

        // Exceptions to RetryPolicy since if we have a parse exception, there is no need for retrying
        retryPolicyClassifier.setExceptionClassifier((Classifier<Throwable, RetryPolicy>) classifiable -> {
            if (classifiable instanceof PayloadParsingException) {
                return new SimpleRetryPolicy(0, Collections.emptyMap());
            } else {
                return retryPolicy; // Retry for all other exceptions
            }
        });

        // Set retry policy in the retry template
        retryTemplate.setRetryPolicy(retryPolicyClassifier);

        // Configure exponential back-off policy
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(5000); // Initial interval in milliseconds
        backOffPolicy.setMultiplier(2.0); // Multiplier to increase the interval
        backOffPolicy.setMaxInterval(30000); // Maximum interval in milliseconds

        // Set back-off policy in the retry template
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}

