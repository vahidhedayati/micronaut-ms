package micronaut.demo.beer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.validation.Validated;
import micronaut.demo.beer.kafka.EventPublisher;
import micronaut.demo.beer.model.BeerItem;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;

@Controller("/billing")
@Validated
public class TicketController {

    final EventPublisher eventPublisher;

    @Inject
    public TicketController(EventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }


    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.ok();
    }



    /**
     * This is to mimick what actual billing would be doing if it were to be running
     * instead of doing the core work - it publishes the event via kafka
     *
     * Kafka like all other technologies such as consul can all be clustered to produce resilience
     * @param beer
     * @param customerName
     * @return
     */
    @Post("/addBeer/{customerName}")
    public HttpResponse<BeerItem> addBeerToCustomerBill(@Body BeerItem beer, @NotBlank String customerName) {
        System.out.println("Serving a beer on tab app ");
        eventPublisher.beerRegisteredEvent(customerName,beer);
        return HttpResponse.ok(beer);
    }

}
