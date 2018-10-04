package micronaut.demo.beer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.reactivex.Single;
import micronaut.demo.beer.client.StockControllerClient;

import java.net.URI;
import java.util.Collections;

@Controller("/")
public class GatewayController {


    private final StockControllerClient stockControllerClient;

    GatewayController(StockControllerClient stockControllerClient) {
        this.stockControllerClient = stockControllerClient;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = "/")
    public HttpResponse index() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }


    @Get("/stock")
   public Single stock() {
        return stockControllerClient.list().onErrorReturnItem(Collections.emptyList());
    }

}
