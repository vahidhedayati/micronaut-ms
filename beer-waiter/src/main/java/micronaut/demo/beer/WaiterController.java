package micronaut.demo.beer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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

    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.ok();
    }

    /*
    Single<MyViewModel> source =
    Single.zip(source1, source2, source3, MyViewModel::new);
     */

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    //@NewSpan
    public Single<Beer> serveBeerToCustomer(@JsonProperty("customerName")  String customerName, @JsonProperty("beerName")  String beerName, @JsonProperty("beerType")  String beerType, @JsonProperty("amount")  String amount, @JsonProperty("price")  String price) {
        System.out.println("Waiter controller serving a beer"+customerName+" >>> bt"+beerType);
        Beer beer = new Beer(beerName,BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
        BeerItem beerItem = new BeerItem(beerName,BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
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
