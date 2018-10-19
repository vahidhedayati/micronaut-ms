package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Single;
import micronaut.demo.beer.kafka.EventPublisher;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.CustomerBill;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

/**
 * This is our fall back for WaiterControllerClient2
 *
 * This fall back attempts to actually make site continue to work - the user may not appreciate output on screen
 * but their transactions are still being captured via kafka
 *
 * and once the actual system comes back up their beers will be charged / deducted etc etc
 *
 */
@Client(id = "waiter", path = "/waiter")
@Fallback
public class WaiterClientFallBack2 implements WaiterControllerClient2 {


    private final EventPublisher eventPublisher;

    @Inject
    public WaiterClientFallBack2(EventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }

    @Override
    public HttpResponse status() {
        return HttpResponse.serverError();
    }

    /**
     * All below methods attempt to use kafka to stream the end users request
     * since it appears the primary point that the gateway needs to process all of this is down.
     */
    @Override
    public Single<Beer> serveBeerToCustomer(String customerName,
                                          String beerName,
                                            String beerType,
                                           String amount,
                                           String price) {
        System.out.println("Fall back attempting to publish kafka event"+customerName+" --"+beerName);
        BeerItem beerItem = new BeerItem(beerName,micronaut.demo.beer.BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
        eventPublisher.beerRegisteredEvent(customerName,beerItem);

        return Single.just(new Beer());
    }


    /**
     *
     * We don't need to speak to tab - it goes directly to waiter when waiter is up to process
     * waiter has some logic if billing is down to direct to tab that directs to billing
     */
    //This is a more intelligent fall back simply routing the actual tab request directly into kafka knowing it could not reach waiter.
    /*@Post(uri = "/tab", consumes = MediaType.APPLICATION_JSON)
    public Single<Beer> tabBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
        BeerItem beerItem = new BeerItem(beerName,micronaut.demo.beer.BeerSize.valueOf(beerType),Integer.valueOf(amount),Double.valueOf(price));
        eventPublisher.tabRegisteredEvent(customerName,beerItem);
        return Single.just(new Beer());
    }
    */

    @Override
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        //We don't actually want to poll a billing request to a kafka stream
       // eventPublisher.billCustomer(customerName);
        return Single.just(new CustomerBill());
    }
}
