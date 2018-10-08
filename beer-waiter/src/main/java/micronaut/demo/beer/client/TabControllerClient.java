package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import micronaut.demo.beer.model.BeerItem;

import javax.validation.constraints.NotBlank;

@Client(id = "tab", path = "/billing")
public interface TabControllerClient {

    @Get("/status")
    HttpResponse status();

    @Post("/addBeer/{customerName}")
    HttpResponse<BeerItem> addBeerToCustomerBill(@Body BeerItem beer, @NotBlank String customerName);

}
