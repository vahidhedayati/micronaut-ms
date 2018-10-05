package micronaut.demo.beer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerStock;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
public interface StockOperations<T extends BeerStock> {

    @Get("/")
    Single list();
    //List<StockEntity> list();

    @Get("/status")
    HttpResponse status();

    @Get("/lookup/{name}")
    Maybe<T> find(String name);

    @Get("/pints/{name}/{amount}")
    Single<Maybe> pints(@NotBlank String name, @NotBlank String amount);

    @Get("/halfPints/{name}/{amount}")
    Single<Maybe> halfPints(@NotBlank String name, @NotBlank String amount);

    @Get("/bottles/{name}/{amount}")
    Single<Maybe> bottles(@NotBlank String name, @NotBlank String amount);

    @Post("/")
    Single<T> save(@Valid @Body T beer);
}
