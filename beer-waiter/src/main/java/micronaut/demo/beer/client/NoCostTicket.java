package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Single;
import micronaut.demo.beer.kafka.EventPublisher;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
@Client(id = "billing", path = "/billing")
@Fallback
public class NoCostTicket implements TicketControllerClient{

    private final EventPublisher eventPublisher;

    @Inject
    NoCostTicket(EventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }

    @Override
    public HttpResponse<BeerItem> addBeerToCustomerBill(BeerItem beer, @NotBlank String customerName) {
        System.out.println("Since beer-billing is down we are publishing to kafa - when it returns it can pick this beer sale up");
        eventPublisher.tabRegisteredEvent(customerName,beer);
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
