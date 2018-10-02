package micronaut.demo.beer.model;

import micronaut.demo.beer.domain.BeerCost;
import micronaut.demo.beer.domain.BeerStock;

public class StockEntity {

    private  String name;


    private long bottles;

    private double barrels; // 1 barrel = 327.318 pints
    private double availablePints;


    private double baseBottleCost;
    private double basePintCost;



    private double baseHalfPintCost;

    private double bottleCost;
    private double pintCost;
    private double halfPintCost;


    public StockEntity(String name, long bottles, double barrels, double availablePints, double bottleCost, double pintCost) {
        this.name = name;
        this.bottles = bottles;
        this.barrels = barrels;
        this.availablePints = availablePints;
        this.bottleCost = bottleCost;
        this.pintCost = pintCost;
    }
    public StockEntity(BeerStock stock, BeerCost cost) {
        this.name=stock.getName();
        this.barrels=stock.getBarrels();
        this.bottles=stock.getBottles();
        this.availablePints=stock.getAvailablePints();

        this.bottleCost=cost.getBottleCost();
        this.pintCost=cost.getPintCost();
        this.halfPintCost =this.getPintCost();
        //this.barrels=cost.get

    }

    public StockEntity(BeerStock stock) {
        this.name = stock.getName();
        this.barrels = stock.getBarrels();
        this.bottles = stock.getBottles();
        this.availablePints = stock.getAvailablePints();
    }
    public StockEntity update(BeerCost cost) {
        this.baseBottleCost=cost.getBottleCost();

        this.basePintCost=cost.getPintCost();
        this.baseHalfPintCost =this.getPintCost();
        this.bottleCost=cost.getBottleCost();
        this.pintCost=cost.getPintCost();
        this.halfPintCost =this.getPintCost()/2;

        return this;
    }
    public StockEntity update(BeerCost cost,  Markup markup) {
        this.baseBottleCost=cost.getBottleCost();

        this.basePintCost=cost.getPintCost();
        this.baseHalfPintCost =this.getPintCost();
        this.bottleCost=cost.getBottleCost()*markup.getBottleMarkup();
        this.pintCost=cost.getPintCost()*markup.getPintMarkup();
        this.halfPintCost =this.getPintCost()/2;

        return this;
    }

    public String getName() {
        return name;
    }

    public long getBottles() {
        return bottles;
    }

    public double getBarrels() {
        return barrels;
    }

    public double getAvailablePints() {
        return availablePints;
    }

    public double getBottleCost() {
        return bottleCost;
    }

    public double getPintCost() {
        return pintCost;
    }

    public double getHalfPintCost() {
        return halfPintCost;
    }


    public double getBaseBottleCost() {
        return baseBottleCost;
    }

    public double getBasePintCost() {
        return basePintCost;
    }

    public double getBaseHalfPintCost() {
        return baseHalfPintCost;
    }
}
