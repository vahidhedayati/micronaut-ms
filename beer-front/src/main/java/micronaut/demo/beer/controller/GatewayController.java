package micronaut.demo.beer.controller;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.HealthService;
import com.fasterxml.jackson.annotation.JsonProperty;
import groovy.util.logging.Slf4j;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import micronaut.demo.beer.client.*;
import micronaut.demo.beer.enums.BeerSize;
import micronaut.demo.beer.model.Beer;
import micronaut.demo.beer.model.BeerStock;
import micronaut.demo.beer.model.CustomerBill;
import micronaut.demo.beer.model.SequenceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Controller("/")
public class GatewayController {
    final static Logger log = LoggerFactory.getLogger(GatewayController.class);

    private final StockControllerClient stockControllerClient;
    private final WaiterControllerClient waiterControllerClient;
    private final StockControllerClient2 stockControllerClient2;
    private final WaiterControllerClient2 waiterControllerClient2;
    private final SequenceControllerClient sequenceControllerClient;

    GatewayController(StockControllerClient stockControllerClient,
                      WaiterControllerClient waiterControllerClient,
                      StockControllerClient2 stockControllerClient2,
                      WaiterControllerClient2 waiterControllerClient2,
                      SequenceControllerClient sequenceControllerClient
                     ) {
        this.stockControllerClient = stockControllerClient;
        this.stockControllerClient2 = stockControllerClient2;
        this.waiterControllerClient=waiterControllerClient;
        this.waiterControllerClient2 = waiterControllerClient2;
        this.sequenceControllerClient=sequenceControllerClient;
    }

    @Produces(MediaType.TEXT_HTML)
    @Get(uri = "/")
    @ContinueSpan
    public HttpResponse index() {
        return HttpResponse.redirect(URI.create("/index.html"));
    }


    /**
     * The long and short -
     *
     * After I enabled zipkin tracing noticed lots of
     * https://docs.micronaut.io/latest/api/io/micronaut/discovery/consul/client/v1/ConsulOperations.html#pass-java.lang.String-
     *
     * Flooding zipkin
     *
     * The reason was all the internal single checks triggered from reactjs site through gateway - each single request checking
     * each http client on the backend and returning yes it was alive or no it was down -
     *
     * The 2nd attempt to reduce was to try to combine into 1 call
     *
     * Then finally I have decided rather than doing all of the check through http - the question is does something else already know this
     * without all these checks ? the answer is yes  - consul does -
     *
     * So without all this overhead the check is simplified to work out if consul thinks each of those apps it dynamically finds
     * are healthy or not ..
     *
     *
     * I have been messing around quite a bit with attempting to make frontend aka reach behave differently
     * "react to things going down" as they do on the backend live !
     *
     * I need to point out all you are about to do is actually add lots of additional overhead and not gain much -
     * since something going wrong could be any millisecond in a second of a minute...
     *
     * Can you react quick enough within milliseconds ? you can but how much overhead for that ?
     *
     * Websockets where all apps connect to client aka react frontend via websockets could be one way -
     * Again you need to measure if at least 1 healthy node exists -
     *
     * Is this now not the same check that consul has internally built within it ?
     *
     * Perhaps the most cost effective would be something like below consul with a single socket on gateway that is a
     * Flowable repeated task and checks consul and responds via websocket if something goes wrong.
     *
     * This goes back to the are you quick enough to capture it as the user clicks it----
     *
     *
     * In the end ----------- you are far better off forgetting about this way and attempting to build your application
     * with through fall back technology to be able to react accordingly regardless of state of backend -
     * Well as long as minimal is up and running.
     *
     * I will go into this in a further example of selling beer -- my arms are getting stronger -
     *
     * @return
     */

