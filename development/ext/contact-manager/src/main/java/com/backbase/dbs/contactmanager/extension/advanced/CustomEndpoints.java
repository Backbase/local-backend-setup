
package com.backbase.dbs.contactmanager.extension.advanced;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.dbs.contactmanager.contact.dto.Contact;
import com.backbase.dbs.contactmanager.contact.dto.InternalRequestWrapper;
import java.util.List;
import org.apache.camel.Consume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The custom camel Endpoints to be used in the behavior extension.
 */
@Component
public class CustomEndpoints {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomEndpoints.class);
    private static final List<String> HIGH_RISK_COUNTRIES = List.of("NL");
    public static final String VALIDATION_INTERCEPTOR = "direct:contact.validate.interceptor";

    @Consume(VALIDATION_INTERCEPTOR)
    public void executeValidation(InternalRequestWrapper<Contact> internalRequest) {

        LOGGER.info("======== In custom endpoint for validation for user " + internalRequest.getBbId() + " ========");
        Contact contact = internalRequest.getInternalRequest().getData();
        if ("sara".equals(internalRequest.getBbId()) && contact.getCountry() != null && isHighRiskCountry(contact.getCountry())) {
            throw new BadRequestException("User is not allowed to handle a counterparty from a high risk country");
        }
    }

    private boolean isHighRiskCountry(String countryCode) {
        return HIGH_RISK_COUNTRIES.contains(countryCode);
    }
}
