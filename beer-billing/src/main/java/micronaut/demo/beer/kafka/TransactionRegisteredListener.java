package micronaut.demo.beer.kafka;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import micronaut.demo.beer.client.TicketControllerClient;
import micronaut.demo.beer.controller.TicketOperations;
import micronaut.demo.beer.domain.CostSync;
import micronaut.demo.beer.domain.CostSyncConfiguration;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;
import micronaut.demo.beer.service.BillService;
import micronaut.demo.beer.service.CostCalculator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

@RequiredArgsConstructor
@KafkaListener(offsetReset = OffsetReset.EARLIEST)///,groupId="billing", threads=10)
public class TransactionRegisteredListener implements TicketOperations<CostSync> {

    final BillService billService;

    final CostCalculator beerCostCalculator;
    private final CostSyncConfiguration costSyncConfiguration;
    private MongoClient mongoClient;

    public TransactionRegisteredListener(BillService billService, CostCalculator beerCostCalculator,
                                         CostSyncConfiguration costSyncConfiguration,
                                         MongoClient mongoClient
                                         ) {
        this.billService = billService;
        this.costSyncConfiguration=costSyncConfiguration;
        this.beerCostCalculator=beerCostCalculator;
        this.mongoClient=mongoClient;
    }

    @Topic("beer-registered")
    void  beerRegisteredEvent(@KafkaKey String customerName, BeerItem beer) {
        System.out.println(customerName+"---------------------------WE GOT TICKET beer-registered"+beer.getName()+"\n\n\n\n\n");

        //Optional<Ticket> t = getTicketForUser(username);
        //Ticket ticket = t.isPresent() ?  t.get() : new Ticket();
        //ticket.add(beer);
        //ticketControllerClient.addBeerToCustomerBill(beer,username);
        //billService.createBillForCostumer(username, ticket);
        //ticketControllerClient.bill(username);
        //ticketControllerClient.cost(username);
        //System.out.println(username+"---------------------------WE GOT TICKET "+ticket+" billing \n\n\n\n\n");

        Optional<Ticket> t = getTicketForUser(customerName);
        Ticket ticket = t.isPresent() ?  t.get() : new Ticket();
        ticket.add(beer);

        billService.createBillForCostumer(customerName, ticket);

        Double currentCost =  beerCostCalculator.calculateBeerCost(beer);


        CostSync found = find(customerName).blockingGet();
        if (found!=null && found.getCost()!=null) {
            Double cost=found.getCost();
            System.out.println("1 Cost "+cost+" vs "+currentCost);
            if (currentCost>0) {
                Flowable.fromPublisher(getCollection().updateOne(Filters.eq("username", customerName), Updates.set("cost", cost+currentCost))).blockingFirst();
                currentCost+=cost;
            }
        } else {
            System.out.println("2 Cost "+currentCost);
            save(new CostSync(customerName,currentCost)).blockingGet();

        }

    }

    @Override
    public Single<List<CostSync>> list() {
        return Flowable.fromPublisher(
                getCollection()
                        .find()
        ).toList();
    }

    @Override
    public Single<List<CostSync>> byUsername(String name) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("username", name))

        ).toList();
    }
    @ContinueSpan
    private Optional<Ticket> getTicketForUser(@SpanTag("getTicketForUser") String customerName) {
        return Optional.ofNullable(billService.getBillForCostumer(customerName));
    }


    @Override
    public Maybe<CostSync> find(String username) {
        return Flowable.fromPublisher(
                getCollection()
                        .find(eq("username", username))
                        .limit(1)
        ).firstElement();
    }


    @Override
    public Single<CostSync> save(@Valid CostSync costSync) {
        return find(costSync.getUsername())
                .switchIfEmpty(
                        Single.fromPublisher(getCollection().insertOne(costSync))
                                .map(success -> costSync)
                );

    }

    private MongoCollection<CostSync> getCollection() {
        return mongoClient
                .getDatabase(costSyncConfiguration.getDatabaseName())
                .getCollection(costSyncConfiguration.getCollectionName(), CostSync.class);
    }

}
