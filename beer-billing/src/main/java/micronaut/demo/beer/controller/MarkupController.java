package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerMarkup;
import micronaut.demo.beer.domain.BeerMarkupConfiguration;

import javax.inject.Inject;
import javax.validation.Valid;

import static com.mongodb.client.model.Filters.eq;

@Controller("/markup")
@Validated
public class MarkupController implements MarkupOperations<BeerMarkup> {

    final EmbeddedServer embeddedServer;
    private final BeerMarkupConfiguration configuration;
    private MongoClient mongoClient;

    @Inject
    public MarkupController(EmbeddedServer embeddedServer,
                            BeerMarkupConfiguration configuration,
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

    @Get("/status")
    @ContinueSpan
    public HttpResponse status() {

        return HttpResponse.ok();
    }

    @Get("/")
    @ContinueSpan
    @Override
    public Single<BeerMarkup> baseCosts() {
        return  Flowable.fromPublisher(getCollection().find(eq("name","defaultMarkup"))).firstElement().toSingle();
    }

    public Maybe<BeerMarkup> altCosts() {
        return  Flowable.fromPublisher(getCollection().find(eq("name","defaultMarkup"))).firstElement();
    }
    @Override
    public Single<BeerMarkup> save(@Valid BeerMarkup cost) {
        return altCosts()
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
