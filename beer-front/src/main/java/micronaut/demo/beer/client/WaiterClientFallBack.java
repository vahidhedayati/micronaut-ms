package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Single;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.BeerMarkup;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;

/**
 * This is the fall back controller for no markup i.e. if the beer-billing application is down this file is called
 * and will return some default markup values for beer bottles and pints
 */
@Fallback
public class WaiterClientFallBack implements WaiterControllerClient {


    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.serverError();
    }

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    public Single<Beer> serveBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
        return Single.just(new Beer());
    }

    @Get("/bill/{customerName}")
    public Single<CustomerBill> bill(@NotBlank String customerName) {
        return Single.just(new CustomerBill());
    }
}
