package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.annotation.Controller;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.dbConfig.CostConfiguration;
import micronaut.demo.beer.domain.BeerCost;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
@Controller("/cost")
@Validated
public class CostController implements CostOperations<BeerCost> {

    final EmbeddedServer embeddedServer;
    private final CostConfiguration configuration;
    private MongoClient mongoClient;

    @Inject
    public CostController(EmbeddedServer embeddedServer,
                          CostConfiguration configuration,
                          MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.mongoClient = mongoClient;
    }

    @Override
    public Single<List<BeerCost>> list() {
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).toList();
    }

    @Override
    public Single<List<BeerCost>> search(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
        ).toList();
    }

    @Override
    public Maybe<BeerCost> find(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
                        .limit(1)
        ).firstElement();
    }


    @Override
    public Single<BeerCost> save(@Valid BeerCost beerCost) {
        return find(beerCost.getName())
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(beerCost))
                                .map(success -> beerCost)
                );

    }

    private MongoCollection<BeerCost> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), BeerCost.class);
    }

}
