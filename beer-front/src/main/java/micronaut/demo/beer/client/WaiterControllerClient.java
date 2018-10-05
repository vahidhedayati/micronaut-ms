package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;


@Client(id = "waiter", path = "/waiter")
//@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface WaiterControllerClient {


    @Get("/status")
    HttpResponse status();

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    Single<Beer> serveBeerToCustomer(@Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price);

    @Get("/bill/{customerName}")
    Single<CustomerBill> bill(@NotBlank String customerName);
}
