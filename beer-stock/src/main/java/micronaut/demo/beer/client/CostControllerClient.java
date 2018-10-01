package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerCost;

import javax.validation.constraints.NotBlank;

@Client("/cost")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface CostControllerClient {

    @Get("/lookup/{name}")
    public Single<BeerCost> lookup(@NotBlank String name);

}
