package micronaut.demo.beer.client;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Single;
import micronaut.demo.beer.Beer;
import micronaut.demo.beer.BeerSize;
import micronaut.demo.beer.model.CustomerBill;


import javax.validation.constraints.NotBlank;


@Client(id = "waiter", path = "/waiter")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface WaiterControllerClient {

    @Get("/beer/{customerName}/{beerName}/{beerType}/{amount}")
    Single<Beer> serveBeerToCustomer(@NotBlank String customerName, @NotBlank String beerName, @NotBlank BeerSize beerType, @NotBlank int amount);
    
    @Get("/bill/{customerName}")
    Single<CustomerBill> bill(@NotBlank String customerName);
}
