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
import micronaut.demo.beer.domain.BeerMarkup;
import micronaut.demo.beer.domain.MarkupConfiguration;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.mongodb.client.model.Filters.eq;

@Controller("/markup")
@Validated
public class MarkupController implements MarkupOperations<BeerMarkup> {

    final EmbeddedServer embeddedServer;
    private final MarkupConfiguration configuration;
    private MongoClient mongoClient;

    @Inject
    public MarkupController(EmbeddedServer embeddedServer,
                            MarkupConfiguration configuration,
                            MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.mongoClient = mongoClient;
    }

    /*
    @Override
    public Single<List<BeerMarkup>> list() {
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).toList();
    }
    */

    @Get("/")
    @Override
    public Maybe<BeerMarkup> baseCosts() {
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).firstElement();
    }


    @Override
    public Single<BeerMarkup> save(@Valid BeerMarkup cost) {
        return baseCosts()
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(cost))
                                .map(success -> cost)
                );

    }

    private MongoCollection<BeerMarkup> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), BeerMarkup.class);
    }



}
