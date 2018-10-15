package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CostSync {

    private String username;
    private Double cost;

    public CostSync(String username, Double cost) {
        this.username = username;
        this.cost = cost;
    }

    public CostSync() {}

    @Override
    public String toString() {
        return "CostSync{" +
                "username='" + this.username + '\'' +
                ", cost=" + this.cost +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
