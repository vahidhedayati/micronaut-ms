package micronaut.demo.beer.client;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.kafka.EventPublisher;
import micronaut.demo.beer.model.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * This is really due to demonstration services we have an identical class to StockController
 * but this fall back takes a very different route to what the original code did
 *
 * It simply pushes any increments of pints/bottles to a kafka stream
 *
 * when stock application comes alive the stream is processed and items duely amended to be at
 * new level of pints/bottles left in stock
 */
@Client(id = "stock", path = "/stock")
@Fallback
public class StockFallBack2 implements StockControllerClient2 {

    final EventPublisher eventPublisher;

    @Inject
    public StockFallBack2(EventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }


    /**
     * ----
     * All of below do what StockFallBack did - this is when the app has realised something is wrong
     *
     *
     */
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
    public Maybe<BeerStock> find(String name) {
        // return Single.just(new BeerStock("out of stock",0,0)).toMaybe();
        return Maybe.empty();
    }

    /**
     * -------------------------------------------------
     *
     * All below methods differ from StockFallBack.groovy - they have a eventPublisher which pushes it to kafka
     *
     *
     * This is likely to be triggered if the user hit the page and the app was healthy
     * and was able to purchase a beer - but by the time they clicked the app or something had gone wrong
     *
     */

    @Override
    public Single<BeerStock> pints(String name, String amount) {
        System.out.println("Stock FALL BACK using kafka stream adding pints");
        eventPublisher.incrementPint(name,amount);
        return Single.just(new BeerStock("out of stock",0,0));
    }

    @Override
    public Single<BeerStock> halfPints(String name, String amount) {
        System.out.println("Stock FALL BACK using kafka stream adding halfPints");
        eventPublisher.incrementHalfPint(name,amount);
        return Single.just(new BeerStock("out of stock",0,0));
    }

    @Override
    public Single<BeerStock> bottles(String name, String amount) {
        System.out.println("Stock FALL BACK using kafka stream adding bottles");
        eventPublisher.incrementBottle(name,amount);
        return Single.just(new BeerStock("out of stock",0,0));
    }


}
