package micronaut.demo.beer.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.Beer;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface StockOperations<T extends Beer> {

    @Get("/")
    Single<List<T>> list();

    @Get("/search/{name}")
    Single<List<T>> search(String name);

    @Get("/lookup/{name}")
    Maybe<T> find(String name);

    @Post("/")
    Single<T> save(@Valid @Body T beer);
}
