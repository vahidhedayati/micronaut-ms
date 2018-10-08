package micronaut.demo.beer.controller;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.sse.Event;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.demo.beer.BeerSize;
import micronaut.demo.beer.domain.CostSync;
import micronaut.demo.beer.domain.CostSyncConfiguration;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;
import micronaut.demo.beer.service.BillService;
import micronaut.demo.beer.service.CostCalculator;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

//import io.micronaut.tracing.annotation.ContinueSpan;
//import io.micronaut.tracing.annotation.NewSpan;

@Controller("/billing")
@Validated
public class TicketController implements TicketOperations<CostSync> {

	final EmbeddedServer embeddedServer;
	final CostCalculator beerCostCalculator;
	final BillService billService;
	private final CostSyncConfiguration configuration;
	private MongoClient mongoClient;

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

	//@Inject
	//EventPublisher eventPublisher;
	@Override
	public Maybe<CostSync> find(String username) {
		return Flowable.fromPublisher(
				getCollection()
						.find(eq("username", username))
						.limit(1)
		).firstElement();
	}


	@Override
	public Single<CostSync> save(@Valid CostSync pet) {
		return find(pet.getUsername())
				.switchIfEmpty(
						Single.fromPublisher(getCollection().insertOne(pet))
								.map(success -> pet)
				);

	}

	private MongoCollection<CostSync> getCollection() {
		return mongoClient
				.getDatabase(configuration.getDatabaseName())
				.getCollection(configuration.getCollectionName(), CostSync.class);
	}


	@Inject
	public TicketController(EmbeddedServer embeddedServer,
							CostCalculator beerCostCalculator,
							BillService billService, CostSyncConfiguration configuration,
							MongoClient mongoClient) {
		this.embeddedServer = embeddedServer;
		this.beerCostCalculator = beerCostCalculator;
		this.billService = billService;
		this.configuration = configuration;
		this.mongoClient = mongoClient;
	}

	
	@Get("/reset/{customerName}")
    public HttpResponse resetCustomerBill(@NotBlank String customerName) {
			billService.createBillForCostumer(customerName, null);
    	    return HttpResponse.ok();
    }

	@Post("/addBeer/{customerName}")
	public HttpResponse<BeerItem> addBeerToCustomerBill(@Body BeerItem beer, @NotBlank String customerName) {
        System.out.println("Servig a beer i Ticket app");

		Optional<Ticket> t = getTicketForUser(customerName);
		Ticket ticket = t.isPresent() ?  t.get() : new Ticket();
		ticket.add(beer);

		billService.createBillForCostumer(customerName, ticket);


		/**
		 * Above 4 lines disabled and are executed in TransactionRegisteredListener with Kafka
		 * Disabled to use mongodb shared across multiple beer-billing instances
		 */
		//eventPublisher.beerRegisteredEvent(customerName,beer);

		// Alternative method not used not completed in Listener file either
	 	// eventPublisher.transactionRegisteredEvent(customerName, createEvent(ticket, customerName));

		return HttpResponse.ok(beer);
	}


	@Get("/bill/{customerName}")
	@NewSpan("bill")
    public Single<Ticket> bill(@NotBlank String customerName) {
			Optional<Ticket> t = getTicketForUser(customerName);
    		Ticket ticket = t.isPresent() ?  t.get() : new Ticket();
    		ticket.setDeskId(embeddedServer.getPort());
        return Single.just(ticket);
    }

	@Get("/cost/{customerName}")
	@NewSpan("cost")
	public Single<Double> cost(@NotBlank String customerName) {
		Optional<Ticket> t = getTicketForUser(customerName);
		double cost = t.isPresent() ? beerCostCalculator.calculateCost(t.get()) :
										  beerCostCalculator.calculateCost(getNoCostTicket());


	//	Flowable.fromPublisher(getCollection().updateOne(Filters.eq("username", customerName), Updates.set("cost", cost))).blockingFirst();

		//We save the cost to MongoDB



		//We save the cost to MongoDB

		//Single<CostSync> found = find(customerName).toSingle();
		Double currentCost = Double.valueOf(cost);
		CostSync found = find(customerName).blockingGet();
		if (found!=null) {
			//System.out.println("WE Have from Mongo "+found.toString());
			//found.subscribe(query -> query.getCost() );
			Flowable.fromPublisher(getCollection().updateOne(Filters.eq("username", customerName), Updates.set("cost", cost))).blockingFirst();
			currentCost=found.getCost();
		} else {
			save(new CostSync(customerName,cost));
		}

		return Single.just(currentCost);




		/*
		Maybe<CostSync> found = find(customerName);
		if (found!=null) {
			Flowable.fromPublisher(getCollection().updateOne(Filters.eq("username", customerName), Updates.set("cost", cost))).blockingFirst();
			return found.toSingle().map(m-> m.getCost());
		} else {
			CostSync current = new CostSync(customerName,cost);
			Flowable.fromPublisher(getCollection().insertOne(current)).map(success -> current);
			return  Single.just(cost);
		}
		*/



	}

	@Get(uri = "/users", produces = MediaType.TEXT_EVENT_STREAM)
	Publisher<Event<String>> users() {
		return Flowable.generate(() -> 0, (i, emitter) -> {
			if (i < 100000) {
				Thread.sleep(200);
				emitter.onNext(Event.of(billService.usersInBarMessage()));
			} else {
				emitter.onComplete();
			}
			return ++i;
		});
	}

	@ContinueSpan
	private Ticket getNoCostTicket() {
		BeerItem smallBeer = new BeerItem("Korona", BeerSize.PINT,0,0.00);
		Ticket noCost = new Ticket();
		noCost.add(smallBeer);
		return noCost;
	}

	@ContinueSpan
	private Optional<Ticket> getTicketForUser(@SpanTag("getTicketForUser") String customerName) {
		return Optional.ofNullable(billService.getBillForCostumer(customerName));
	}
}
