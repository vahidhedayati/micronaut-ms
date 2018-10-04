package micronaut.demo.beer.client;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import java.util.ArrayList;
import java.util.List;

@Client(id = "stock", path = "/stock")
@Fallback
public class StockFallBack implements StockControllerClient {


    @Override
    public Single list() {
        System.out.println("Ahh2");
        //Single<StockEntity> single=null;
        List<StockEntity> ss = new ArrayList<>();
        ss.add(new StockEntity("out of stock",0L,0.00d,0.00d,0.00d,0.00d) );
        //ss.add(new StockEntity("out of stock",0L,0.00d,0.00d,0.00d,0.00d) );
        Single<List<StockEntity>> sss = Single.just(ss);
        return sss;

        //Single s = Single.just(new StockEntity("out of stock",0L,0.00d,0.00d,0.00d,0.00d) );

        //return Single.zip(sss,sss,(v,v1)->{
         //   return v1;
        //});


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



    public Maybe<BeerStock> find(String name) {
        // return Single.just(new BeerStock("out of stock",0,0)).toMaybe();
        return Maybe.empty();
    }

}
