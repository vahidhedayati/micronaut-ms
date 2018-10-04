package micronaut.demo.beer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.reactivex.Single;
import micronaut.demo.beer.client.StockControllerClient;
import micronaut.demo.beer.client.WaiterControllerClient;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Collections;

@Controller("/")
public class GatewayController {


    private final StockControllerClient stockControllerClient;
    private final WaiterControllerClient waiterControllerClient;


    GatewayController(StockControllerClient stockControllerClient,WaiterControllerClient waiterControllerClient) {
        this.stockControllerClient = stockControllerClient;
        this.waiterControllerClient=waiterControllerClient;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = "/")
    public HttpResponse index() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }


   @Get("/stock")
   public Single stock() {
        return stockControllerClient.list().onErrorReturnItem(Collections.emptyList());
   }


    @Get("/beer/{customerName}/{beerName}/{beerType}/{amount}")
    public Single<Beer> serveBeerToCustomer(@NotBlank String customerName, @NotBlank String beerName, @NotBlank BeerSize beerType, @NotBlank int amount) {
        return waiterControllerClient.serveBeerToCustomer(customerName,beerName,beerType,amount)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0));
    }

    @Get("/bill/{customerName}")
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        return waiterControllerClient.bill(customerName)
                .onErrorReturnItem(new CustomerBill());
    }


}
