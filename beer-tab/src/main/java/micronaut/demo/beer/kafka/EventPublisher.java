package micronaut.demo.beer.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import micronaut.demo.beer.model.BeerItem;

@KafkaClient
public interface EventPublisher {


    @Topic("beer-registered")
    void  tabRegisteredEvent(@KafkaKey String username, BeerItem beer);


}
