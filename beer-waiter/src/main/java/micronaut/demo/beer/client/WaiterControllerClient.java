package micronaut.demo.beer.client;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Single;
import micronaut.demo.beer.Beer;
import micronaut.demo.beer.BeerSize;
import micronaut.demo.beer.CustomerBill;

import javax.validation.constraints.NotBlank;

@Client("/waiter")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface WaiterControllerClient {

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    Single<Beer> serveBeerToCustomer(@JsonProperty("customerName")  String customerName, @JsonProperty("beerName")  String beerName, @JsonProperty("beerType")  String beerType, @JsonProperty("amount")  String amount, @JsonProperty("price")  String price);

    @Get("/status")
    HttpResponse status();


    @Get("/bill/{customerName}")
    Single<CustomerBill> bill(@NotBlank String customerName);
}
