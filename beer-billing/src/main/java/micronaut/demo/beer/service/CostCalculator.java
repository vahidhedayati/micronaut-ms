package micronaut.demo.beer.service;

import micronaut.demo.beer.model.BeerItem;
import micronaut.demo.beer.model.Ticket;

public interface CostCalculator {
    public double calculateCost(Ticket ticket) ;
    public double calculateBeerCost(BeerItem beerItem) ;
}
