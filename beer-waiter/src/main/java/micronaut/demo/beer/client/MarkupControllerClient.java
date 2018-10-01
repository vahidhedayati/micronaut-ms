package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.Client;
import io.reactivex.Maybe;
import micronaut.demo.beer.model.BeerMarkup;

@Client(id = "billing", path = "/markup")
public interface MarkupControllerClient {

    @Get("/")
    Maybe<BeerMarkup> baseCosts();



}


