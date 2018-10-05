package micronaut.demo.beer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.reactivex.Single;
import micronaut.demo.beer.client.MarkupControllerClient;
import micronaut.demo.beer.client.StockControllerClient;
import micronaut.demo.beer.client.WaiterControllerClient;
import micronaut.demo.beer.enums.BeerSize;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Collections;

@Controller("/")
public class GatewayController {


    private final StockControllerClient stockControllerClient;
    private final WaiterControllerClient waiterControllerClient;
    private final MarkupControllerClient markupControllerClient;

    GatewayController(StockControllerClient stockControllerClient,
                      WaiterControllerClient waiterControllerClient,
                      MarkupControllerClient markupControllerClient) {
        this.stockControllerClient = stockControllerClient;
        this.waiterControllerClient=waiterControllerClient;
        this.markupControllerClient=markupControllerClient;
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

    @Get("/billingStatus")
    public HttpResponse status() {
        return markupControllerClient.status();
    }

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    Single<Beer> serveBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
            System.out.println("Serving "+beerName+" "+price);
        return waiterControllerClient.serveBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0, 0.00));
    }

    @Get("/bill/{customerName}")
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        return waiterControllerClient.bill(customerName)
                .onErrorReturnItem(new CustomerBill());
    }


}
