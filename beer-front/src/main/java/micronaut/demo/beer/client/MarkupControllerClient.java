package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerMarkup;

@Client(id = "billing", path = "/markup")
//@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface MarkupControllerClient {

    @Get("/status")
    HttpResponse status();

    @Get("/")
    Single<BeerMarkup> baseCosts();

}
