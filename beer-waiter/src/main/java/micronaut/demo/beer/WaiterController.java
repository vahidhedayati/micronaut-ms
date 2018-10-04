package micronaut.demo.beer;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import micronaut.demo.beer.client.TicketControllerClient;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

@Controller("/waiter")
@Validated
public class WaiterController {

    TicketControllerClient ticketControllerClient;

    final EmbeddedServer embeddedServer;

    @Inject
    public WaiterController(TicketControllerClient ticketControllerClient,EmbeddedServer embeddedServer) {
        this.ticketControllerClient = ticketControllerClient;
        this.embeddedServer=embeddedServer;
    }

    /*
    Single<MyViewModel> source =
    Single.zip(source1, source2, source3, MyViewModel::new);
     */

    @Get("/beer/{customerName}/{beerName}/{beerType}/{amount}")
    //@NewSpan
    public Single<Beer> serveBeerToCustomer(@NotBlank String customerName,@NotBlank String beerName,@NotBlank BeerSize beerType,@NotBlank int amount) {
        Beer beer = new Beer(beerName,beerType,amount);
        BeerItem beerItem = new BeerItem(beerName,beerType,amount);
        ticketControllerClient.addBeerToCustomerBill(beerItem, customerName);
        return Single.just(beer);
    }
    
    @Get("/bill/{customerName}")
    //@NewSpan
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        Single<Ticket> singleTicket = ticketControllerClient.bill(customerName);
        Single<Double> singleCost= ticketControllerClient.cost(customerName);
        Ticket ticket= singleTicket.blockingGet();
        CustomerBill bill = new CustomerBill(singleCost.blockingGet().doubleValue());
        bill.setDeskId(ticket.getDeskId());
        return Single.just(bill);
    }
}
