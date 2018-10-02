package micronaut.demo.beer.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerMarkup;

import javax.validation.Valid;

@Validated
public interface MarkupOperations<T extends BeerMarkup> {


    @Get("/")
    Single<T> baseCosts();


    @Post("/")
    Single<T> save(@Valid @Body T beer);
}
