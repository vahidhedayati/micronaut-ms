package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;

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

}
