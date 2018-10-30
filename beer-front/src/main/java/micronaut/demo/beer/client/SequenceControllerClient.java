package micronaut.demo.beer.client;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.model.SequenceTest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Client(id = "stock", path = "/sequence")
public interface SequenceControllerClient {


    @Get("/")
    Single<List<SequenceTest>> list();

    @Get("/lookup/{name}")
    Maybe<SequenceTest> lookup(@NotBlank String name);

    @Post(uri = "/", consumes = MediaType.APPLICATION_JSON)
    Single<SequenceTest> save(@Valid SequenceTest sequenceTest);

    @Post(uri = "/saveSequence", consumes = MediaType.APPLICATION_JSON)
    Single<SequenceTest> saveSequence(String name, String date);
}
