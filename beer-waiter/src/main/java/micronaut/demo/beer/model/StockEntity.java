package micronaut.demo.beer.model;

public class StockEntity {

    private  String name;


    private long bottles;

    private double barrels; // 1 barrel = 327.318 pints

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

    private double availablePints;

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

    public StockEntity(BeerStock stock, BeerCost cost, BeerMarkup markup) {
        this.name=stock.getName();
        this.barrels=stock.getBarrels();
        this.bottles=stock.getBottles();
        this.availablePints=stock.getAvailablePints();

        this.bottleCost=cost.getBottleCost()*markup.getBottleMarkup();
        this.pintCost=cost.getPintCost()*markup.getPintMarkup();
        this.halfPintCost =this.getPintCost()/2;
        //this.barrels=cost.get
    }

}
