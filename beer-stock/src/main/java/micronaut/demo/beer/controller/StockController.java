package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import micronaut.demo.beer.dbConfig.StockConfiguration;
import micronaut.demo.beer.domain.Beer;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
@Controller("/stock")
@Validated
public class StockController implements StockOperations<Beer> {

    final EmbeddedServer embeddedServer;
    private final StockConfiguration configuration;
    private MongoClient mongoClient;

    @Inject
    public StockController(EmbeddedServer embeddedServer,
                          StockConfiguration configuration,
                            MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.configuration = configuration;
        this.mongoClient = mongoClient;
    }
    @Get("/list")
    @Override
    public Single<List<Beer>> list() {
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).toList();
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

    @Override
    public Single<List<Beer>> search(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
        ).toList();
    }

    @Get("/lookup/{name}")
    @Override
    public Maybe<Beer> find(String username) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", username))
                        .limit(1)
        ).firstElement();
    }

    @Get("/pints/{name}/{amount}")
    public Single<Maybe> pints(@NotBlank String name, @NotBlank String amount) {
        Maybe<Beer> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addPint(Integer.valueOf(amount))));
        }
        return null;
    }

    @Get("/halfPints/{name}/{amount}")
    public Single<Maybe> halfPints(@NotBlank String name, @NotBlank String amount) {
        Maybe<Beer> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addHalfPint(Integer.valueOf(amount))));
        }
        return null;
    }

    @Get("/bottles/{name}/{amount}")
    public Single<Maybe> bottles(@NotBlank String name, @NotBlank String amount) {
        Maybe<Beer> found = find(name);
        if (found!=null) {
            return Single.just(found.map(beer-> beer.addBottle(Integer.valueOf(amount))));
        }
        return null;
    }

    @Override
    public Single<Beer> save(@Valid Beer pet) {
        return find(pet.getName())
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(pet))
                                .map(success -> pet)
                );

    }

    private MongoCollection<Beer> getCollection() {
        return mongoClient
                .getDatabase(configuration.getDatabaseName())
                .getCollection(configuration.getCollectionName(), Beer.class);
    }


}
