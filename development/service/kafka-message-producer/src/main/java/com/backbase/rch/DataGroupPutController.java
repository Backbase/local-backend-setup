package com.backbase.rch;

import com.backbase.accesscontrol.event.event.spec.v1.PutDataGroupsEvent;
import com.backbase.buildingblocks.backend.communication.event.EnvelopedEvent;
import com.backbase.buildingblocks.backend.communication.event.proxy.EventBus;
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
public class DataGroupPutController {

    private final EventBus eventBus;

    @PostMapping(path = "")
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/data-group",
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    public void updateDataGroups(@Valid @RequestBody PutDataGroupsEvent putDataGroupsEvent) {

        EnvelopedEvent<PutDataGroupsEvent> eventWrapper = new EnvelopedEvent<>();
        eventWrapper.setEvent(putDataGroupsEvent);
        log.info("Emitting a putDataGroupsEvent {}", putDataGroupsEvent);
        eventBus.emitEvent(eventWrapper);

    }

}
