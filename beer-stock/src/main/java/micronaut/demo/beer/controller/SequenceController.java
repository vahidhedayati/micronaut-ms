package micronaut.demo.beer.controller;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.dbConfig.SequenceConfiguration;
import micronaut.demo.beer.domain.SequenceTest;

import javax.inject.Inject;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Controller("/sequence")
@Validated
public class SequenceController implements SequenceOperations<SequenceTest> {

    final EmbeddedServer embeddedServer;
    private final SequenceConfiguration sequenceConfiguration;
    private MongoClient mongoClient;

    @Inject
    public SequenceController(EmbeddedServer embeddedServer,
                              SequenceConfiguration sequenceConfiguration,
                              MongoClient mongoClient) {
        this.embeddedServer = embeddedServer;
        this.sequenceConfiguration = sequenceConfiguration;
        this.mongoClient = mongoClient;
    }

    @Override
    @ContinueSpan
    public Single<List<SequenceTest>> list() {
        System.out.println("listing sequence");
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).toList();
    }

    @Override
    public Single<List<SequenceTest>> search(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
        ).toList();
    }

    @Override
    public Maybe<SequenceTest> find(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("name", name))
                        .limit(1)
        ).firstElement();
    }

    @Post(uri = "/saveSequence", consumes = MediaType.APPLICATION_JSON)
    public Single<SequenceTest> saveSequence(String name, String date) {

        System.out.println("Save Seq alt called");
        return save(new SequenceTest(name, getDateCreated(date)));
    }

    public Date getDateCreated(String date) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            return df.parse(date);
        }catch (Exception e) {

        }
        return null;
    }

    @Override
    public Single<SequenceTest> save(@Valid SequenceTest sequenceTest) {
        System.out.println("Sequence being added: "+sequenceTest);
        return find(sequenceTest.getName())
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(sequenceTest))
                                .map(success -> sequenceTest)
                );

    }

    private MongoCollection<SequenceTest> getCollection() {
        return mongoClient
                .getDatabase(sequenceConfiguration.getDatabaseName())
                .getCollection(sequenceConfiguration.getCollectionName(), SequenceTest.class);
    }

}
