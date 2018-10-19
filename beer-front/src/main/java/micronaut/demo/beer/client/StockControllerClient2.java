package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerStock;

import javax.validation.constraints.NotBlank;

@Client(id = "stock", path = "/stock")
public interface StockControllerClient2 {

    @Get("/status")
    HttpResponse status();

    @Get("/")
    Single list();

    @Get("/lookup/{name}")
    Maybe<BeerStock> find(@NotBlank String name);

    @Post(uri = "/pints", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> pints( String name,String amount);

    @Post(uri = "/halfPints", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> halfPints(String name,String amount);

    @Post(uri = "/bottles", consumes = MediaType.APPLICATION_JSON)
    Single<BeerStock> bottles(String name,String amount);
}
