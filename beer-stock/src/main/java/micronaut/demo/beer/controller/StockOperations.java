package micronaut.demo.beer.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface StockOperations<T extends BeerStock> {

    @Get("/")
    Single list();
    //List<StockEntity> list();

    @Get("/lookup/{name}")
    Maybe<T> find(String name);

    @Post("/")
    Single<T> save(@Valid @Body T beer);
}
