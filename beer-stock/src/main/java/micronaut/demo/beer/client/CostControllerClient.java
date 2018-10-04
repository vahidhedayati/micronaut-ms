package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerCost;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Client("/cost")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface CostControllerClient {


    @Get("/")
    public Single<List<BeerCost>> list();

    @Get("/lookup/{name}")
    public Maybe<BeerCost> lookup(@NotBlank String name);

}
