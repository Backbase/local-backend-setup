package com.backbase.accesscontrol.eventlistener;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("process")
@Slf4j
public class PutDataGroupsEventListener implements java.util.function.Consumer<KStream<Object, String>> {

    /**
     * Performs this operation on the given argument.
     *
     * @param input the input argument
     */
    @Override
    public void accept(KStream<Object, String> input) {
        input.foreach((key, value) -> log.info("Key: {} Value: {}", key, value));
    }

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this operation followed by the {@code after}
     * operation. If performing either operation throws an exception, it is relayed to the caller of the composed
     * operation. If performing this operation throws an exception, the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this operation followed by the {@code after}
     * operation
     * @throws NullPointerException if {@code after} is null
     */
    @NotNull
    @Override
    public Consumer<KStream<Object, String>> andThen(@NotNull Consumer<? super KStream<Object, String>> after) {
        return Consumer.super.andThen(after);
    }
}