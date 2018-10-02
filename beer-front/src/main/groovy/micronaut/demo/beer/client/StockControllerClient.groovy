package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.Client;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerStock;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Client(id = "stock", path = "/stock")
public interface StockControllerClient {


    @Get("/")
    public Single list();

    @Get("/lookup/{name}")
    public Maybe<BeerStock> find(@NotBlank String name);


    @Get("/pints/{name}/{amount}")
    public Single<BeerStock> pints(@NotBlank String customerName, @NotBlank String amount);

    @Get("/halfPints/{name}/{amount}")
    public Single<BeerStock> halfPints(@NotBlank String customerName, @NotBlank String amount);


    @Get("/bottles/{name}/{amount}")
    public Single<BeerStock> bottles(@NotBlank String customerName, @NotBlank String amount);
}
