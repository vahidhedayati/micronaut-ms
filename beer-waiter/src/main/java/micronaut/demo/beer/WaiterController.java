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
import micronaut.demo.beer.client.TabControllerClient;
import micronaut.demo.beer.client.TicketControllerClient;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller("/waiter")
@Validated
public class WaiterController {

    TicketControllerClient ticketControllerClient;

    final EmbeddedServer embeddedServer;
    private final TabControllerClient tabControllerClient;

    @Inject
    public WaiterController(TicketControllerClient ticketControllerClient,EmbeddedServer embeddedServer,TabControllerClient tabControllerClient) {
        this.ticketControllerClient = ticketControllerClient;
        this.embeddedServer=embeddedServer;
        this.tabControllerClient=tabControllerClient;
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


    @Post(uri = "/tab", consumes = MediaType.APPLICATION_JSON)
    //@NewSpan
    public Single<Beer> tabBeerToCustomer(@JsonProperty("customerName")  String customerName, @JsonProperty("beerName")  String beerName, @JsonProperty("beerType")  String beerType, @JsonProperty("amount")  String amount, @JsonProperty("price")  String price) {
        System.out.println("Waiter controller tabbing a beer"+customerName+" >>> bt"+beerType);
        Beer beer = new Beer(beerName,BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
        BeerItem beerItem = new BeerItem(beerName,BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
        tabControllerClient.addBeerToCustomerBill(beerItem, customerName);
        return Single.just(beer);
    }
    
    @Get("/bill/{customerName}")
    //@NewSpan
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        Single<Ticket> singleTicket = ticketControllerClient.bill(customerName);
        Single<Double> singleCost= ticketControllerClient.cost(customerName);
        Map<String,CustomerBill> map2=new LinkedHashMap<>();
        return Single.zip(singleTicket, singleCost,(result1,result2)->{
            CustomerBill bill = new CustomerBill(result2.doubleValue());
            bill.setDeskId(result1.getDeskId());
            bill.setWaiterId(embeddedServer.getPort());
            return bill;
        });
        /*
        Ticket ticket= singleTicket.blockingGet();
        CustomerBill bill = new CustomerBill(singleCost.blockingGet().doubleValue());
        bill.setDeskId(ticket.getDeskId());
        bill.setWaiterId(embeddedServer.getPort());
        return Single.just(bill);
        */
    }
}
