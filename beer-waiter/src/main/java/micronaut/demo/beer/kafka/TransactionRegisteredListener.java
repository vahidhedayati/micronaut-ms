package micronaut.demo.beer.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import micronaut.demo.beer.client.TicketControllerClient;
import micronaut.demo.beer.client.WaiterControllerClient;
import micronaut.demo.beer.model.BeerItem;

/**
 *
 * When the waiter application was down WaiterFallBack2 streamed the event to kafka
 *
 * Now that this bill-waiter app is starting up - it will listen for any new topics
 *
 * and action them
 */


@RequiredArgsConstructor
@KafkaListener(offsetReset = OffsetReset.EARLIEST)///,groupId="billing", threads=10)
public class TransactionRegisteredListener {

    final WaiterControllerClient waiterControllerClient;
    final TicketControllerClient ticketControllerClient;


    public TransactionRegisteredListener(WaiterControllerClient waiterControllerClient,TicketControllerClient ticketControllerClient) {
        this.waiterControllerClient=waiterControllerClient;
        this.ticketControllerClient=ticketControllerClient;
    }


    @Topic("customer-beer")
    void beerRegisteredEvent(@KafkaKey String customerName, BeerItem beerItem) {
        System.out.println("Kafa stream receieved adding a customer beer"+customerName+" to stock controller - so stock must be up otherwise it will go to tab app");
        ticketControllerClient.addBeerToCustomerBill(beerItem, customerName);
    }


    @Topic("customer-bill")
    void billCustomer(@KafkaKey String customerName) {
        System.out.println("Kafa stream receieved billing customer "+customerName);
        waiterControllerClient.bill(customerName);
    }

}
