package com.backbase.accesscontrol;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationDataGroupUpdate;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("updateDataGroup")
@AllArgsConstructor
@Slf4j
public class UpdateDataGroupFunction implements
    Function<Message<PresentationDataGroupUpdate>, PresentationDataGroupUpdate> {

    @Value("${waiting:10000}")
    private Long waitingTime;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper putDataGroupsEventMapper;

    /**
     * Applies this function to the given argument.
     *
     * @param message the function argument
     * @return the function result
     */
    @Override
    public PresentationDataGroupUpdate apply(Message<PresentationDataGroupUpdate> message) {
        log.info("Message received: {}", message);
        Acknowledgment acknowledgment = message.getHeaders()
            .get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
        PresentationDataGroupUpdateDto dto = mapEventToDto(message);
        updateDataGroupWithDelay(dto);

        log.info("Event process successfully");

        if (acknowledgment != null) {
            System.out.println("Acknowledgment provided");
            acknowledgment.acknowledge();
        }

        return message.getPayload();
    }

    private PresentationDataGroupUpdateDto mapEventToDto(Message<PresentationDataGroupUpdate> message) {
        return putDataGroupsEventMapper.map(message.getPayload());
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
}
