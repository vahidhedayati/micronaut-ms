package micronaut.demo.beer.client;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.domain.BeerCost;
import micronaut.demo.beer.domain.SequenceTest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Client("/sequence")
@CircuitBreaker(delay = "1s", attempts = "5", multiplier = "3", reset = "100s")
public interface SequenceControllerClient {


    @Get("/")
    public Single<List<SequenceTest>> list();

    @Get("/lookup/{name}")
    public Maybe<SequenceTest> lookup(@NotBlank String name);

    @Post("/")
    public Single<SequenceTest> save(@Valid SequenceTest sequenceTest);

    @Post(uri = "/saveSequence", consumes = MediaType.APPLICATION_JSON)
    Single<SequenceTest> saveSequence(String name, String date);

}
