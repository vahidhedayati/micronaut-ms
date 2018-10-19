package micronaut.demo.beer.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import micronaut.demo.beer.model.BeerItem;


/**
 * This is used by StockFallBack2 And WaiterClientFallBack2 java files
 *
 * It mimicks what was about to happen by sending it to a kafka stream where
 * it will sit until actual application is backup - when
 * either app is started it will talk to kafka and process topics
 */

@KafkaClient
public interface EventPublisher {




    //This interacts with beer-waiter app - beer-waiter listens in for customer-beer topic
    @Topic("customer-beer")
    void beerRegisteredEvent(@KafkaKey String username, BeerItem beerName);

    //This interacts with beer-waiter app - beer-waiter listens in for customer-bill topic
    @Topic("customer-bill")
    void billCustomer(@KafkaKey String customerName);



    //This interacts with beer-stock app - beer-stock listens in for increment-pint topic
    @Topic("increment-pint")
    void incrementPint(@KafkaKey String beerName, String amount);

    //This interacts with beer-stock app - beer-stock listens in for increment-halfpint topic
    @Topic("increment-halfpint")
    void incrementHalfPint(@KafkaKey String beerName, String amount);

    //This interacts with beer-stock app - beer-stock listens in for increment-bottle topic
    @Topic("increment-bottle")
    void incrementBottle(@KafkaKey String beerName, String amount);

}
