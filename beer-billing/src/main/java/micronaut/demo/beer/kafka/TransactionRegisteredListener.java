package micronaut.demo.beer.kafka;

import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import lombok.RequiredArgsConstructor;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;
import micronaut.demo.beer.service.BillService;

import java.util.Optional;

@RequiredArgsConstructor
@KafkaListener(offsetReset = OffsetReset.EARLIEST)///,groupId="billing", threads=10)
public class TransactionRegisteredListener {

    final BillService billService;


    public TransactionRegisteredListener(BillService billService) {
        this.billService = billService;
    }

    @Topic("beer-registered")
    void  beerRegisteredEvent(@KafkaKey String username, BeerItem beer) {
        System.out.println(username+"---------------------------WE GOT TICKET beer-registered \n\n\n\n\n");
        Optional<Ticket> t = getTicketForUser(username);
        Ticket ticket = t.isPresent() ?  t.get() : new Ticket();
        ticket.add(beer);
        System.out.println(username+"---------------------------WE GOT TICKET "+ticket+" billing \n\n\n\n\n");
        billService.createBillForCostumer(username, ticket);
    }


    @ContinueSpan
    private Optional<Ticket> getTicketForUser(@SpanTag("getTicketForUser") String customerName) {
        return Optional.ofNullable(billService.getBillForCostumer(customerName));
    }

}
