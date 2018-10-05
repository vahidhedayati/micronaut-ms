package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.client.MarkupControllerClient;
import micronaut.demo.beer.dbConfig.CostConfiguration;
import micronaut.demo.beer.dbConfig.StockConfiguration;
import micronaut.demo.beer.domain.BeerCost;
import micronaut.demo.beer.domain.BeerStock;
import micronaut.demo.beer.model.BeerMarkup;
import micronaut.demo.beer.model.StockEntity;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
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
    final MarkupControllerClient markupControllerClient;

    @Inject
    public StockController(EmbeddedServer embeddedServer,
                          StockConfiguration configuration, CostConfiguration costConfig,
                            MongoClient mongoClient,MarkupControllerClient markupControllerClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.costConfig = costConfig;
        this.mongoClient = mongoClient;
        this.markupControllerClient=markupControllerClient;
    }


    /**
     * This uses Single.zip to combine multiple domain objects in this microservice
     * and sends out as a new model out to the gateway application beer-front
     *
     * also connects to markupControllerClient and gets markup that is appended to
     * the stockEntity object received from this app - if no beer-billing it will fall back to MarkupClientFallBack and
     * return a default markup percentage to resell to clients.
     *
     * The issue with this specific example model is that beer-billing is needed to charge customers so - for a more fuller
     * solution the messages probably need to go to a queue that is saved for replay and marked as completed etc just to make sure
     * all transactions are captured otherwise in this specific model - when billing is down the shop will actually not be
     * able to serve beer there may be waiters but no way to actually serve the beer - as such ... so the defaults
     * here although set really will not be sellable.. due to the way it is modelled but beer stock / waiters present
     * just no billing system to charge through so the function to sell beer would at that point be down i guess.
     *
     */

    @Get("/")
    @Override
    public  Single<StockEntity> list() {

        // Firstly collect the list of beerStock as a Single<List
        Single<List<BeerStock>> beers= Flowable.fromPublisher(getCollection().find()).toList();

        // Secondly collect the list of beerCost as a Single<List
        Single<List<BeerCost>> costs= Flowable.fromPublisher(getCost().find()).toList();


        //Thirdly using the client get the baseCosts from the beer-billing - i.e. the markup
        Single<BeerMarkup> markupMaybe1 =  markupControllerClient.baseCosts().onErrorReturnItem(new BeerMarkup("shouldNotBeCalled",50.16,50.16));


        /**
         * Generate a new hashMap to store the name then a new object called StockEntity which combines
         * both above lists using Single.zip and returns then new Single<StockEntity>
         */
        Map<String,StockEntity> map2=new LinkedHashMap<>();


        /**
         * Now using zip combine all of the 3 Single lists/objects together to make a new object called
         * StockEntity...
         */
        Single ss = Single.zip(beers, costs, markupMaybe1,(result1,result2, result3)->{
                    for(BeerStock s : result1){
                        map2.put(s.getName(),new StockEntity(s));
                    }
                    for(BeerCost e: result2) {
                        StockEntity se = map2.get(e.getName());
                        if (se!=null) {
                            se.update(e,result3);
                        }
                    }
                    return map2.values();
                });
        //System.out.println("SS "+ss.getClass());
        return ss;
    }


    @Get("/status")
    public HttpResponse status() {
        return HttpResponse.ok();
    }


    @Get("/lookup/{name}")
    @Override
    public Maybe<BeerStock> find(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
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
