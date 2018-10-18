package micronaut.demo.beer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.reactivex.Single;
import micronaut.demo.beer.client.MarkupControllerClient;
import micronaut.demo.beer.client.StockControllerClient;
import micronaut.demo.beer.client.TabControllerClient;
import micronaut.demo.beer.client.WaiterControllerClient;
import micronaut.demo.beer.enums.BeerSize;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.BeerStock;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.Collections;
import java.util.Date;

@Controller("/")
public class GatewayController {


    private final StockControllerClient stockControllerClient;
    private final WaiterControllerClient waiterControllerClient;
    private final MarkupControllerClient markupControllerClient;
    private final TabControllerClient tabControllerClient;

    GatewayController(StockControllerClient stockControllerClient,
                      WaiterControllerClient waiterControllerClient,
                      MarkupControllerClient markupControllerClient,
                      TabControllerClient tabControllerClient) {
        this.stockControllerClient = stockControllerClient;
        this.waiterControllerClient=waiterControllerClient;
        this.markupControllerClient=markupControllerClient;
        this.tabControllerClient=tabControllerClient;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = "/")
    @ContinueSpan
    public HttpResponse index() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }


   @Get("/stock")
   @ContinueSpan
   public Single stock() {
        return stockControllerClient.list().onErrorReturnItem(Collections.emptyList());
   }

    @Get("/billingStatus")
    @ContinueSpan
    public HttpResponse billingStatus() {
        return markupControllerClient.status();
    }

    @Get("/waiterStatus")
    @ContinueSpan
    public HttpResponse waiterStatus() {
        return waiterControllerClient.status();
    }

    @Get("/stockStatus")
    @ContinueSpan
    public HttpResponse stockStatus() {
        return stockControllerClient.status();
    }

    @Get("/tabStatus")
    @ContinueSpan
    public HttpResponse tabStatus() {
        return tabControllerClient.status();
    }


    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<Beer> serveBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
            System.out.println("Serving "+beerName+" "+price);
        return waiterControllerClient.serveBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0, 0.00));
    }


    /**
     * This calls waiter app to call the tab app -
     *
     * Gateway itself knowing tab app is up and billing is down from the health checks
     * @param customerName
     * @param beerName
     * @param beerType
     * @param amount
     * @param price
     * @return
     */
    @Post(uri = "/tab", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<Beer> tabBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
        System.out.println("Tab beer: "+beerName+" "+price);
        return waiterControllerClient.tabBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0, 0.00));
    }


    @Post(uri = "/pints", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> addPints(@Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("addPints "+name+" "+amount);
        return stockControllerClient.pints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/halfPints", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> halfPints(@Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("halfPints "+name+" "+amount);
        return stockControllerClient.halfPints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/bottles", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> bottles(@Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("bottles "+name+" "+amount);
        return stockControllerClient.bottles(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Get("/bill/{customerName}")
    @ContinueSpan
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        System.out.println("Getting bill for "+customerName+" "+new Date());
        return waiterControllerClient.bill(customerName)
                .onErrorReturnItem(new CustomerBill());
    }


}
