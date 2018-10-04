package micronaut.demo.beer.client;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerMarkup;

/**
 * This is the fall back controller for no markup i.e. if the beer-billing application is down this file is called
 * and will return some default markup values for beer bottles and pints
 */
@Client(id = "billing", path = "/markup")
@Fallback
public class MarkupClientFallBack implements MarkupControllerClient {

    @Override
    public Single<BeerMarkup> baseCosts() {
      return Single.just(new  BeerMarkup("fallback",8.16,8.16));
    }
}
