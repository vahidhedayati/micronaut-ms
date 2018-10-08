package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.validation.constraints.NotBlank;
@Client(id = "billing", path = "/billing")
@Fallback
public class NoCostTicket implements TicketControllerClient{
    @Override
    public HttpResponse<BeerItem> addBeerToCustomerBill(BeerItem beer, @NotBlank String customerName) {
        return HttpResponse.ok();
    }

    @Override
    public Single<Ticket> bill(@NotBlank String customerName) {
        return Single.just(new Ticket());
    }

    @Override
    public HttpResponse resetCustomerBill(@NotBlank String customerName) {
        return HttpResponse.ok();
    }

    @Override
    public Single<Double> cost(@NotBlank String customerName) {
        return Single.just(Double.valueOf(0));
    }
}
