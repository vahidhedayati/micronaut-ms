package micronaut.demo.beer.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import micronaut.demo.beer.controller.StockController;

/**
 *
 * When the waiter application was down StockFallBack2 streamed the event to kafka
 *
 * Now that this bill-stock app is starting up - it will listen for any new topics
 *
 * and action them
 */


@RequiredArgsConstructor
@KafkaListener(offsetReset = OffsetReset.EARLIEST)///,groupId="billing", threads=10)
public class TransactionRegisteredListener {

    final StockController stockController;



    public TransactionRegisteredListener(StockController stockController) {
        this.stockController=stockController;
    }


    /**
     * This is when a beer was sold and something had gone wrong where it could not interact with stock and hence the
     * above fall back has now triggered these calls which in turn call the client to the same controller as if the user had
     * hit the requests - back when the app had been alive.
     *
     * so replaying the missing transactions.
     *
     * All the pints / bottle calcluations end up on a mongo db so it doesn't matter which instance of stock later then
     * looks up stock amounts etc since it will be looking at same db content -
     *
     *
     * this is on assumption that there is a clustered mongo db dealing with the beer-stock aspect of things for all the
     * instances using that same cluster which could many mongo dbs behind that cluster.
     *
     */

    @Topic("increment-pint")
    void incrementPint(@KafkaKey String name,  String amount) {
        stockController.incPints(name,amount);
    }

    //This interacts with beer-stock app - beer-stock listens in for increment-halfpint topic
    @Topic("increment-halfpint")
    void incrementHalfPint(@KafkaKey String name, String amount) {
        stockController.incHalfPints(name,amount);
    }

    //This interacts with beer-stock app - beer-stock listens in for increment-bottle topic
    @Topic("increment-bottle")
    void incrementBottle(@KafkaKey String name,String amount) {
        System.out.println("Increment bottles from kakfa>?>>>> "+name+" amount: "+amount);
        stockController.incBottles(name,amount);
    }

}
