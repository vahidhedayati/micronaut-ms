package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;
import micronaut.demo.beer.model.Markup;

@Client(id = "billing", path = "/markup")
public interface MarkupControllerClient {

    @Get("/")
    Single<Markup> baseCosts();



}


