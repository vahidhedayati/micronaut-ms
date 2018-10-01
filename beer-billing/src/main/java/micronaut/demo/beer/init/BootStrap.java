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
import micronaut.demo.beer.domain.BeerCost;
import micronaut.demo.beer.domain.CostConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Upon application startup initialise a 5% markup on all bottles/pints if it already does not exist
 * This in short defaults the db to a 5% charge which the user can later change I guess dynamically if they wanted
 *
 * aka happy hours etc - setup schedules to reduce the markup per bottle or per pint...
 *
 *
 *
 * Vahid Hedayati
 */
@Slf4j
@CompileStatic
@Singleton
public class BootStrap implements ApplicationEventListener<ServerStartupEvent> {

    final EmbeddedServer embeddedServer;
    private MongoClient mongoClient;
    private final CostConfiguration costConfig;

    final static Logger log = LoggerFactory.getLogger(BootStrap.class);

    @Inject
    public BootStrap(EmbeddedServer embeddedServer, MongoClient mongoClient, CostConfiguration costConfig) {
        this.embeddedServer = embeddedServer;
        this.mongoClient = mongoClient;
        this.costConfig=costConfig;

    }


    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        setupDefaults();
    }

    void setupDefaults() {
            Maybe<BeerCost> currentBeer = Flowable.fromPublisher(
                    getCosts()
                            .find()
                            .limit(1)
            ).firstElement();

            BeerCost beerCost = new BeerCost(5.00,5.00);
            currentBeer.switchIfEmpty(
                    Single.fromPublisher(getCosts().insertOne(beerCost))
                            .map(success -> beerCost)
            );
    }


    private MongoCollection<BeerCost> getCosts() {
        return mongoClient
                .getDatabase(costConfig.getDatabaseName())
                .getCollection(costConfig.getCollectionName(), BeerCost.class);
    }
}
