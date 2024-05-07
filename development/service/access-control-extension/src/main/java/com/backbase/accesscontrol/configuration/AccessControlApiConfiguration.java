package com.backbase.accesscontrol.configuration;

import static com.backbase.buildingblocks.communication.http.HttpCommunicationConfiguration.INTERCEPTORS_ENABLED_HEADER;

import com.backbase.buildingblocks.communication.http.HttpCommunicationConfiguration;
import com.backbase.dbs.accesscontrol.api.client.ApiClient;
import com.backbase.dbs.accesscontrol.api.client.v3.LegalEntitiesApi;
import com.backbase.dbs.accesscontrol.api.client.v3.ServiceAgreementsApi;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

@Setter
@Validated
@Configuration
@ComponentScan(basePackages = {"com.backbase.dbs.accesscontrol.api.client"},
    excludeFilters = {
        @Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
        @Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class}),
        @Filter(type = FilterType.REGEX, pattern = "com.backbase.dbs.accesscontrol.api.client.*.ApiClient"),
        @Filter(type = FilterType.REGEX, pattern = "com.backbase.dbs.accesscontrol.api.client.*.*Api")})
@ConfigurationProperties("backbase.communication.services.access-control")
public class AccessControlApiConfiguration {

    private static final String ACCESS_CONTROL = "access-control";

    @Value("${backbase.communication.http.default-scheme:http}")
    @Pattern(regexp = "https?")
    private String scheme;

    @Bean("ServiceAgreementsApiV3")
    ServiceAgreementsApi createGeneratedServiceAgreementsApiClientV3(
        @Qualifier(HttpCommunicationConfiguration.INTER_SERVICE_REST_TEMPLATE_BEAN_NAME) RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(scheme + "://" + ACCESS_CONTROL);
        apiClient.addDefaultHeader(INTERCEPTORS_ENABLED_HEADER, Boolean.TRUE.toString());
        return new ServiceAgreementsApi(apiClient);
    }

    @Bean
    LegalEntitiesApi legalEntitiesApiClientV3(
        @Qualifier(HttpCommunicationConfiguration.INTER_SERVICE_REST_TEMPLATE_BEAN_NAME) RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(scheme + "://" + ACCESS_CONTROL);
        apiClient.addDefaultHeader(INTERCEPTORS_ENABLED_HEADER, Boolean.TRUE.toString());
        return new LegalEntitiesApi(apiClient);
    }
}
