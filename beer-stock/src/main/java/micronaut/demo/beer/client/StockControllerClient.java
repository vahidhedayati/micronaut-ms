package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Client("/stock")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface StockControllerClient {


    @Get("/")
    public Single<List<StockEntity>>list();

    @Get("/lookup/{name}")
    public Maybe<BeerStock> find(@NotBlank String name);


    @Get("/pints/{name}/{amount}")
    public Single<BeerStock> pints(@NotBlank String name, @NotBlank String amount);

    @Get("/halfPints/{name}/{amount}")
    public Single<BeerStock> halfPints(@NotBlank String name, @NotBlank String amount);


    @Get("/bottles/{name}/{amount}")
    public Single<BeerStock> bottles(@NotBlank String name, @NotBlank String amount);
}
