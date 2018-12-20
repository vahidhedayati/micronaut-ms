package micronaut.demo.beer;

import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.tracing.annotation.ContinueSpan;
import io.micronaut.tracing.annotation.SpanTag;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;
import micronaut.demo.beer.service.BillService;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import java.util.Optional;

@ServerWebSocket("/ws/{hostPort}")
public class TransactionWebSocket {

    EmbeddedServer embeddedServer;
    BillService billService;

    @Inject
    public TransactionWebSocket(EmbeddedServer embeddedServer,BillService billService) {
        this.embeddedServer = embeddedServer;
        this.billService=billService;


    }

    @OnOpen
    public Publisher<String> onOpen( String hostPort, WebSocketSession session) {
        //System.out.println("Message "+session.getId());
        return session.send(hostPort);
    }

    @OnMessage
    public Publisher<String> onMessage(String message, WebSocketSession session) {
        return  session.send(message);
    }
    @OnMessage
    public Publisher<String> onMessage(byte[] message, WebSocketSession session) {
        // return session.broadcast(msg);
        //
        String content = new String(message);
        //System.out.println(" content"+content);

        if (content.indexOf(':')>-1) {
            String[] parts = content.split(":");

            String username = parts[0];

            String beerName = parts[1];
            // BeerItem.Size beerSize=BeerItem.Size.MEDIUM;
            if (parts.length > 2) {
                String beerSize = parts[2];


                /**
                 * This is the logic now from TicketController.java - being executed by
                 * each WebsocketClientHander on each running instance of the beer-billing application
                 *
                 *
                 */

                //  System.out.println("Billing "+username+" beerName "+beerName);//
                Optional<Ticket> t = getTicketForUser(username);
                BeerItem beer = new BeerItem(beerName, BeerItem.Size.valueOf(beerSize));// );
                Ticket ticket = t.isPresent() ? t.get() : new Ticket();
                ticket.add(beer);
                System.out.println("Billing " + username + " ticket " + ticket + " size:" + beerSize);
                billService.createBillForCostumer(username, ticket);
            }
        }
        return session.send(content);
    }
    @OnClose
    public Publisher<String> onClose(WebSocketSession session) {
        String msg = "Disconnected!";
        return session.send(msg);
    }

    @ContinueSpan
    private Optional<Ticket> getTicketForUser(@SpanTag("getTicketForUser") String customerName) {
        return Optional.ofNullable(billService.getBillForCostumer(customerName));
    }


}
