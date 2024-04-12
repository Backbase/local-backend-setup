# Kafka command for debugging purpose

1. Docker command to access Kafka container's interactive shell.
    ```shell
    docker exec -it broker /bin/bash
    ```
   
2. Check Kafka version
   ```shell
   kafka-run-class kafka.Kafka --version
   ```

3. List Kafka topics
    ```shell
    kafka-topics --list --bootstrap-server=localhost:9092
    ```
   
4. To view messages on Kafka topics e.g. `LegalEntityCompletedEvent`
   ```shell
   export TOPIC=com.backbase.stream.compositions.events.ingress.event.spec.v1.LegalEntityBatchPushEvent
   kafka-console-consumer --bootstrap-server localhost:9092 --topic ${TOPIC} --from-beginning
   ```