    @Get("/appStatus")
    @ContinueSpan
    public Single appStatus() {
        @Property(name = "CONSUL_HOST")
        String propertyName="localhost";
        System.out.println(propertyName+" property name");
        ConsulClient client = new ConsulClient(propertyName);

        //Get a total list of micro services appearing under consul
        List<String> results = getAllConsulClients(client);
        //Create a new array list
        List<String> healthyNodesFound = new ArrayList<>();

        //Go through each found apps ensure there is at least 1 healthy node if so add it into found health check
        for (String node:results) {
            Response<List<HealthService>> healthyServices = client.getHealthServices(node, true, QueryParams.DEFAULT);
            HealthService healthServices = healthyServices.getValue().get(0);
            if (healthServices!=null) {
                System.out.println ("=== "+node+" > "+healthServices.getService().getAddress()+" "+healthServices.getService().getPort());
                healthyNodesFound.add(node);
            }
        }

        Map<String,Integer> responses = new HashMap<String,Integer>();
        responses.put("billing",healthyNodesFound.contains("billing")?200:400);
        responses.put("waiter",healthyNodesFound.contains("waiter")?200:400);
        responses.put("stock", healthyNodesFound.contains("stock")?200:400);


        return Single.just(responses);


        /**
         * This below is old attempt to collect all http responses - not efficient as above
         */


        /*
        Map<String,HttpResponse> responses = new HashMap<String,HttpResponse>();
        responses.put("billing", markupControllerClient.status());
        responses.put("waiter", waiterControllerClient.status());
        responses.put("stock", stockControllerClient.status());
        responses.put("tab", tabControllerClient.status());
        */


        /*
        //String returnString="";

        try {
            ObjectMapper mapperObj = new ObjectMapper();
            returnString=mapperObj.writeValueAsString(responses);
        }catch(Exception e){

        }
        //"{billing:"+ markupControllerClient.status()+",waiter:"+ waiterControllerClient.status()+", stock:"+stockControllerClient.status()+", tab:"+tabControllerClient.status()+" }";

        */


    }


    /**
     * This collects a string array list of all / any microservices registered on consul host above
     * @param client
     * @return
     */
    public List<String> getAllConsulClients(ConsulClient client) {
        List<String> instances = new ArrayList<>();

        Response<Map<String, List<String>>> services = client
                .getCatalogServices(QueryParams.DEFAULT);
        for (String serviceId : services.getValue().keySet()) {
            instances.add(serviceId);
        }
        return instances;
    }


    @Get("/stock")
   @ContinueSpan
   public Single stock() {
        return stockControllerClient.list().onErrorReturnItem(Collections.emptyList());
   }


    /**
     * Below checks are ignored now - appStatus above checks consul
     * @return
     */

   /*

    @Get("/billingStatus")
    @ContinueSpan
    public HttpResponse billingStatus() {
        return markupControllerClient.status();
    }

    @Get("/waiterStatus")
    @ContinueSpan
    public HttpResponse waiterStatus() {
        return waiterControllerClient.status();
    }

    @Get("/stockStatus")
    @ContinueSpan
    public HttpResponse stockStatus() {
        return stockControllerClient.status();
    }

    @Get("/tabStatus")
    @ContinueSpan
    public HttpResponse tabStatus() {
        return tabControllerClient.status();
    }

    */


