package micronaut.demo.beer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CostSync {

    private String username;
    private Double cost;


    @JsonCreator
    public CostSync(@JsonProperty("username") String username, @JsonProperty("cost") Double cost) {
        this.username = username;
        this.cost = cost;
    }

    public CostSync() {
    }

    protected void setUsername(String u) {
        this.username = u;
    }

    protected void setCost(String u) {
        this.cost = Double.valueOf(u);
    }
    protected void setCost(Double u) { this.cost = u; }

    public String getUsername() {
        return this.username;
    }

    public Double getCost(){
       return this.cost;
    }


    @Override
    public String toString() {
        return "CostSync{" +
                "username='" + username + '\'' +
                ", cost=" + cost +
                '}';
    }
}
