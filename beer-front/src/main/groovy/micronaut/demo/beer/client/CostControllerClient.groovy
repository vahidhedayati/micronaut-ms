package micronaut.demo.beer.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.Client
import io.reactivex.Maybe
import io.reactivex.Single
import micronaut.demo.beer.model.BeerCost

import javax.validation.constraints.NotBlank

@Client(id = "stock", path = "/cost")
interface CostControllerClient {

    @Get("/")
    public Single<List<BeerCost>> list();

    @Get("/lookup/{name}")
    public Maybe<BeerCost> lookup(@NotBlank String name);

}
