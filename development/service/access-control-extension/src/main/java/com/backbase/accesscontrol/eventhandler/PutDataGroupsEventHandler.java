package com.backbase.accesscontrol.eventhandler;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.event.event.spec.v1.PutDataGroupsEvent;
import com.backbase.accesscontrol.event.event.spec.v1.PutDataGroupsSuccessEvent;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.buildingblocks.backend.communication.event.EnvelopedEvent;
import com.backbase.buildingblocks.backend.communication.event.handler.EventHandler;
import com.backbase.buildingblocks.backend.communication.event.proxy.EventBus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class PutDataGroupsEventHandler implements EventHandler<PutDataGroupsEvent> {

    @Value("${waiting:10000}")
    private Long waitingTime;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper putDataGroupsEventMapper;
    private final EventBus eventBus;

    @Override
    public void handle(EnvelopedEvent<PutDataGroupsEvent> envelopedEvent) {
        log.info("Received Event {}", envelopedEvent);

        PresentationDataGroupUpdateDto dto = mapEventToDto(envelopedEvent);
        updateDataGroupWithDelay(dto);

        PutDataGroupsSuccessEvent successEvent = mapDtoToSuccessEvent(dto);
        emitSuccessEvent(successEvent);
    }

    private PresentationDataGroupUpdateDto mapEventToDto(EnvelopedEvent<PutDataGroupsEvent> envelopedEvent) {
        return putDataGroupsEventMapper.map(envelopedEvent.getEvent());
    }

    private void updateDataGroupWithDelay(PresentationDataGroupUpdateDto dto) {
        try {
            Thread.sleep(waitingTime);
            dataGroupServiceFacade.updateDataGroup(dto);
        } catch (InterruptedException e) {
            log.error("Error occurred while waiting for thread", e);
            Thread.currentThread().interrupt();
        }
    }

    private PutDataGroupsSuccessEvent mapDtoToSuccessEvent(PresentationDataGroupUpdateDto dto) {
        return putDataGroupsEventMapper.map(dto);
    }

    private void emitSuccessEvent(PutDataGroupsSuccessEvent successEvent) {
        EnvelopedEvent<PutDataGroupsSuccessEvent> eventWrapper = new EnvelopedEvent<>();
        eventWrapper.setEvent(successEvent);
        eventBus.emitEvent(eventWrapper);
    }
}
