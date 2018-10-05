package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * When all fails and stocks micro service is not up some default out of stock is returned
 */
@Client(id = "stock", path = "/stock")
@Fallback
public class StockFallBack implements StockControllerClient {

    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.serverError();
    }

    @Override
    public Single list() {
        List<StockEntity> ss = new ArrayList<>();
        ss.add(new StockEntity("out of stock",0L,0.00d,0.00d,0.00d,0.00d));
        //ss.add(new StockEntity("out of stock",0L,0.00d,0.00d,0.00d,0.00d) );
        //Single<List<StockEntity>> sss = Single.just(ss);
        return Single.just(ss);
    }

    @Override
    public Single<BeerStock> pints(String name, String amount) {
        return Single.just(new BeerStock("out of stock",0,0));
    }

    @Override
    public Single<BeerStock> halfPints(String name, String amount) {
        return Single.just(new BeerStock("out of stock",0,0));
    }

    @Override
    public  Single<BeerStock> bottles(String name, String amount) {
        return Single.just(new BeerStock("out of stock",0,0));
    }


    @Override
    public Maybe<BeerStock> find(String name) {
        // return Single.just(new BeerStock("out of stock",0,0)).toMaybe();
        return Maybe.empty();
    }

}
