package micronaut.demo.beer.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

import java.util.Date;



@KafkaClient
public interface EventPublisher {




    @Topic("save-sequence")
    void saveSequence(@KafkaKey String name, String date);


}
