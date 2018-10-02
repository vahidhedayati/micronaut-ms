package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.dbConfig.CostConfiguration;
import micronaut.demo.beer.dbConfig.StockConfiguration;
import micronaut.demo.beer.domain.BeerCost;
import micronaut.demo.beer.domain.BeerStock;
import micronaut.demo.beer.model.StockEntity;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
@Controller("/stock")
@Validated
public class StockController implements StockOperations<BeerStock> {

    final EmbeddedServer embeddedServer;
    private final StockConfiguration configuration;
    private final CostConfiguration costConfig;
    private MongoClient mongoClient;

    @Inject
    public StockController(EmbeddedServer embeddedServer,
                          StockConfiguration configuration,
                           CostConfiguration costConfig,
                            MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.costConfig = costConfig;
        this.mongoClient = mongoClient;
    }


    /**
     * TODO - current returns a  Single<StockEntity>  - originally was a Single<List> (List object)
     *
     *
     *     Trying to get my head around zipping Single<List> appears to be rather complex
     * @return
     */
    @Get("/")
    @Override
    public  Single<StockEntity> list() {
        Single<List<BeerStock>> beers= Flowable.fromPublisher(getCollection().find()).toList();
        Single<List<BeerCost>> costs= Flowable.fromPublisher(getCost().find()).toList();
        Map<String,StockEntity> map2=new LinkedHashMap<>();
        Single ss = Single.zip(beers, costs, (result1,result2)->{
                    for(BeerStock s : result1){
                        map2.put(s.getName(),new StockEntity(s));
                    }
                    for(BeerCost e: result2) {
                        StockEntity se = map2.get(e.getName());
                        if (se!=null) {
                            se.update(e);
                        }
                    }
                    return map2.values();
                });
        System.out.println("SS "+ss.getClass());
        return ss;
    }

    @Get("/aa")
    public Single<List<BeerStock>> list1() {
        Single<List<BeerStock>> beers= Flowable.fromPublisher(getCollection().find()).toList();
        System.out.println("Beers "+beers);
        //Single<List<BeerCost>> costs= Flowable.fromPublisher(getCost().find()).toList();
        return Flowable.fromPublisher(getCollection().find()).toList(); ///Single.zip(beers,costs);
    }
    /*
    Observable<Customer> customers = //...
Observable<Order> orders = customers
.flatMap(customer ->
Observable.from(customer.getOrders()));
, equivalent and equally verbose:
Observable<Order> orders = customers
.map(Customer::getOrders)
.flatMap(Observable::from);
he need to map from a single item to Iterable is so popular tha
     */


    @Get("/lookup/{name}")
    @Override
    public Maybe<BeerStock> find(String username) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", username))
                        .limit(1)
        ).firstElement();
    }

    @Get("/pints/{name}/{amount}")
    public Single<Maybe> pints(@NotBlank String name, @NotBlank String amount) {
        Maybe<BeerStock> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addPint(Integer.valueOf(amount))));
        }
        return null;
    }

    @Get("/halfPints/{name}/{amount}")
    public Single<Maybe> halfPints(@NotBlank String name, @NotBlank String amount) {
        Maybe<BeerStock> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addHalfPint(Integer.valueOf(amount))));
        }
        return null;
    }

    @Get("/bottles/{name}/{amount}")
    public Single<Maybe> bottles(@NotBlank String name, @NotBlank String amount) {
        Maybe<BeerStock> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addBottle(Integer.valueOf(amount))));
        }
        return null;
    }

    @Override
    public Single<BeerStock> save(@Valid BeerStock pet) {
        return find(pet.getName())
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(pet))
                                .map(success -> pet)
                );

    }

    private MongoCollection<BeerStock> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), BeerStock.class);
    }
    private MongoCollection<BeerCost> getCost() {
        return mongoClient
                .getDatabase(costConfig.getDatabaseName())
                .getCollection(costConfig.getCollectionName(), BeerCost.class);
    }

}
