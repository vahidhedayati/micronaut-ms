package micronaut.demo.beer;

import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.reactivestreams.Publisher;

import javax.inject.Inject;

@ServerWebSocket("/ws/{hostPort}")
public class TransactionWebSocket {

    EmbeddedServer embeddedServer;

    @Inject
    public TransactionWebSocket(EmbeddedServer embeddedServer) {
        this.embeddedServer = embeddedServer;
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
/*
        if (content.indexOf(':')>-1) {
            String[] parts = content.split(":");

            String username = parts[0];

            String beerName = parts[1];
            // BeerItem.Size beerSize=BeerItem.Size.MEDIUM;
            if (parts.length > 2) {
                String beerSize = parts[2];




                //  System.out.println("Billing "+username+" beerName "+beerName);//
                Optional<Ticket> t = getTicketForUser(username);
                BeerItem beer = new BeerItem(beerName, BeerItem.Size.valueOf(beerSize));// );
                Ticket ticket = t.isPresent() ? t.get() : new Ticket();
                ticket.add(beer);
                System.out.println("Billing " + username + " ticket " + ticket + " size:" + beerSize);
                billService.createBillForCostumer(username, ticket);
            }
            */
        //}
        return session.send(content);
    }
    @OnClose
    public Publisher<String> onClose(WebSocketSession session) {
        String msg = "Disconnected!";
        return session.send(msg);
    }


}
