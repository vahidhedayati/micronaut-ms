package micronaut.demo.beer.client;
import io.micronaut.http.annotation.Get;

import io.micronaut.http.client.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.Beer;

import javax.validation.constraints.NotBlank;

@Client("/stock")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface StockControllerClient {

    @Get("/lookup/{name}")
    public Maybe<Beer> find(@NotBlank String name);


    @Get("/pints/{name}/{amount}")
    public Single<Beer> pints(@NotBlank String customerName,@NotBlank String amount);

    @Get("/halfPints/{name}/{amount}")
    public Single<Beer> halfPints(@NotBlank String customerName,@NotBlank String amount);


    @Get("/bottles/{name}/{amount}")
    public Single<Beer> bottles(@NotBlank String customerName,@NotBlank String amount);
}
