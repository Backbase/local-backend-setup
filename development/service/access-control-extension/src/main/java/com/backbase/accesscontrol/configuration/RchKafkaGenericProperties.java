package com.rbs.coutts.config;

import com.backbase.buildingblocks.context.ContextScoped;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ContextScoped
@ConfigurationProperties("rbs.entitlements.sync")
public class RbsSyncEntitlementsProperties {

    @NotEmpty
    private String messageDestination;
    @NotEmpty
    private String pingIdPropertyName;
    @NotEmpty
    private String buidClaimValue = "RBS.CUK";
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String applicationId;
    @NotEmpty
    private String transferReceiverId;
    @NotEmpty
    private String transferSenderId;

    /**
     * Page size for requesting users via DBS API.
     */
    @Min(value = 1, message = "users-page-size must be greater than zero")
    private int usersPageSize = 100;

    private List<String> adminRealms;
}
