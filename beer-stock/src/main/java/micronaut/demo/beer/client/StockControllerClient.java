package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerStock;

import javax.validation.constraints.NotBlank;

@Client("/stock")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface StockControllerClient {

    @Get("/status")
    HttpResponse status();

    @Get("/")
    Single list();

    @Get("/lookup/{name}")
    Maybe<BeerStock> find(@NotBlank String name);

    @Post(uri = "/pints", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> pints(@Body("name") String name, @Body("amount") String amount);

    @Post(uri = "/halfPints", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> halfPints(@Body("name") String name, @Body("amount") String amount);

    @Post(uri = "/bottles", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> bottles(@Body("name") String name, @Body("amount") String amount);
}