package micronaut.demo.beer.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import micronaut.demo.beer.domain.CostSync;
import micronaut.demo.beer.model.Ticket;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor
public class MapBillService implements BillService {


    Map<String, Ticket> billsPerCustomer = new HashMap<>();
    public Ticket getBillForCostumer(String username) { return billsPerCustomer.get(username); }

    public void  createBillForCostumer(String username, Ticket ticket) {

        billsPerCustomer.put(username,ticket);


    };


    public String usersInBarMessage() {
        int howManyUsers = size();
        if (howManyUsers==0) {
            return "The bar is empty!";
        } else {
            String users = billsPerCustomer.keySet().stream().collect(Collectors.joining(","));
            return "The bar has " + howManyUsers + " users: " + users;
        }
    }

    private int size() {
        return billsPerCustomer.size();
    }


}
