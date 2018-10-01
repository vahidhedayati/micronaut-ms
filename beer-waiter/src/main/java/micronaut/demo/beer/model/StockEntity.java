package micronaut.demo.beer.model;

public class StockEntity {

    private  String name;


    private long bottles;

    private double barrels; // 1 barrel = 327.318 pints
    private double availablePints;

    private double bottleCost;
    private double pintCost;


    public StockEntity(String name, long bottles, double barrels, double availablePints, double bottleCost, double pintCost) {
        this.name = name;
        this.bottles = bottles;
        this.barrels = barrels;
        this.availablePints = availablePints;
        this.bottleCost = bottleCost;
        this.pintCost = pintCost;
    }


}
