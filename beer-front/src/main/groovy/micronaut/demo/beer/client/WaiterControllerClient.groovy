package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.Client;
import io.reactivex.Single;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.CustomerBill;

import javax.validation.constraints.NotBlank;

@Client(id = "waiter", path = "/waiter")
public interface WaiterControllerClient {

    @Get("/beer/{customerName}")
    public Single<Beer> serveBeerToCustomer(@NotBlank String customerName);
    
    @Get("/bill/{customerName}")
    public Single<CustomerBill> bill(@NotBlank String customerName);
}
