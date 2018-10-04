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
import micronaut.demo.beer.domain.BeerMarkup;
import micronaut.demo.beer.domain.BeerMarkupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

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
    private final BeerMarkupConfiguration configuration;

    final static Logger log = LoggerFactory.getLogger(BootStrap.class);

    @Inject
    public BootStrap(EmbeddedServer embeddedServer, MongoClient mongoClient, BeerMarkupConfiguration configuration) {
        this.embeddedServer = embeddedServer;
        this.mongoClient = mongoClient;
        this.configuration=configuration;

    }


    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        setupDefaults();
    }

    void setupDefaults() {
            List<BeerMarkup> markups = Flowable.fromPublisher(getCosts().find()).toList().blockingGet();

            for (int i =0; i < markups.size(); i++) {

                BeerMarkup markup=markups.get(i);
                System.out.println("Iterate >>> "+markup.getBottleMarkup()+" "+markup.getName());

            }

            Maybe<BeerMarkup> currentBeer = Flowable.fromPublisher(getCosts().find(eq("name","defaultMarkup")).limit(1)).firstElement();
                   ;
            BeerMarkup beerCost = new BeerMarkup("defaultMarkup",5.11,5.12);
            currentBeer.switchIfEmpty(
                    Single.fromPublisher(getCosts().insertOne(beerCost))
                            .map(success -> beerCost)
            ).subscribe(s-> System.out.println("RESULTS -------------------------------------------------------->>"+s));

        BeerMarkup stock = currentBeer.blockingGet();
       // System.out.println("MARKUP--------------------------------------------------------- :::::::: "+stock.getBottleMarkup()+" "+stock.getName()+" "+stock.getPintMarkup());
        System.out.println("MARKUP--------------------------------------------------------- :::::::: "+stock);

    }


    private MongoCollection<BeerMarkup> getCosts() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), BeerMarkup.class);
    }
}
