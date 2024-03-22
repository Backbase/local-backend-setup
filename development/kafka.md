# To view list of kafka topics

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