    /**
     * -------------------------------------------------------------------------------
     * All of below is access via beer/index.js on reactjs frontend
     *
     */
    @Post(uri = "/beer", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<Beer> serveBeerToCustomer(@SpanTag("gateway.beer") @Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
            System.out.println("Serving "+beerName+" "+price);
        return waiterControllerClient.serveBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0, 0.00));
    }


    /**
     * This calls waiter app to call the tab app -
     *
     * Gateway itself knowing tab app is up and billing is down from the health checks
     * @param customerName
     * @param beerName
     * @param beerType
     * @param amount
     * @param price
     * @return
     */
    @Post(uri = "/tab", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<Beer> tabBeerToCustomer(@SpanTag("gateway.tab")  @Body("customerName")  String customerName, @Body("beerName")  String beerName, @Body("beerType")  String beerType, @Body("amount")  String amount, @Body("price")  String price) {
        System.out.println("Tab beer: "+beerName+" "+price);
        return waiterControllerClient.tabBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer("out of stock",BeerSize.PINT,0, 0.00));
    }


    @Post(uri = "/pints", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> addPints(@SpanTag("gateway.pints") @Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("addPints "+name+" "+amount);
        return stockControllerClient.pints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/halfPints", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> halfPints(@SpanTag("gateway.halfPints") @Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("halfPints "+name+" "+amount);
        return stockControllerClient.halfPints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/bottles", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> bottles(@SpanTag("gateway.bottles") @Body("name")  String name, @Body("amount")  String amount) {
        System.out.println("bottles "+name+" "+amount);
        return stockControllerClient.bottles(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Get("/bill/{customerName}")
    @ContinueSpan
    public Single<CustomerBill> bill(@SpanTag("gateway.bill") @NotBlank String customerName) {
        System.out.println("Getting bill for "+customerName+" "+new Date());
        return waiterControllerClient.bill(customerName)
                .onErrorReturnItem(new CustomerBill());
    }

    @Get("/lookup/{name}")
    @ContinueSpan
    public Maybe<BeerStock> lookup(@SpanTag("gateway.beerLookup") @NotBlank String name) {
        System.out.println("Looking up beer for "+name+" "+new Date());
        return stockControllerClient.find(name)
                .onErrorReturnItem(new BeerStock());
    }




    /**
     *
     *
     * Below is going to use client2 files of each client which are engineered with better fallbacks
     *
     * this makes the whole health check redundant as such
     *
     * "so long as minimal is up and running"
     * -------------------------------------------------------------------------------
     * All of below is access via beer2/index.js on reactjs frontend
     *
     * We don't need a beertab action
     *
     */
    @Post(uri = "/beer2", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<Beer> serveNewBeerToCustomer(@JsonProperty("customerName") String customerName,
                                        @JsonProperty("beerName") String beerName,
                                        @JsonProperty("beerType") String beerType,
                                        @JsonProperty("amount") String amount,
                                        @JsonProperty("price") String price) {
        System.out.println("Serving ---- beer2 "+beerName+" "+price);
        return waiterControllerClient2.serveBeerToCustomer(customerName,beerName,beerType,amount,price)
                .onErrorReturnItem(new Beer());
    }



    @Post(uri = "/pints2", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> addNewPints(@JsonProperty("name") String name,@JsonProperty("amount") String amount) {
        System.out.println("addPints 2 ----------------------------------- "+name+" "+amount);
        return stockControllerClient2.pints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/halfPints2", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> newHalfPints(@JsonProperty("name") String name,@JsonProperty("amount") String amount) {
        System.out.println("halfPints2 ---------------------  "+name+" "+amount);
        return stockControllerClient2.halfPints(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Post(uri = "/bottles2", consumes = MediaType.APPLICATION_JSON)
    @ContinueSpan
    Single<BeerStock> newBottles(@JsonProperty("name") String name,@JsonProperty("amount") String amount) {
        System.out.println("bottles-----------------------------------2 "+name+" "+amount);
        return stockControllerClient2.bottles(name,amount)
                .onErrorReturnItem(new BeerStock());
    }

    @Get("/bill2/{customerName}")
    @ContinueSpan
    public Single<CustomerBill> newBill(@NotBlank String customerName) {
        System.out.println("Getting bill for "+customerName+" "+new Date());
        return waiterControllerClient2.bill(customerName)
                .onErrorReturnItem(new CustomerBill());
    }


    /**
     * ----------------------
     *
     *
     * DB Sequential test - using kafka fall back -
     * does things get added in sequence sent ?
     *
     *
     */


    @Get("/sequence")
    public Single<List<SequenceTest>> listSequence() {
        return sequenceControllerClient.list();
    }


    /**
     * From some basic tests it appears flooding the http client with a sequential order of records are added non sequentially
     * meaning the order they are received when flooded does not appear to be the order by which it was flooded
     *
     * Once running the testSequence test - you can order records on mongo like this
     * $ mongo
     *  use beerstock;
     *  db.sequence.find().sort({"dateCreated":1});
     *
     * Above orders by date created ascending and is not order sent - it is also visible by the console logs sent back when called
     *
     */

    @Get("/testSequence")
    public Single<List<SequenceTest>> testSequence() {
        System.out.println("Testing sequence");

        List<String> sequences = new ArrayList<>();
        for (int a=0; a < 1000; a++) {
            sequences.add("Name"+a);
            //sequences.add(new SequenceTest("Name: "+a.toString(),new Date()));
        }
        Flowable.fromIterable(sequences)
            .map(name -> new SequenceTest(name, new Date()))
            .forEach(k-> this.saveSequence(k.getName(),k.getDateCreatedString()).subscribe());

        return sequenceControllerClient.list();
    }



    @Post(uri ="/saveSequence", consumes = MediaType.APPLICATION_JSON)
    public Single<SequenceTest> saveSequence(@JsonProperty("name") String name,@JsonProperty("date") String date) {
        System.out.println("Saving sequence "+name+" "+date);
        return sequenceControllerClient.saveSequence(name,date);
    }

}
