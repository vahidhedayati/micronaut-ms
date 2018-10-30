package micronaut.demo.beer.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.Fallback;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.SequenceTest;
import micronaut.demo.beer.kafka.EventPublisher;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Client("/sequence")
@Fallback
public class SequenceClientFallBack implements SequenceControllerClient {
    final EventPublisher eventPublisher;

    @Inject
    public SequenceClientFallBack(EventPublisher eventPublisher) {
        this.eventPublisher=eventPublisher;
    }

    public Single<List<SequenceTest>> list() {
        List<SequenceTest> ss = new ArrayList<>();
        ss.add(new SequenceTest("system down",new Date()));
        return Single.just(ss);
    }

    public Maybe<SequenceTest> lookup(@NotBlank String name) {
        return null;
    }

    public Single<SequenceTest> saveSequence(String name, String date){

        eventPublisher.saveSequence(name,date);
        return Single.just(new SequenceTest());
    }

    public Single<SequenceTest> save(@Valid SequenceTest sequenceTest) {
        System.out.println("kafka ---------------------"+sequenceTest);
        eventPublisher.saveSequence(sequenceTest.getName(),sequenceTest.getDateCreatedString());
        return Single.just(new SequenceTest());

    }
}
