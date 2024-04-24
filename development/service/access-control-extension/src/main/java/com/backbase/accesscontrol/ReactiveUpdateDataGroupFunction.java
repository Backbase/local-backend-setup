package com.backbase.accesscontrol;

import com.backbase.accesscontrol.domain.dto.PresentationDataGroupUpdateDto;
import com.backbase.accesscontrol.mapper.PutDataGroupsEventMapper;
import com.backbase.accesscontrol.service.facades.v3.DataGroupServiceFacade;
import com.backbase.accesscontrol.service.rest.spec.v3.model.PresentationDataGroupUpdate;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component("reactiveUpdateDataGroup")
@AllArgsConstructor
@Slf4j
public class ReactiveUpdateDataGroupFunction implements
    Function<Flux<PresentationDataGroupUpdate>, Flux<PresentationDataGroupUpdate>> {

    @Value("${waiting:10000}")
    private Long waitingTime;
    private final DataGroupServiceFacade dataGroupServiceFacade;
    private final PutDataGroupsEventMapper putDataGroupsEventMapper;

    /**
     * Applies this function to the given argument.
     *
     * @param inputs the function argument
     * @return the function result
     */
    @Override
    public Flux<PresentationDataGroupUpdate> apply(Flux<PresentationDataGroupUpdate> inputs) {

        return inputs.doOnNext(this::process);
    }

    private void process(PresentationDataGroupUpdate presentationDataGroupUpdate) {
        log.info("Reactive Message received: {}", presentationDataGroupUpdate);
        PresentationDataGroupUpdateDto dto = putDataGroupsEventMapper.map(presentationDataGroupUpdate);
        updateDataGroupWithDelay(dto);
    }

    private void updateDataGroupWithDelay(PresentationDataGroupUpdateDto dto) {
        try {
            Thread.sleep(waitingTime);
            dataGroupServiceFacade.updateDataGroup(dto);
            log.info("Reactive Event process successfully");
        } catch (InterruptedException e) {
            log.error("Error occurred while waiting for thread", e);
            Thread.currentThread().interrupt();
        }
    }
}
