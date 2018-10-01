package micronaut.demo.beer.init;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import groovy.transform.CompileStatic;
import groovy.util.logging.Slf4j;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import micronaut.demo.beer.dbConfig.CostConfiguration;
import micronaut.demo.beer.dbConfig.StockConfiguration;
import micronaut.demo.beer.domain.Beer;
import micronaut.demo.beer.domain.BeerCost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

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


    }

    private MongoCollection<Beer> getStock() {
        return mongoClient
                .getDatabase(stockConfig.getDatabaseName())
                .getCollection(stockConfig.getCollectionName(), Beer.class);
    }

    private MongoCollection<BeerCost> getCosts() {
        return mongoClient
                .getDatabase(costConfig.getDatabaseName())
                .getCollection(costConfig.getCollectionName(), BeerCost.class);
    }
}
