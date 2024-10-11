package com.backbase.accesscontrol.model;

import com.backbase.integration.accessgroup.rest.spec.v3.FunctionGroupItem;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationFunctionGroupUpdate;
import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationIdentifier;
import com.backbase.integration.accessgroup.rest.spec.v3.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionGroupUpsertDTO {

    private Map<String, String> additions = new HashMap<>();
    private Map<String, String> metadata = new HashMap<>();
    private String name;
    private String description;
    private String externalServiceAgreementId;
    private List<Permission> permissions;
    private String validFromDate;
    private String validFromTime;
    private String validUntilDate;
    private String validUntilTime;
    private Long apsId;
    private String apsName;
    private FunctionGroupItem.TypeEnum type;
    private IntegrationFunctionGroupUpdate functionGroup;
    private IntegrationIdentifier identifier;
}