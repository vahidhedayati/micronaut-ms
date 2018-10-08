package micronaut.demo.beer.init;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import groovy.transform.CompileStatic;
import groovy.util.logging.Slf4j;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.dbConfig.CostConfiguration;
import micronaut.demo.beer.dbConfig.StockConfiguration;
import micronaut.demo.beer.domain.BeerStock;
import micronaut.demo.beer.domain.BeerCost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

/**
 * Upon start up of the stock application it will generate some default beers and default pricing per beer type
 *
 */
@Slf4j
@CompileStatic
@Singleton
public class BootStrap implements ApplicationEventListener<ServerStartupEvent> {

    final EmbeddedServer embeddedServer;
    private MongoClient mongoClient;
    private final CostConfiguration costConfig;
    private final StockConfiguration stockConfig;
    final static Logger log = LoggerFactory.getLogger(BootStrap.class);

    @Inject
    public BootStrap(EmbeddedServer embeddedServer,MongoClient mongoClient,CostConfiguration costConfig,StockConfiguration stockConfig) {
        this.embeddedServer = embeddedServer;
        this.mongoClient = mongoClient;
        this.costConfig=costConfig;
        this.stockConfig=stockConfig;
    }


    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        setupDefaults();
    }

    void setupDefaults() {

        ArrayList<String> beers =  new ArrayList<>(Arrays.asList("Budweiser", "Heineken", "Peroni", "Coors"));
        int i =0;
        for (String beer : beers) {
            System.out.println("Setting up beer: "+beer);
            i++;

            /**
             * Generate the costs for those beer names
             * by default all getting priced at business end at 0.99 per bottle and 0.85 per pint
             * So this is internal prices - not what is sold to the client..
             *
             * Selling on the markup is set by the beer-billing applications..
             *
             * Changes to the markup in billing will then reprice the percentage charged on top of these prices
             *
             */
            Maybe<BeerCost> currentCost = Flowable.fromPublisher(
                    getCosts()
                            .find(eq("name",beer))
                            .limit(1)
            ).firstElement();
            //Cost the beers differently per beer type
            BeerCost beerCostObject = new BeerCost(beer, 0.99+i,0.85+i);
            currentCost.switchIfEmpty(
                    Single.fromPublisher(getCosts().insertOne(beerCostObject))
                            .map(success -> beerCostObject)
            ).subscribe(beerCost-> System.out.println("BeerCost "+beerCost));
            //.subscribe(System.out::println);
            //
            BeerCost cost = currentCost.blockingGet();
            System.out.println("BeerCost =  ::::: "+cost);


            /**
             * Generate actual beer object
             * by default all getting a 1000 bottles and 2 barrels of larger
             */
            Maybe<BeerStock> currentBeer = Flowable.fromPublisher(
                    getStock()
                            .find(eq("name", beer))
                            .limit(1)
            ).firstElement();

            BeerStock beerObject = new BeerStock(beer, 1000L, 2);

            currentBeer.switchIfEmpty(

                    Single.fromPublisher(getStock().insertOne(beerObject))
                            .map(success -> beerObject)

            ).subscribe(System.out::println);

            //BeerStock stock = currentBeer.blockingGet();
            //System.out.println("We actually have :::::::: "+stock.getName());



        }
    }

    private MongoCollection<BeerStock> getStock() {
        return mongoClient
                .getDatabase(stockConfig.getDatabaseName())
                .getCollection(stockConfig.getCollectionName(), BeerStock.class);
    }

    private MongoCollection<BeerCost> getCosts() {
        return mongoClient
                .getDatabase(costConfig.getDatabaseName())
                .getCollection(costConfig.getCollectionName(), BeerCost.class);
    }
}
