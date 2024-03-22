package com.backbase.rch;

import com.backbase.buildingblocks.backend.communication.event.EnvelopedEvent;
import com.backbase.buildingblocks.backend.communication.event.proxy.EventBus;
import com.backbase.stream.compositions.events.ingress.event.spec.v1.LegalEntityBatchPushEvent;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class LegalEntityBatchUpsertController {

    private final EventBus eventBus;

    @PostMapping(path = "")
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/legal-entity/batch/upsert",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public void batchUpsert(@Valid @RequestBody LegalEntityBatchPushEvent legalEntityBatchPushEvent) {

        /*List<LegalEntityPushEvent> realEvent = new ArrayList<>();
        LegalEntityPushEvent event = new LegalEntityPushEvent()
            .withLegalEntity(new LegalEntity()
                .withExternalId("le12")
                .withName("Test Name")
                .withType(Type.CUSTOMER)
                .withCustomerCategory(CustomerCategory.RETAIL)
                .withParentExternalId("moustache-bank-usa")
                .withActivateSingleServiceAgreement(true));

        realEvent.add(event);*/

        EnvelopedEvent<LegalEntityBatchPushEvent> eventWrapper = new EnvelopedEvent<>();
        eventWrapper.setEvent(legalEntityBatchPushEvent);
        log.info("Emitting a LegalEntityPushEvent {}", legalEntityBatchPushEvent);
        eventBus.emitEvent(eventWrapper);

    }

}
