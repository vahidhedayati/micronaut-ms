package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.validation.constraints.NotBlank;
@Client("/billing")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface TicketControllerClient {

    @Get("/reset/{customerName}")
    HttpResponse resetCustomerBill(@NotBlank String customerName);

    @Post("/addBeer/{customerName}")
    HttpResponse<BeerItem> addBeerToCustomerBill(@Body BeerItem beer, @NotBlank String customerName);


    @Get("/bill/{customerName}")
    Single<Ticket> bill(@NotBlank String customerName);

    @Get("/cost/{customerName}")
    Single<Double> cost(@NotBlank String customerName);

}


