package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.validation.constraints.NotBlank;

/**
 * When all fails and stocks micro service is not up some default out of stock is returned
 */
@Client(id = "tab", path = "/billing")
@Fallback
public class TabFallBack implements TabControllerClient {

    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.serverError();
    }


    @Override
    public HttpResponse<BeerItem> addBeerToCustomerBill(BeerItem beer, @NotBlank String customerName) {
        System.out.println("This is the fall back of the fallback - of no NoCostTicket calling TabControllerClient which failed and fell here");
        return HttpResponse.ok();
    }
}
