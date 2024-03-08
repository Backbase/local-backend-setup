
package com.backbase.dbs.contactmanager.extension.advanced;

import com.backbase.dbs.contactmanager.contact.route.CreateContactRouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@ConditionalOnProperty(name = "backbase.contact.approval.enabled", havingValue = "false",  matchIfMissing = true)
public class CustomExtendingRouteBuilder extends CreateContactRouteBuilder {

    @Override
    public void configurePreHook(RouteDefinition rd) {
        rd.to(CustomEndpoints.VALIDATION_INTERCEPTOR);
    }

}