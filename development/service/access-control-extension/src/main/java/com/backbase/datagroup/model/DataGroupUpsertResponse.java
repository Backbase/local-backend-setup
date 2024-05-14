package com.backbase.datagroup.model;

import com.backbase.integration.accessgroup.rest.spec.v3.IntegrationDataGroupItemBatchPutRequestBody;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataGroupUpsertResponse extends IntegrationDataGroupItemBatchPutRequestBody {

    @JsonProperty("externalServiceAgreementId")
    private String externalServiceAgreementId;

}
