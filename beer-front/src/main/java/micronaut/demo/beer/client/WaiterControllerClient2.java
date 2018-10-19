package micronaut.demo.beer.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;


@Client(id = "waiter", path = "/waiter")
public interface WaiterControllerClient2 {


    @Get("/status")
    HttpResponse status();

    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    Single<Beer> serveBeerToCustomer(@JsonProperty("customerName") String customerName,
                                                           @JsonProperty("beerName") String beerName,
                                                           @JsonProperty("beerType") String beerType,
                                                           @JsonProperty("amount") String amount,
                                                           @JsonProperty("price") String price);



    @Get("/bill/{customerName}")
    Single<CustomerBill> bill(@NotBlank String customerName);
}
