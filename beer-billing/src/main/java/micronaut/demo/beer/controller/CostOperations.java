package micronaut.demo.beer.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerCost;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface CostOperations<T extends BeerCost> {


    @Get("/")
    Maybe<T> baseCosts();

    @Get("/lookup/{name}")
    Maybe<T> find(Double field);

    @Post("/")
    Single<T> save(@Valid @Body T beer);
}